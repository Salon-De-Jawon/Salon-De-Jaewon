document.addEventListener("DOMContentLoaded", function(){
    console.log("안녕 헤어샵 페이지 나야 js");

    let allShops = [];
    let page = 0;
    const size = 10;
    let isLoading = false;
    let endOfList = false;

    let region = "";
    let userLat = null;
    let userLon = null;

    let selectedShops = [];

    function loadSelectedShopsFromSession() {
        const saved = sessionStorage.getItem("selectedShops");
        if (saved) {
            selectedShops = JSON.parse(saved);
        }
    }

    function saveSelectedShopsToSession() {
        sessionStorage.setItem("selectedShops", JSON.stringify(selectedShops));
    }

    loadSelectedShopsFromSession();

    navigator.geolocation.getCurrentPosition(success, error);

    function success(position) {
        userLat = position.coords.latitude;
        userLon = position.coords.longitude;

        console.log("위도: ", userLat, "경도: ", userLon);
        getAddressFromCoords(userLat, userLon);
    }

    function error(err) {
        alert("위치 정보 접근이 거부되었습니다.");
    }

    function getAddressFromCoords(userLat, userLon) {
        fetch(`/api/coord-to-address?x=${userLon}&y=${userLat}`)
            .then(res => res.json())
            .then(data => {
                if(data.userAddress) {
                    region = data.region1depth;
                    document.getElementById("user-region").textContent = `${data.region1depth} ${data.region2depth}`;
                    getShopList();
                } else {
                    console.log("주소 정보 없음");
                }
            })
            .catch(err => console.error("에러: ", err))
    }

    function getShopList() {
        if (isLoading || endOfList) return;
        isLoading = true;

        fetch(`/api/shop-list?region=${region}&lat=${userLat}&lon=${userLon}&page=${page}&size=${size}`)
            .then(res => res.json())
            .then(shopList => {
                if(shopList.length === 0) {
                    endOfList = true;
                    if(page === 0) {
                        document.querySelector("#shop-list").innerHTML = "<p>해당 지역에 등록된 샵이 없습니다.</p>";
                    }
                    return;
                }
                allShops.push(...shopList);
                renderShopList(shopList, true);
                page++;
            })
            .catch(err => console.error("샵 리스트 불러오기 실패: ", err))
            .finally(() => isLoading = false);
    }

    function renderShopList(shopList, append = false) {
        const container = document.querySelector("#shop-list");
        if (!container) return;
        if(!append) container.innerHTML = "";

        shopList.forEach(shop => {
            const card = document.createElement("div");
            card.className = "shop-card";
            card.dataset.id = shop.id;
            card.dataset.name = shop.shopName;

            const couponHtml = shop.hasCoupon ? `<div class="shop-coupon"><img src="/images/coupon.png" alt="쿠폰" /></div>` : "";

            let designersHtml = "";
            if (shop.designerList?.length > 0) {
                designersHtml = shop.designerList.map(d => `
                    <div class="icon-circle">
                        <a href="/designer/${d.designerId}">
                            <img src="${d.imgUrl || '/images/default-profile.png'}" alt="디자이너 이미지" />
                        </a>
                    </div>
                `).join("");
            }

            // 미용실 상세페이지로 이동하는 로직
            card.addEventListener("click", function (e) {
            if(e.target.closest(".select-box")) return;

            location.href = `/shop/${shop.id}`;
            });

            card.innerHTML = `
                <div class="shop-img" style="background-image: url('${shop.shopImageDto?.imgUrl || '/images/default.png'}'); background-size: cover;"></div>
                <div class="shop-info-area">
                    <div class="shop-info">
                        <div class="shop-header">
                            <h3 class="shop-name">${shop.shopName}</h3>
                            ${couponHtml}
                            <div class="select-box ${selectedShops.includes(String(shop.id)) ? 'selected' : ''}"></div>
                        </div>
                        <div class="shop-info-content">
                            <p class="shop-rating">★★★★★ <span class="rating-count">${shop.rating} (${shop.reviewCount})</span></p>
                            <p class="shop-address">${shop.address}</p>
                            <p class="shop-time">${shop.openTime} ~ ${shop.closeTime}</p>
                            <p class="shop-distance">${formatDistance(shop.distance)}</p>
                        </div>
                    </div>
                    <div class="profile-icons-wrapper">
                        <div class="profile-icons">${designersHtml}</div>
                    </div>
                </div>
            `;

            container.appendChild(card);
        });

        document.querySelectorAll(".icon-circle img").forEach(img => {
            if (img.complete) applyClass(img);
            else img.addEventListener("load", () => applyClass(img));
        });

        function applyClass(img) {
            img.classList.toggle("landscape", img.naturalWidth < img.naturalHeight);
        }

        updateSelectedShopUI();
    }

    function formatDistance(distance) {
        return distance >= 1000 ? (distance / 1000).toFixed(1) + "km" : Math.round(distance) + "m";
    }

    window.addEventListener("scroll", () => {
        if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
            getShopList();
        }
    });

    document.addEventListener("click", function (e) {
        if (e.target.classList.contains("select-box")) {
            const shopCard = e.target.closest(".shop-card");
            const shopId = shopCard.dataset.id;

            if (selectedShops.includes(shopId)) {
                selectedShops = selectedShops.filter(id => id !== shopId);
                e.target.classList.remove("selected");
            } else {
                if (selectedShops.length >= 3) {
                    alert("최대 3개의 미용실만 선택할 수 있습니다.");
                    return;
                }
                selectedShops.push(shopId);
                e.target.classList.add("selected");
            }

            saveSelectedShopsToSession();
            updateSelectedShopUI();
        }

        if (e.target.classList.contains("remove-btn")) {
            const parent = e.target.closest(".selected-shop-one, .selected-shop-two, .selected-shop-three");
            const span = parent.querySelector("span");
            const shopToRemove = allShops.find(shop => shop.shopName === span.textContent.trim());

            if (!shopToRemove) return;

            selectedShops = selectedShops.filter(id => id !== String(shopToRemove.id));
            saveSelectedShopsToSession();
            updateSelectedShopUI();

            document.querySelectorAll(".shop-card").forEach(card => {
                if (card.dataset.id === String(shopToRemove.id)) {
                    const selectBox = card.querySelector(".select-box");
                    if (selectBox) selectBox.classList.remove("selected");
                }
            });
        }
    });

    function updateSelectedShopUI() {
        const boxes = [
            document.querySelector(".selected-shop-one"),
            document.querySelector(".selected-shop-two"),
            document.querySelector(".selected-shop-three"),
        ];

        boxes.forEach((box, idx) => {
            const shopId = selectedShops[idx];
            if (shopId) {
                const shop = allShops.find(s => s.id == shopId);
                box.style.display = "flex";
                box.querySelector("span").textContent = shop ? shop.shopName : "알 수 없음";
            } else {
                box.style.display = "none";
            }
        });
    }

    const compareBtn = document.getElementById("compare-btn");
    if (compareBtn) {
        compareBtn.addEventListener("click", () => {
            if (selectedShops.length < 2) {
                alert("비교할 미용실을 2개 이상 선택하세요.");
                return;
            }

            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

            // 서버 세션에 선택된 샵 ID 목록 저장
            fetch("/api/saveSelectedShops", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify(selectedShops)
            })
            .then(res => {
                if (res.ok) {
                    location.href = "/compare";
                } else {
                    alert("서버에 선택 정보 저장 실패");
                }
            })
            .catch(err => {
                console.error("세션 저장 중 오류 발생: ", err);
                alert("오류 발생");
            });
        });
    }
});

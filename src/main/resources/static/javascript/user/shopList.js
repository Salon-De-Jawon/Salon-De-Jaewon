import { initAddressSearchToggle } from '/javascript/user/addressSearchUtil.js ';
import { setStoredLocation, getStoredLocation } from '/javascript/user/locationUtil.js';



document.addEventListener("DOMContentLoaded", function(){
    console.log("안녕 헤어샵 페이지 나야 js");


    const currentUserId = window.currentUserId ?? null;
    const isGuest       = !currentUserId;

    let agreeLocation = false;
    if (!isGuest) {
      const raw = window.userAgreeLocation;        // 0/1, "0"/"1", true/false
      agreeLocation =
            raw === true  || raw === "true" ||
            raw === 1     || raw === "1";
    }

    const guestAgreed = localStorage.getItem("guestLocationConsent") === "true";


    const needLocationConsent = () => (isGuest ? !guestAgreed : !agreeLocation);


    // 위치 동의 모달 요소
    const locationModal = document.querySelector("#location-agree-modal");
    const confirmBtn = document.querySelector("#location-agree-accept");
    const cancelBtn = document.querySelector("#location-agree-cancel");

    // 현재 위치 선택 클릭 시 처리

    document.addEventListener('click', (e) => {
        const btn = e.target.closest('.location-now-text');
        if (!btn) return;

        if (needLocationConsent()) {
          locationModal.classList.remove('hidden');
          locationModal.style.display = 'flex';
        } else {
       detectAndConvertLocation(applyDetectedLocation);
        }
    });


    // 모달 확인 → 동의 처리 및 위치 감지
    confirmBtn?.addEventListener('click', () => {
        locationModal.style.display = 'none';

        if (isGuest) {
          /* 비회원: 로컬스토리지에만 기록 */
          localStorage.setItem('guestLocationConsent', 'true');
        } else {
          /* 회원: 서버 PATCH → DB flag 1 */
          const csrfToken  = document.querySelector('meta[name="_csrf"]')?.content;
          const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

          fetch('/api/member/location-consent', {
            method : 'PATCH',
            headers: { 'Content-Type':'application/json', [csrfHeader]: csrfToken }
          })
            .then(r => { if (!r.ok) throw new Error();  agreeLocation = true; window.userAgreeLocation = true; })
            .catch(() => { alert('동의 처리 실패'); return; });
        }

        detectAndConvertLocation(applyDetectedLocation);
    });

    cancelBtn?.addEventListener("click", () => {
      locationModal.style.display = "none";
    });

    // 위치 감지 및 주소 변환 → 콜백으로 결과 전달
    function detectAndConvertLocation(callback) {
      navigator.geolocation.getCurrentPosition(
        position => {
          const lat = position.coords.latitude;
          const lon = position.coords.longitude;

          fetch(`/api/coord-to-address?x=${lon}&y=${lat}`)
            .then(res => res.json())
            .then(data => {
              if (data.userAddress) {
                callback({
                  lat,
                  lon,
                  userAddress: data.userAddress,
                  region1depth: data.region1depth,
                  region2depth: data.region2depth
                });
              } else {
                alert("주소 정보를 불러올 수 없습니다.");
              }
            })
            .catch(err => {
              alert("주소 변환 실패");
              console.error(err);
            });
        },
        err => {
          alert("위치 정보 접근이 거부되었습니다.");
          console.error(err);
        },
        { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
      );
    }


    // 위치 저장 및 샵 리스트 초기화
    function applyDetectedLocation({ lat, lon, userAddress, region1depth, region2depth }) {
      document.getElementById("user-region").textContent = `${region1depth} ${region2depth}`;
      region = region1depth;
      userLat = lat;
      userLon = lon;
      page = 0;
      endOfList = false;
      allShops = [];

      setStoredLocation(currentUserId, {
        userAddress,
        userLatitude: lat,
        userLongitude: lon,
        region1depth,
        region2depth,
      });

      document.querySelector("#shop-list").innerHTML = "";
      getShopList();

      document.querySelector(".initial-display")?.classList.remove("hidden");
      document.querySelector(".initial-display")?.classList.add("active");
      document.getElementById("expandedShopSearchArea")?.classList.add("hidden");
      document.getElementById("expandedAddressInputArea")?.classList.add("hidden");
    }


    // 주소 검색창으로 수동 저장
    initAddressSearchToggle({
      onSelectAddress: ({ userAddress, userLatitude, userLongitude, region1depth, region2depth }) => {
        console.log("주소 선택됨: ", userAddress, userLatitude, userLongitude, region1depth, region2depth);

        applyDetectedLocation({
          lat: userLatitude,
          lon: userLongitude,
          userAddress,
          region1depth,
          region2depth
        });
      }
    });



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

    //로컬스트리지에 저장 (선택한 지역)
    const storedLocation = getStoredLocation(currentUserId);

    region = storedLocation?.region1depth || "";
    userLat = storedLocation?.userLatitude || null;
    userLon = storedLocation?.userLongitude || null;

    if (storedLocation && region && userLat !== null && userLon !== null) {
      document.getElementById("user-region").textContent = `${storedLocation.region1depth} ${storedLocation.region2depth}`;
      getShopList();
    }


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

            const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

            // 서버 세션에 선택된 샵 ID 목록 저장
            fetch("/api/saveSelectedShops", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [csrfHeader]: csrfToken,
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


    /////////////////////////////////////////////////////////////////////
});

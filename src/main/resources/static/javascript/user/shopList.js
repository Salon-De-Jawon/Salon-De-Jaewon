document.addEventListener("DOMContentLoaded", function(){
    console.log("안녕 헤어샵 페이지 나야 js");

    let allShops=[];
    let page = 0;
    const size = 10;
    let isLoading = false;
    let endOfList = false;

    let region = "";
    let userLat = null;
    let userLon = null;


    // 유저 위치 정보 받아오는 js
    navigator.geolocation.getCurrentPosition(success, error);

    function success(position) {
        userLat = position.coords.latitude;
        userLon = position.coords.longitude;

        console.log("위도: ", userLat, "경도: ", userLon);

        // 유저 위도경도 주소로 변환
        getAddressFromCoords(userLat, userLon);
    }

    function error(err) {
        alert("위치 정보 접근이 거부되었습니다.");
    }

    // 주소 변환 기능

    function getAddressFromCoords(userLat, userLon) {
        fetch(`/api/coord-to-address?x=${userLon}&y=${userLat}`)
            .then(res => res.json())
            .then(data => {
                if(data.userAddress) {
                    console.log("주소: ", data.userAddress);
                    console.log("지역: ", data.region1depth + " " + data.region2depth);
                    region = data.region1depth;


                    const regionText = `${data.region1depth} ${data.region2depth}`;
                    document.getElementById("user-region").textContent = regionText;

                    // 유저가 있는 지역 기준으로 샵 리스트 요청
                    getShopList();
                } else {
                    console.log("주소 정보 없음");
                }
            })
            .catch(err => console.error("에러: ", err))
    }

    // 샵 리스트 불러오기
    function getShopList() {
        if (isLoading || endOfList) return;

        isLoading = true;

        fetch(`/api/shop-list?region=${region}&lat=${userLat}&lon=${userLon}&page=${page}&size=${size}`)
            .then(res => res.json())
            .then(shopList => {
                if(shopList.length === 0) {
                    endOfList = true;
                    if(page===0) {
                        document.querySelector("#shop-list").innerHTML = "<p>해당 지역에 등록된 샵이 없습니다.</p>";
                    }
                    return;
                }

                allShops.push(...shopList);
                renderShopList(shopList, true);
                page++;
            })
            .catch((err) => console.error("샵 리스트 불러오기 실패: ", err))
            .finally(() => isLoading = false);
    }

    // 샵 카드 렌더링
    function renderShopList(shopList, append = false) {
        const container = document.querySelector("#shop-list");
        if (!container) return;

        if(!append) container.innerHTML = "";

        shopList.forEach((shop) => {
            const card = document.createElement("div");
            card.className = "shop-card";

            const couponHtml = shop.hasCoupon
                ? `<div class="shop-coupon"><img src="/images/coupon.png" alt="쿠폰" /></div>`
                : "";

            // 디자이너 이미지 리스트 HTML 구성
            let designersHtml = "";
            if (shop.designerList && shop.designerList.length > 0) {
                designersHtml = shop.designerList
                    .map(designer => `
                        <div class="icon-circle">
                            <a href="/designer/${designer.designerId}">
                                <img src="${designer.imgUrl || '/images/default-profile.png'}" alt="디자이너 이미지" />
                            </a>
                        </div>
                    `)
                    .join("");
            }

            card.innerHTML = `
                <div class="shop-img" style="background-image: url('${(shop.shopImageDto && shop.shopImageDto.imgUrl) ? shop.shopImageDto.imgUrl : '/images/default.png'}'); background-size: cover;"></div>
                <div class="shop-info-area">
                    <div class="shop-info">
                        <div class="shop-header">
                            <h3 class="shop-name">${shop.shopName}</h3>
                            ${couponHtml}
                            <div class="select-box"></div>
                        </div>
                        <div class="shop-info-content">
                            <p class="shop-rating">★★★★★
                              <span class="rating-count">${shop.rating} (${shop.reviewCount})</span></p>
                            <p class="shop-address">${shop.address}</p>
                            <p class="shop-time">${shop.openTime} ~ ${shop.closeTime}</p>
                            <p class="shop-distance">${formatDistance(shop.distance)}</p>
                        </div>
                    </div>
                     <div class="profile-icons-wrapper">
                        <div class="profile-icons">
                            ${designersHtml}
                         </div>
                    </div>
                </div>
            `;



            container.appendChild(card);
        });

        // 가로보다 세로가 길경우 가로 기준으로 이미지 크기 맞추기
        const images = document.querySelectorAll(".icon-circle img");

        images.forEach((img) => {
          if (img.complete) {
            applyClass(img);
          } else {
            img.addEventListener("load", () => applyClass(img));
          }
        });

        function applyClass(img) {
          const width = img.naturalWidth;
          const height = img.naturalHeight;

          // 가로가 세로보다 짧을 경우 (세로가 더 김)
          if (width < height) {
            img.classList.add("landscape");
          } else {
            img.classList.remove("landscape");
          }
        }

    }

    function formatDistance(distance) {
        if (distance >= 1000) {
            return (distance / 1000).toFixed(1) + "km";
        } else {
            return Math.round(distance) + "m";
        }
    }

   window.addEventListener("scroll", () => {
        if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
            getShopList();
        }
   });

    // document end
});
import { initAddressSearchToggle } from '/javascript/user/addressSearchUtil.js ';
import { setStoredLocation, getStoredLocation } from '/javascript/user/locationUtil.js';


document.addEventListener("DOMContentLoaded", () => {

  console.log("메인 js 출근");

  // 메인 화면 갈라지는 거.
  const overlay = document.querySelector(".split-overlay");
  const mapBox = document.querySelector(".main-map");
  const imgUrl = overlay.dataset.img;

  overlay.style.setProperty("--img-url", `url("${imgUrl}")`);
  overlay.style.backgroundImage = "none";
  overlay.style.setProperty("--img-left", `url("${imgUrl}")`);
  overlay.style.setProperty("--img-right", `url("${imgUrl}")`);

  const sheet = document.styleSheets[0];
  sheet.insertRule(`.split-overlay::before{background-image:var(--img-left);}`, sheet.cssRules.length);
  sheet.insertRule(`.split-overlay::after{background-image:var(--img-right);}`, sheet.cssRules.length);

  overlay.addEventListener("click", () => {
    if (overlay.classList.contains("open")) return;
    overlay.classList.add("open");

    mapBox.style.opacity = "1";

    const onEnd = (e) => {
      if (e.propertyName !== "transform") return;
      mapBox.classList.add("front");
      overlay.removeEventListener("transitionend", onEnd);
    };
    overlay.addEventListener("transitionend", onEnd);
  });

  document.addEventListener("click", (e) => {
    if (
      !overlay.classList.contains("open") ||
      overlay.contains(e.target) ||
      mapBox.contains(e.target)
    ) return;

    mapBox.classList.remove("front");
    setTimeout(() => overlay.classList.remove("open"), 0);
  });

  // 지도 초기화
  const mapContainer = document.getElementById("map");
  const mapOption = {
    center: new kakao.maps.LatLng(36.3504119, 127.3845475),
    level: 3,
  };

  navigator.geolocation.getCurrentPosition(success, error, {
    enableHighAccuracy: true,
    timeout: 10000,
    maximumAge: 0
  });

  const map = new kakao.maps.Map(mapContainer, mapOption);


  let allShops = [];
  let markers = [];

  let region = "";
  let userLat = null;
  let userLon = null;
  let page = 0;
  let endOfList = false;




  // 회원 비회원 구분
  const currentUserId = window.currentUserId ?? null;
  const isGuest       = !currentUserId;

  // 위치 정보 동의 초기값
  let agreeLocation = false;

  //로그인 회원 위치정보 동의 여부 확인 및 결과 저장
  if (!isGuest) {
    const raw = window.userAgreeLocation;
    agreeLocation =
          raw === true  || raw === "true" ||
          raw === 1     || raw === "1";
  }

  // 비회원 위치 정보 동의 여부 확인  로컬스트리지에서
  const guestAgreed = localStorage.getItem("guestLocationConsent") === "true";


  // 위치 정보 제공 동의 알림을 띄울 필요가 있는지 없는지
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


  // 모달 확인 ->  동의 처리 및 위치 감지

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


  // 모달 닫기 -> 동의 안하고 닫기.
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


  // 위치 저장
function applyDetectedLocation({ lat, lon, userAddress, region1depth, region2depth }) {
  document.getElementById("user-region").textContent = `${region1depth} ${region2depth}`;
  region = region1depth;
  userLat = lat;
  userLon = lon;

  setStoredLocation(currentUserId, {
    userAddress,
    userLatitude: lat,
    userLongitude: lon,
    region1depth,
    region2depth,
  });

  const moveLatLon = new kakao.maps.LatLng(lat, lon);
  map.setCenter(moveLatLon);

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



    //로컬스트리지에 저장 (선택한 지역)
    const storedLocation = getStoredLocation(currentUserId);

    region = storedLocation?.region1depth || "";
    userLat = storedLocation?.userLatitude || null;
    userLon = storedLocation?.userLongitude || null;


if (storedLocation && region && userLat !== null && userLon !== null) {
  document.getElementById("user-region").textContent = `${storedLocation.region1depth} ${storedLocation.region2depth}`;

  const userCenter = new kakao.maps.LatLng(userLat, userLon);
  map.setCenter(userCenter);

  fetch(`/api/shops?lat=${userLat}&lon=${userLon}`)
    .then(res => res.json())
    .then(data => {
      if (Array.isArray(data)) {
        allShops = data;
        updateMarkersInViewport();
      } else {
        console.error("미용실 리스트가 배열이 아님:", data);
      }
    })
    .catch(err => console.error("미용실 리스트 가져오기 실패:", err));
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
//                    getShopList();
                } else {
                    console.log("주소 정보 없음");
                }
            })
            .catch(err => console.error("에러: ", err))
    }

function success(position) {
  userLat = position.coords.latitude;
  userLon = position.coords.longitude;

  const moveLatLon = new kakao.maps.LatLng(userLat, userLon);
  map.setCenter(moveLatLon);

  getAddressFromCoords(userLat, userLon);

  fetch(`/api/shops?lat=${userLat}&lon=${userLon}`)
    .then(res => res.json())
    .then(data => {
      if (Array.isArray(data)) {
        allShops = data;
        updateMarkersInViewport();
      } else {
        console.error("미용실 리스트가 배열이 아님:", data);
      }
    })
    .catch(err => console.error("미용실 리스트 가져오기 실패:", err));

  kakao.maps.event.addListener(map, 'idle', updateMarkersInViewport);
}

  function error(err) {
    alert("위치 정보를 불러올 수 없습니다.");
    console.error(err);
  }

  function updateMarkersInViewport() {
    const bounds = map.getBounds();
    const sw = bounds.getSouthWest();
    const ne = bounds.getNorthEast();
    const visibleShops = allShops.filter(shop => {
      const lat = shop.latitude;
      const lng = shop.longitude;
      return lat >= sw.getLat() && lat <= ne.getLat() && lng >= sw.getLng() && lng <= ne.getLng();
    });
    clearMarkers();
    displayMarkers(visibleShops);
  }


  function displayMarkers(shopList) {
    shopList.forEach(shop => {
      const position = new kakao.maps.LatLng(shop.latitude, shop.longitude);
      const marker = new kakao.maps.Marker({ map, position });
      const infowindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:5px; font-size:14px;">${shop.shopName}</div>`
      });
      kakao.maps.event.addListener(marker, 'mouseover', () => infowindow.open(map, marker));
      kakao.maps.event.addListener(marker, 'mouseout', () => infowindow.close());
      kakao.maps.event.addListener(marker, 'click', () => {
        window.location.href = `/shop/${shop.id}`;
      });
      markers.push(marker);
    });
  }

  function clearMarkers() {
    markers.forEach(marker => marker.setMap(null));
    markers = [];
  }


if (storedLocation?.region1depth) {
  fetch(`/api/main-banners?region=${encodeURIComponent(storedLocation.region1depth)}`)
    .then(res => res.json())
    .then(banners => {
      console.log("🎯 받아온 배너 목록:", banners); // ✅ 여기 콘솔 찍기

      if (Array.isArray(banners) && banners.length > 0) {
        const selected = getRandomItems(banners, 5);
        renderAdBannerSlides(selected);
      } else {
        console.warn("📭 배너 없음 또는 배열 아님");
      }
    })
    .catch(err => {
      console.error("❌ 배너 로딩 실패:", err);
    });
}

// ✅ 랜덤 n개 뽑기
function getRandomItems(arr, count) {
  const shuffled = arr.slice().sort(() => 0.5 - Math.random());
  return shuffled.slice(0, count);
}

// ✅ 슬라이드에 배너 렌더링
function renderAdBannerSlides(banners) {
const wrapper = document.querySelector(".ad-banner-swiper .swiper-wrapper");
  if (!wrapper) return;

  wrapper.innerHTML = ""; // 기존 정적 배너 제거

  banners.forEach(banner => {
    const slide = document.createElement("div");
    slide.className = "swiper-slide";
    slide.innerHTML = `
      <a href="/shop/${banner.shopId}">
        <img src="${banner.imgUrl}" alt="배너 이미지" style="width:100%; border-radius:12px;" />
      </a>
    `;
    wrapper.appendChild(slide);
  });

    if (window.adBannerSwiper) {
      window.adBannerSwiper.update();
    }
}

//추천 샵 로딩

if (storedLocation?.region1depth) {
  fetch(`/api/recommend-shops?region=${encodeURIComponent(storedLocation.region1depth)}`)
    .then(res => res.json())
    .then(shops => {
      console.log("추천 샵 목록:", shops);
      if (Array.isArray(shops) && shops.length > 0) {
        renderRecommendShopSlides(shops);
      } else {
        console.warn("추천 샵 없음 또는 형식 오류");
      }
    })
    .catch(err => {
      console.error("추천 샵 로딩 실패:", err);
    });
}

// 샵 추천 랜더링


function renderRecommendShopSlides(shopList) {
  const wrapper = document.querySelector(".shopSwiper .shop-slider");
  if (!wrapper) return;

  wrapper.innerHTML = ""; // 기존 항목 제거

  shopList.forEach(shop => {
    const slide = document.createElement("div");
    slide.className = "swiper-slide";

    slide.innerHTML = `
      <div class="shop-content" onclick="location.href='/shop/${shop.id}'">
          <div class="skew-box"><img src="${shop.shopImageDto?.imgUrl || '/images/default.png'}"
                                     alt="샵 이미지"
                                     class="img-fit"
                                     style="width: 100%; border-radius: 12px; margin-top: 6px;" /></div>
          <div class="shop-info">
              <div class="shop-info-detail">
                  <div class="shop-name">${shop.shopName}</div>
                  ${shop.distance != null ? `<div class="shop-distance">${shop.distance}km</div>` : ""}
              </div>
              <div class="shop-review-detail">
                  <img src="/images/pointed-star.png" alt="별점" />
                  <div class="shop-rating">${shop.avgRating}</div>
                  <img src="/images/comment.png" alt="리뷰" />
                  <div class="shop-review-count">${shop.reviewCount.toLocaleString()}</div>
              </div>
          </div>
      </div>
    `;

    wrapper.appendChild(slide);
  });

  if (window.shopSwiper) {
    window.shopSwiper.update();
  }

    adjustImageFitAll('.shopSwiper img.img-fit', 4 / 3);
}


//document end

});

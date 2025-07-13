import { initAddressSearchToggle } from './user/addressSearchUtil.js';
import { setStoredLocation, getStoredLocation } from './user/locationUtil.js';


document.addEventListener("DOMContentLoaded", function () {
  console.log("나 메인 js 잡히다");

    // 위치 정보

        const currentUserId = (window.currentUserId !== undefined && window.currentUserId !== null)
          ? window.currentUserId
          : null;
        const agreeLocation = window.userAgreeLocation !== undefined && window.userAgreeLocation !== null
          ? window.userAgreeLocation
          : false;
        const isGuest = !currentUserId;

        // 비회원 위치 동의 여부 확인
        const guestAgreed = localStorage.getItem("guestLocationConsent") === "true";

        // 위치 동의 모달 요소
        const locationModal = document.querySelector("#location-agree-modal");
        console.log("모달 요소", locationModal);
        const confirmBtn = document.querySelector("#location-agree-accept");
        const cancelBtn = document.querySelector("#location-agree-cancel");

        // 현재 위치 선택 클릭 시 처리
        // document 전체에 클릭 이벤트 위임
        document.addEventListener("click", (e) => {
          const locationNowText = e.target.closest(".location-now-text");
          console.log("클릭 되었어");
          if (!locationNowText) return;

          if (isGuest) {
            if (!guestAgreed) {
              locationModal.classList.remove("hidden");
              locationModal.style.display = "flex";
            } else {
              requestAndSetCurrentLocation();
            }
          } else {
            if (!agreeLocation) {
              locationModal.classList.remove("hidden");
              locationModal.style.display = "flex";
            } else {
              requestAndSetCurrentLocation();
            }
          }
        });


        // 모달 확인 → 동의 처리 및 위치 감지
        confirmBtn?.addEventListener("click", () => {
          locationModal.style.display = "none";

          if (isGuest) {
            localStorage.setItem("guestLocationConsent", "true");
            requestAndSetCurrentLocation();
          } else {
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

            fetch("/api/member/location-consent", {
              method: "PATCH",
              headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken,
              },
            })
              .then(res => {
                if (!res.ok) throw new Error("위치 제공 동의 실패");
                requestAndSetCurrentLocation();
              })
              .catch(err => {
                alert("동의 처리 중 오류가 발생했습니다.");
                console.error(err);
              });
          }
        });

        cancelBtn?.addEventListener("click", () => {
          locationModal.style.display = "none";
        });


      // 위치 감지 및 저장 후 shop 리스트 가져오기
      function requestAndSetCurrentLocation() {
        navigator.geolocation.getCurrentPosition(
          position => {
            const lat = position.coords.latitude;
            const lon = position.coords.longitude;

            fetch(`/api/coord-to-address?x=${lon}&y=${lat}`)
              .then(res => res.json())
              .then(data => {
                if (data.userAddress) {
                  document.getElementById("user-region").textContent = `${data.region1depth} ${data.region2depth}`;
                  region = data.region1depth;
                  userLat = lat;
                  userLon = lon;
                  page = 0;
                  endOfList = false;
                  allShops = [];
                  setStoredLocation(currentUserId, {
                    userAddress: data.userAddress,
                    userLatitude: lat,
                    userLongitude: lon,
                    region1depth: data.region1depth,
                    region2depth: data.region2depth,
                  });
                  document.querySelector("#shop-list").innerHTML = "";
                  getShopList();

                  document.querySelector(".initial-display")?.classList.remove("hidden");
                  document.querySelector(".initial-display")?.classList.add("active");

                  document.getElementById("expandedShopSearchArea")?.classList.add("hidden");
                  document.getElementById("expandedAddressInputArea")?.classList.add("hidden");
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



    // 주소 검색창으로 수동 저장
    initAddressSearchToggle({
      onSelectAddress: ({ userAddress, userLatitude, userLongitude, region1depth, region2depth }) => {
        console.log("주소 선택됨: ", userAddress, userLatitude, userLongitude, region1depth, region2depth);

        // 저장 추가
        setStoredLocation(currentUserId, {
          userAddress,
          userLatitude,
          userLongitude,
          region1depth,
          region2depth,
        });

        userLat = userLatitude;
        userLon = userLongitude;
        region = region1depth;

        //텍스트 즉시 갱신
         document.getElementById("user-region").textContent = `${region1depth} ${region2depth}`;

        page = 0;
        endOfList = false;
        allShops = [];
        document.querySelector("#shop-list").innerHTML = "";
        getShopList();
      }
    });


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



    // 지도 그리는 js
    var mapContainer = document.getElementById('map'), // 지도를 표시할 div
      mapOption = {
          center: new kakao.maps.LatLng(36.3504119, 127.3845475), // 지도의 중심좌표(대전시청)
          level: 3 // 지도의 확대 레벨
      };
    var map = new kakao.maps.Map(mapContainer, mapOption);

    // 마커

    let allShops = [];
    let markers = [];

    navigator.geolocation.getCurrentPosition(success, error, {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0
    });

    function success(position) {
      const userLat = position.coords.latitude;
      const userLon= position.coords.longitude;
      console.log("위도:", userLat, "경도:", userLon);

      // 사용자 위치로 지도 중심 이동
      const moveLatLon = new kakao.maps.LatLng(userLat, userLon);
      map.setCenter(moveLatLon);

      getAddressFromCoords(userLat, userLon);


      // 미용실 리스트 받아옴
      fetch(`/api/shops?lat=${userLat}&lon=${userLon}`)
        .then(res => res.json())
        .then(data => {
            if (Array.isArray(data)) {
                allShops = data;
                updateMarkersInViewport();  // 처음 로드 시 표시
            } else {
                console.error("미용실 리스트가 배열이 아님:", data);
            }
        })
        .catch(err => console.error("미용실 리스트 가져오기 실패:", err));

      // 지도 이벤트 등록

      kakao.maps.event.addListener(map, 'idle', function() {
        updateMarkersInViewport();
      });
    }

    function error(err) {
      alert("위치 정보 접근이 거부되었습니다.");
    }

    // 마커 표시 이벤트 위에 지도이벤트 등록이랑 연결
    function updateMarkersInViewport() {
        const bounds = map.getBounds();
        const sw = bounds.getSouthWest();
        const ne = bounds.getNorthEast();

        // 지도에 들어오는 미용실

        const visibleShops = allShops.filter(shop => {
            const lat = shop.latitude;
            const lng = shop.longitude;
            return lat >= sw.getLat() && lat <= ne.getLat() && lng >= sw.getLng() && lng <= ne.getLng();
        });

        clearMarkers();  // 기존 마커 제거
        displayMarkers(visibleShops);  // 새로 표시
    }

    // 마커 생성 기능
    function displayMarkers(shopList) {
        shopList.forEach(shop => {
            const position = new kakao.maps.LatLng(shop.latitude, shop.longitude);
            const marker = new kakao.maps.Marker({
                map: map,
                position: position
            });

            const infowindow = new kakao.maps.InfoWindow({
                content: `<div style = "padding:5px; font-size:14px;">${shop.shopName}</div>`
            });

            kakao.maps.event.addListener(marker, 'mouseover', () => infowindow.open(map, marker));
            kakao.maps.event.addListener(marker, 'mouseout', () => infowindow.close());

            //마커 클릭시 상세 페이지 이동
            kakao.maps.event.addListener(marker, 'click', ()=> {
                window.location.href=`/shop/${shop.id}`;   // 샵 페이지로 이동하는 주소!!!
            });

            markers.push(marker);
        });
    }

    // 마커 제거 기능

    function clearMarkers() {
        markers.forEach(marker => marker.setMap(null));
        markers = [];
    }

//    function getAddressFromCoords(userLat, userLon) {
//        fetch(`/api/coord-to-address?x=${userLon}&y=${userLat}`)
//            .then(res => res.json())
//            .then(data => {
//            if (data.userAddress) {
//                console.log("주소: ", data.userAddress);
//                console.log("지역: ", data.region1depth + " " + data.region2depth);
//
//                const regionText = `${data.region1depth} ${data.region2depth}`;
//                document.getElementById("user-region").textContent = regionText;
//
//            } else {
//                console.log("주소 정보 없음");
//            }
//            })
//            .catch(err => console.error("에러:", err))
//    }
//
//    function error(err) {
//      alert("위치 정보를 불러올 수 없습니다.");
//      console.error(err);
//    }


    new Swiper(".ad-banner-swiper", {
      slidesPerView: 1.3,
      spaceBetween: 30,
      centeredSlides: true,
      loop: true,
      autoplay: {
        delay: 8000,
        disableOnInteraction: false,
      },
    });

    new Swiper(".shopSwiper", {
      loop: true,
      slidesPerView: "auto",
      centeredSlides: true,
      spaceBetween: 16,
      grabCursor: true,
    });


///////////////////////////////////////////////////////////

  // DOMContentLoaded 끝
});

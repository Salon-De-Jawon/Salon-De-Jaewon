document.addEventListener("DOMContentLoaded", function () {
  console.log("나 메인 js 잡히다");

    function openSplit() {
      const split = document.querySelector(".split-box");
      const map = document.querySelector(".main-map");

      split.classList.add("open");

      setTimeout(() => {
        map.classList.add("show");
      }, 400);
    }

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

    navigator.geolocation.getCurrentPosition(success, error);

    function success(position) {
      const latitude = position.coords.latitude;
      const longitude = position.coords.longitude;
      console.log("위도:", latitude, "경도:", longitude);

      getAddressFromCoords(latitude, longitude);
    }

    function error(err) {
      alert("위치 정보 접근이 거부되었습니다.");
    }


  // DOMContentLoaded 끝
});

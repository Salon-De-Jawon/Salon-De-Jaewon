 document.addEventListener("DOMContentLoaded", function () {
    const loginHamburger = document.getElementById("loginHamburger");
    const sidebar = document.getElementById("sidebar");
    const sidebarClose = document.querySelector(".sidebar-close");

    console.log("✅ loginHamburger:", loginHamburger); // ✅ 이 위치가 정확함

    if (loginHamburger) {
      loginHamburger.addEventListener("click", () => {
        console.log("햄버거 클릭됨");
        sidebar.classList.add("active");
      });
    }

    if (sidebarClose) {
      sidebarClose.addEventListener("click", () => {
        sidebar.classList.remove("active");
      });
    }

    document.addEventListener("click", (e) => {
      if (
        sidebar.classList.contains("active") &&
        !sidebar.contains(e.target) &&
        !loginHamburger.contains(e.target)
      ) {
        sidebar.classList.remove("active");
      }
    });

    // 헤더 클릭하면 세션스토리지에 있는 정보를 세션에 저장하기.
    const headerCompareLink = document.getElementById("compare-header-link");

      if (headerCompareLink) {
        headerCompareLink.addEventListener("click", function (e) {
          e.preventDefault();

          // sessionStorage에서 선택된 샵 ID 불러오기
          const selected = sessionStorage.getItem("selectedShops");
          const selectedShops = selected ? JSON.parse(selected) : [];

          if (selectedShops.length < 2) {
            alert("비교할 미용실을 2개 이상 선택하세요.");
            return;
          }

          // 서버 세션에 저장 요청 후 compare로 이동
          fetch("/api/saveSelectedShops", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify(selectedShops),
          })
            .then(() => {
              location.href = "/compare";
            })
            .catch(err => {
              console.error("세션 저장 실패", err);
              alert("비교 페이지 이동 중 오류가 발생했습니다.");
            });
        });
      }
  });
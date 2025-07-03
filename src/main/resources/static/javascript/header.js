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
  });
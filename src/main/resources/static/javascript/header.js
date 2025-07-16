 document.addEventListener("DOMContentLoaded", function () {
     const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
     const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
     const csrfToken = csrfTokenMeta ? csrfTokenMeta.getAttribute('content') : null;
     const csrfHeader = csrfHeaderMeta ? csrfHeaderMeta.getAttribute('content') : null;


    const loginHamburger = document.getElementById("loginHamburger");
    const sidebar = document.getElementById("sidebar");
    const sidebarClose = document.querySelector(".sidebar-close");

    console.log("loginHamburger:", loginHamburger);

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
        !(loginHamburger && loginHamburger.contains(e.target))
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
              [csrfHeader]: csrfToken,
            },
            body: JSON.stringify(selectedShops),
          })
            .then(response => {
              if (!response.ok) {
                throw new Error("서버 응답 실패");
              }
              // 세션 저장 성공 후 이동
              location.href = "/compare";
            })
            .catch(err => {
              console.error("세션 저장 실패", err);
              alert("비교 페이지 이동 중 오류가 발생했습니다.");
            });
        });
      }


        const badgeEl   = document.getElementById("notification-badge");  // 헤더에 이미 있는 배지 span
        const userId    = window.currentUserId || null;                    // 컨트롤러가 내려준 JS 전역
        const initCnt   = Number(badgeEl?.dataset.count || 0);             // data-count 속성

        function showBadge(cnt){
            if(!badgeEl) return;
            if(cnt>0){
                badgeEl.style.display='flex';
                badgeEl.textContent  = cnt;
                badgeEl.dataset.count= cnt;
            }else{
                badgeEl.style.display='none';
                badgeEl.dataset.count= 0;
            }
        }
        showBadge(initCnt);   // 최초 표시

        // 알림 클릭 처리
        const container = document.getElementById("sidebar-alert-container");
          if (container) {
            container.addEventListener("click", function (e) {
              const card = e.target.closest(".sidebar-alert");
              if (!card) return;

              const target = card.dataset.target;
              const id     = card.dataset.id;

              fetch("/api/notification/read", {
                method: "POST",
                headers: {
                  "Content-Type": "application/json",
                  [csrfHeader]: csrfToken,
                },
                body: JSON.stringify({
                  webTarget: target,
                  targetId : id
                })
              })
              .then(res => {
                if (!res.ok) throw new Error("HTTP " + res.status);

                if (["DESAPPLY", "SHOPAPPLY", "BANNER"].includes(target)) {
                  card.remove();
                  const remain = container.querySelectorAll(".sidebar-alert").length;
                  showBadge(remain);
                }
              })
              .catch(err => console.error("읽음 처리 실패", err));
            });
          }

        // WebSocket 구독으로 실시간 갱신
        if(userId){
            const sock  = new SockJS('/ws');
            const stomp = Stomp.over(sock);
            stomp.connect({}, ()=>{
                stomp.subscribe(`/topic/notify/${userId}`, msg=>{
                    const { unreadTotal } = JSON.parse(msg.body); // 서비스에서 넣어준 값
                    const data = JSON.parse(msg.body);
                    showBadge(data.unreadTotal);
                    addAlertCard(data);
                });
            });
        }


      function addAlertCard(data) {
          const container = document.getElementById("sidebar-alert-container");
          if (!container) return;

          const card = document.createElement("div");
          card.className = "sidebar-alert";
          card.dataset.target = data.webTarget;
          card.dataset.id = data.targetId;

          const content = document.createElement("span");
          content.className = "alert-content";
          content.textContent = data.message;

          const date = document.createElement("span");
          date.className = "alert-date";
          date.textContent = data.createAt || "방금 전";

          card.append(content, date);
          container.prepend(card);

          // 최대 3개 유지
          while (container.children.length > 3) {
            container.removeChild(container.lastChild);
          }
        }

  });
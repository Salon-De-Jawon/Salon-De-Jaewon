document.addEventListener("DOMContentLoaded", () => {

  const csrfToken  = document.querySelector('meta[name="_csrf"]')?.content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

  // 1️⃣ 변수 이름 통일
  const userId = window.currentUserId;     // ← 여기만 바꿔주면 끝

  if (!userId) {
    console.warn("currentUserId 없음, 알림 기능 건너뜀");
    return;
  }

  /* ---------- WebSocket ---------- */
  const sock   = new SockJS("/ws");
  const stomp  = Stomp.over(sock);

  stomp.connect({}, () => {
    console.log("웹소켓 연결 성공");

    stomp.subscribe(`/topic/notify/${userId}`, (msg) => {
      const data = JSON.parse(msg.body);   // { message, webTarget, targetId, unreadTotal }

      /* 2️⃣ 배지 숫자를 서버가 보내준 unreadTotal 로 갱신 */
      const badge = document.getElementById("notification-badge");
      if (badge) {
        if (data.unreadTotal > 0) {
          badge.textContent  = data.unreadTotal;
          badge.dataset.count= data.unreadTotal;
          badge.style.display = "inline-block";
        } else {
          badge.style.display = "none";
          badge.dataset.count = 0;
        }
      }

      /* ------ 사이드바 알림 카드 렌더링 ------ */
      const container = document.getElementById("sidebar-alert-container");
      if (!container) return;

      const alertDiv = document.createElement("div");
      alertDiv.className = "sidebar-alert";
      alertDiv.textContent = data.message;

      alertDiv.addEventListener("click", () => {
        // 읽음 처리
        fetch("/api/notification/read", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            [csrfHeader]: csrfToken,
          },
          body: JSON.stringify({
            webTarget: data.webTarget,
            targetId: data.targetId
          })
        })
        .catch(err => console.error("읽음 처리 실패", err));
        /* 필요 시 페이지 이동 로직 */
      });

      container.prepend(alertDiv);
      while (container.children.length > 3) container.removeChild(container.lastChild);
    });
  }, err => console.error("웹소켓 연결 실패:", err));
});

document.addEventListener("DOMContentLoaded", function() {

    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
    const csrfToken = csrfTokenMeta?.getAttribute('content');
    const csrfHeader = csrfHeaderMeta?.getAttribute('content');

    const memberId = window.currentMemberId;

    if (!memberId) {
        console.warn("memberId 없음, 종료");
        return;
    }

    // 웹소켓 연결

    const socket = new SockJS("/ws");
    const stompClient = Stomp.over(socket);

    // 발생한 이벤트를 알림으로 연결
    stompClient.connect({}, () => {
        console.log("웹소켓 연결 성공");

        stompClient.subscribe("/topic/notify/" + memberId, (message) => {
            const data = JSON.parse(message.body);
            console.log("실시간 알림 수신됨:", data);

            // 알림 뱃지 업데이트
            const badge = document.getElementById("notification-badge");
            let currentCount = parseInt(badge.textContent || "0");
            badge.textContent = currentCount + 1;
            badge.style.display = "inline-block";

            // 알림 메시지 생성
            const container = document.getElementById("sidebar-alert-container");

            const alertDiv = document.createElement("div");
            alertDiv.className = "sidebar-alert";
            alertDiv.textContent = data.message;

            alertDiv.onclick = function () {
                fetch(`/api/notification/read`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken,
                    },
                    body: JSON.stringify({
                        webTarget: data.webTarget,
                        targetId: data.targetId
                    })
                }).then(() => {
                        console.log("읽음 처리 완료");
                }).catch(err => {
                    console.error("읽음 처리 실패", err);
                });

                // 페이지 이동
//                let link = "#";
//                if (data.webTarget === "REVIEW") {
//                    link = `/review/${data.targetId}`;
//                } else if (data.webTarget === "DESAPPLY") {
//                    link = `/apply/detail/${data.targetId}`;
//                }
//
//                location.href = link;
            };

            container.prepend(alertDiv);

            // 최대 3개까지만 유지
            while (container.children.length > 3) {
                container.removeChild(container.lastChild);
            }
        });
    }, (error) => {
        console.error("웹소켓 연결 실패:", error);
    });

});

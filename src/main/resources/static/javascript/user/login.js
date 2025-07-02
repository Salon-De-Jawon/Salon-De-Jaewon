document.addEventListener("DOMContentLoaded", () => {

    // 로그인 인풋 클리어 기능
    const clearButtons = document.querySelectorAll(".clear-btn");

          clearButtons.forEach((btn) => {
            const input = btn.parentElement.querySelector("input");

            // 초기 상태: 값이 있으면 보여주고 없으면 숨김
            if (input.value === "") {
              btn.style.display = "none";
            } else {
              btn.style.display = "inline";
            }

            // x 버튼 클릭 시 input 비우고 버튼 숨김
            btn.addEventListener("click", () => {
              input.value = "";
              input.focus();
              btn.style.display = "none";
            });

            // input 입력 시 버튼 다시 표시
            input.addEventListener("input", () => {
              if (input.value === "") {
                btn.style.display = "none";
              } else {
                btn.style.display = "inline";
              }
            });
          });

    // find id modal start

    const findModal = document.getElementById("findIdModal");
    const step1 = document.getElementById("findIdStep1");
    const step2 = document.getElementById("findIdStep2");

    const emailInput = document.getElementById("emailInput");
    const emailMsg = document.getElementById("chkEmailMsg");

    const codeInput = document.getElementById("emailCodeInput");
    const codeMsg = document.getElementById("chkCodeMsg");

    const sendBtn = document.getElementById("sendChkNum");
    const verifyBtn = document.getElementById("chkNumCheck");
    const nextBtn = document.getElementById("nextBtn");

   const csrfToken = document.getElementById("csrfToken").value;
   const csrfHeader = document.getElementById("csrfHeader").value;


    document.querySelector(".find-id").addEventListener("click", openFindIdModal);

    function openFindIdModal() {
        findIdModal.style.display = "block";
        resetFindIdModal();
    }

    window.closeFindIdModal = function () {
        findIdModal.style.display= "none";
    };

    function resetFindIdModal() {
        step1.style.display = "block";
        step2.style.display = "none";
        emailInput.value = "";
        codeInput.value = "";
        emailMsg.style.display = "none";
        codeMsg.style.display = "none";
        nextBtn.disabled = true;
    }

    sendBtn.addEventListener("click", async function(e) {
        e.preventDefault();

        const email = emailInput.value.trim();
        emailMsg.style.display = "none";

        if(!email) {
            emailMsg.textContent = "이메일을 입력하세요";
            emailMsg.style.display = "block";
            return;
        }

        try {
            const res = await fetch("/auth/email/check", {
                method: "post",
                headers: {"Content-Type": "application/json",
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({email})
            });

            const result = await res.json();

            if(!result.exists) {
                emailMsg.textContent = "일치하는 이메일이 없습니다";
                emailMsg.style.color = "red";
                emailMsg.style.display = "block";
                return;
            }

            emailMsg.textContent = "인증번호가 전송되었습니다.";
            emailMsg.style.color = "green";
            emailMsg.style.display = "block";
            codeInput.focus();

            fetch("/auth/email/send", {
              method: "POST",
              headers: {
                "Content-Type" : "application/json",
                [csrfHeader]: csrfToken
              },
              body: JSON.stringify({email, context: "find"})
            })
              .then(res => res.json())
              .then(data => {
                if(data.status !== "sent") {
                    emailMsg.textContent = "이메일 전송에 실패했습니다";
                    emailMsg.style.color = "red";
                    }
              })
              .catch(() => {
                emailMsg.textContent = "서버 오류가 발생했습니다.";
                emailMsg.style.color = "red";
              });

        } catch (err) {
            emailMsg.textContent = "서버 오류가 발생했습니다.";
            emailMsg.style.color = "red";
            emailMsg.style.display = "block";
        }
    });

// check send number

    verifyBtn.addEventListener("click", function() {
        const code = codeInput.value.trim();
        codeMsg.style.display = "none";

        if(!code) {
            codeMsg.textContent = "인증번호를 입력해주세요.";
            codeMsg.style.color = "red";
            codeMsg.style.display = "block";
            return;
        }

        fetch("/auth/email/verify", {
            method:"POST",
            headers: {"Content-Type":"application/json"},
            credentials: "include",
            body: JSON.stringify({code})
        })
            .then(res => res.json())
            .then(data => {
                if(data.success) {
                    codeMsg.textContent="인증되었습니다.";
                    codeMsg.style.color = "green";
                    nextBtn.disabled = false;
                } else {
                    codeMsg.textContent = "인증번호가 일치하지 않습니다";
                    codeMsg.style.color = "red";
                    nextBtn.disabled=true;
                }
                codeMsg.style.display = "block";
            })
            .catch(() => {
               codeMsg.textContent = "서버 오류가 발생했습니다.";
               codeMsg.style.color = "red";
                codeMsg.style.display = "block";
                nextBtn.disabled = true;
            });
    });

//    step2

    window.goToIdPrintStep = async function () {
        step1.style.display = "none";
        step2.style.display = "block";

        try {
         const res = await fetch("/auth/email/find-id", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },
            credentials: "include"
         });

         const data = await res.json();

         if (data.loginId) {
            document.getElementById("foundId").textContent = data.loginId;
         } else {
            document.getElementById("foundId").textContent = "알 수 없음";
         }
       } catch (err) {
            document.getElementById("foundId").textContent = "서버 오류";
       }
    };


// document end
});
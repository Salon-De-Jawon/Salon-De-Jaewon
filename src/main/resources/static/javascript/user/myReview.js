import { autoBindDragScroll } from '/javascript/dragScroll.js';

document.addEventListener("DOMContentLoaded", () => {
        autoBindDragScroll();
        const reviewBoxes = document.querySelectorAll(".review-box");

        reviewBoxes.forEach((box) => {
          const images = box.querySelectorAll("img");
          let currentImageIndex = 0;
          let intervalId;

          // --- 이미지 없는 경우 처리 ---
          if (images.length === 0) {
            const rating = box.dataset.rating; // data-rating 값 가져오기
            const date = box.dataset.date; // data-date 값 가져오기
            const hairstyle = box.dataset.hairstyle; // data-hairstyle 값 가져오기

            if (rating && date && hairstyle) {
              // 캔버스 이미지 생성 함수 호출
              const generatedImageUrl = createReviewImage(
                rating,
                date,
                hairstyle
              );

              const imgElement = document.createElement("img");
              imgElement.src = generatedImageUrl;
              imgElement.alt = "리뷰 정보 이미지";
              imgElement.classList.add("active");
              box.appendChild(imgElement);

              return;
            }
          }
          // --- 이미지 없는 경우 처리 끝 ---

          if (images.length > 0) {
            images[0].classList.add("active");
          }

          const showNextImage = () => {
            // 현재 활성화된 이미지 숨기기
            images[currentImageIndex].classList.remove("active");

            // 다음 이미지 인덱스 계산 (무한 순환)
            currentImageIndex = (currentImageIndex + 1) % images.length;

            // 다음 이미지 활성화
            images[currentImageIndex].classList.add("active");
          };

          // 마우스가 박스 위에 올라갔을 때
          box.addEventListener("mouseenter", () => {
            // 이미지가 1개 이하면 전환할 필요 없음
            if (images.length <= 1) return;

            // 1.5초마다 이미지 전환 시작
            intervalId = setInterval(showNextImage, 2000); // 1.5초
          });

          // 마우스가 박스에서 벗어났을 때
          box.addEventListener("mouseleave", () => {
            // 이미지 전환 중지
            clearInterval(intervalId);

            // 첫 번째 이미지로 되돌리기 (선택 사항)
            // 모든 이미지 숨기기
            images.forEach((img) => img.classList.remove("active"));
            // 첫 번째 이미지 다시 활성화
            if (images.length > 0) {
              images[0].classList.add("active");
              currentImageIndex = 0; // 인덱스 초기화
            }
          });
        });
});

// --- 캔버스 이미지 생성 함수 ---
     function createReviewImage(rating, date, hairstyle) {
       const canvas = document.createElement("canvas");

       const width = 300;
       const height = 400;
       canvas.width = width;
       canvas.height = height;
       const ctx = canvas.getContext("2d");

       ctx.fillStyle = "#f8f8f8";
       ctx.fillRect(0, 0, width, height);

       ctx.fillStyle = "#333";
       ctx.textAlign = "center";

       const starText = "★".repeat(parseInt(rating));
       ctx.font = "bold 40px Arial";
       ctx.fillText(starText, width / 2, height * 0.35);

       ctx.font = "24px Arial";
       ctx.fillText(`- ${date} -`, width / 2, height * 0.5);

       ctx.font = "bold 28px Arial";
       ctx.fillText(hairstyle, width / 2, height * 0.65);

       return canvas.toDataURL("image/jpeg", 0.9);
     }

     //모달 열고 닫기

     const modal = document.getElementById("review-details");

     /* 리뷰 박스를 클릭하면 모달 열기 */
     document.querySelectorAll(".review-box").forEach((box) => {
       box.addEventListener("click", () => {
         modal.classList.add("show"); // overlay ON
       });
     });

     /* X 버튼으로 닫기 */
     function closeReviewDetails() {
       modal.classList.remove("show"); // overlay OFF
     }

     window.closeReviewDetails = closeReviewDetails;
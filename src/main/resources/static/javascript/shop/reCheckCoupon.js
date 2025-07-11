// 예약 확인 섹션
function handleChangeReservation() {
  const confirmChange = confirm("정말로 예약을 변경하시겠습니까?");
  if (confirmChange) {
    window.location.href = "/reservation-edit.html"; // 변경페이지로 이동
  }
}

// 고객 요청사항 섹션
document.addEventListener("DOMContentLoaded", function () {
  const commentField = document.getElementById("customerComment");
  const saveBtn = document.getElementById("commentSaveBtn");

  if (commentField && saveBtn) {
    saveBtn.addEventListener("click", function () {
      const isEditable = !commentField.readOnly;

      if (isEditable) {
        // 저장 처리 위치 👇
        commentField.readOnly = true;
        commentField.style.backgroundColor = "#f5f5f5"; // 회색 배경
        commentField.style.cursor = "default";

        saveBtn.textContent = "수정";
      } else {
        commentField.readOnly = false;
        commentField.style.backgroundColor = "#fff"; // 원래 배경 복원
        commentField.style.cursor = "text";

        commentField.focus();
        saveBtn.textContent = "저장";
      }
    });
  }
});
// 쿠폰 선택 섹션

// 샘플 쿠폰 데이터
const coupons = [
  {
    id: "coupon1",
    title: "생일 쿠폰",
    description: "생일 축하 특별 할인",
    discountType: "percent",
    value: 10,
    minAmount: 20000,
    expire: "2025-07-18",
    issuedAt: "2025-06-01",
    salonId: "salon001",
    userOwns: true,
    usable: true,
  },
  {
    id: "coupon2",
    title: "첫 방문 쿠폰",
    description: "첫 방문 고객을 위한 특별 혜택",
    discountType: "amount",
    value: 5000,
    minAmount: 15000,
    expire: "2025-07-18",
    issuedAt: "2025-06-15",
    salonId: "salon001",
    userOwns: true,
    usable: true,
  },
];

// 조건 필터 및 쿠폰 렌더링
function loadCoupons(sortBy = "issuedAt") {
  const couponList = document.querySelector(".coupon-list");
  couponList.innerHTML = ""; // 초기화

  const today = new Date();
  const salonId = "salon001";

  let validCoupons = coupons.filter(
    (c) =>
      c.userOwns &&
      c.salonId === salonId &&
      new Date(c.expire) >= today &&
      c.usable
  );

  // 정렬
  if (sortBy === "percent") {
    validCoupons.sort(
      (a, b) =>
        (b.discountType === "percent" ? b.value : 0) -
        (a.discountType === "percent" ? a.value : 0)
    );
  } else if (sortBy === "amount") {
    validCoupons.sort(
      (a, b) =>
        (b.discountType === "amount" ? b.value : 0) -
        (a.discountType === "amount" ? a.value : 0)
    );
  } else if (sortBy === "expire") {
    validCoupons.sort((a, b) => new Date(a.expire) - new Date(b.expire));
  } else {
    validCoupons.sort((a, b) => new Date(b.issuedAt) - new Date(a.issuedAt));
  }

  document.querySelector(
    ".couponSelect span"
  ).textContent = `${validCoupons.length}개`;

  if (validCoupons.length === 0) {
    couponList.innerHTML = `<p style="font-size:12px; color:#888;">사용 가능한 쿠폰이 없습니다.</p>`;
    return;
  }

  validCoupons.forEach((coupon) => {
    const div = document.createElement("div");
    div.className = "coupon-item";
    div.innerHTML = `
      <div class="coupon-left">
        <div class="coupon-amount">
          ${
            coupon.discountType === "percent"
              ? `${coupon.value}% 할인`
              : `${coupon.value.toLocaleString()}원 할인`
          }
        </div>
        <div class="coupon-title">${coupon.title}</div>
        <div class="coupon-desc">${coupon.description}</div>
        <div class="coupon-min">${coupon.minAmount.toLocaleString()}원 이상 사용 가능</div>
        <div class="coupon-exp">사용 기한: ${coupon.expire}</div>
      </div>
      <button onclick="applyCoupon('${coupon.id}')">사용하기</button>
    `;
    couponList.appendChild(div);
  });
}

// 쿠폰 할인율, 할인금액 적용
let selectedCouponId = null;

function applyCoupon(couponId) {
  const allCoupons = document.querySelectorAll(".coupon-item");
  const discountDisplay = document.getElementById("applied-discount");

  // 선택 취소
  if (selectedCouponId === couponId) {
    selectedCouponId = null;
    appliedDiscount.coupon = 0;

    allCoupons.forEach((item) => {
      const button = item.querySelector("button");
      button.disabled = false;
      button.style.opacity = "1";
      button.style.pointerEvents = "auto";
    });

    if (discountDisplay) discountDisplay.textContent = "총 원 할인";
    updateFinalPrice();
    return;
  }

  // ✅ 선택 적용
  selectedCouponId = couponId;

  allCoupons.forEach((item) => {
    const button = item.querySelector("button");
    if (button.getAttribute("onclick").includes(couponId)) {
      button.disabled = false;
      button.style.opacity = "1";
      button.style.pointerEvents = "auto";
    } else {
      button.disabled = true;
      button.style.opacity = "0.4";
      button.style.pointerEvents = "none";
    }
  });

  const coupon = coupons.find((c) => c.id === couponId);
  let couponDiscount = 0;
  let displayText = "";

  if (coupon) {
    if (coupon.discountType === "percent") {
      couponDiscount = Math.floor(originalPrice * (coupon.value / 100));
      displayText = `${coupon.value}%`;
    } else {
      couponDiscount = coupon.value;
      displayText = `${coupon.value.toLocaleString()}원`;
    }

    appliedDiscount.coupon = couponDiscount;

    if (discountDisplay) discountDisplay.textContent = `총 ${displayText} 할인`;
  }

  updateFinalPrice();
}

// 정렬 버튼 연결
function setupCouponFilters() {
  const buttons = document.querySelectorAll(".filter-options button");
  buttons.forEach((btn) => {
    btn.addEventListener("click", () => {
      loadCoupons(btn.dataset.sort);
    });
  });
}

// 실행
document.addEventListener("DOMContentLoaded", () => {
  loadCoupons();
  setupCouponFilters();
});

// 정액권 선택 & 예약 하기 버튼 섹션

const salonId = "salon001";
const originalPrice = 58000; // 시술 기본 금액
const appliedDiscount = {
  coupon: 0,
  ticket: 0,
};

// 정액권 렌더링

let selectedTicketId = null;

function applyTicket(ticketId) {
  const allButtons = document.querySelectorAll(".ticket-item button");
  const ticketButton = [...allButtons].find((btn) =>
    btn.getAttribute("onclick").includes(ticketId)
  );
  const ticketItem = ticketButton.closest(".ticket-item");
  const ticketPriceDisplay = document.getElementById("ticket-price");

  const amountText = ticketItem.querySelector(".ticket-amount").textContent;
  const discountAmount = parseInt(amountText.replace(/[^0-9]/g, ""), 10);

  if (selectedTicketId === ticketId) {
    selectedTicketId = null;
    appliedDiscount.ticket = 0;

    allButtons.forEach((btn) => {
      btn.disabled = false;
      btn.style.opacity = "1";
      btn.style.pointerEvents = "auto";
    });

    if (ticketPriceDisplay)
      ticketPriceDisplay.textContent = "정액권 적용 금액: 원";
  } else {
    selectedTicketId = ticketId;
    appliedDiscount.ticket = discountAmount;

    allButtons.forEach((btn) => {
      if (btn === ticketButton) {
        btn.disabled = false;
        btn.style.opacity = "1";
      } else {
        btn.disabled = true;
        btn.style.opacity = "0.4";
        btn.style.pointerEvents = "none";
      }
    });

    if (ticketPriceDisplay)
      ticketPriceDisplay.textContent = `정액권 적용 금액: ${discountAmount.toLocaleString()}원`;
  }

  updateFinalPrice();
}
// 총 결제 금액
function updateFinalPrice() {
  const ticketAmount = appliedDiscount.ticket || 0;
  const couponAmount = appliedDiscount.coupon || 0;
  const final = Math.max(0, originalPrice - couponAmount - ticketAmount);

  const display = document.getElementById("total-price");
  if (display)
    display.textContent = `총 결제 금액: ${final.toLocaleString()}원`;
}

// 예약확인 버튼
document.addEventListener("DOMContentLoaded", () => {
  const reserveBtn = document.querySelector("#finalReservation button");
  if (reserveBtn) {
    reserveBtn.addEventListener("click", () => {
      alert("예약이 완료되었습니다!");
      window.location.href = "/reservation-history.html";
    });
  }
});

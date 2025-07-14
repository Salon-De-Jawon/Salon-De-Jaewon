document.addEventListener("DOMContentLoaded", function () {
  const memberNameInput = document.getElementById("memberName");
  const memberIdInput = document.getElementById("memberId");
  const resultDiv = document.getElementById("memberSearchResults");

  memberNameInput.addEventListener("input", async function () {
    const keyword = this.value.trim();
    if (keyword.length < 2) {
      resultDiv.innerHTML = '';
      return;
    }

    try {
      const res = await fetch(`/manage/members/search?keyword=${encodeURIComponent(keyword)}`);
      const members = await res.json();

      resultDiv.innerHTML = '';
      if (members.length === 0) {
        const none = document.createElement("div");
        none.classList.add("autocomplete-item");
        none.textContent = "일치하는 회원이 없습니다.";
        resultDiv.appendChild(none);
        return;
      }

      members.forEach(member => {
        const div = document.createElement("div");
        div.classList.add("autocomplete-item");
        div.textContent = `${member.name} (${member.tel})`;
        div.dataset.id = member.id;
        div.dataset.name = member.name;
        resultDiv.appendChild(div);
      });
    } catch (e) {
      console.error("회원 검색 오류:", e);
    }
  });

  resultDiv.addEventListener("click", async function (e) {
    const target = e.target;
    if (!target.classList.contains("autocomplete-item")) return;

    const memberId = target.dataset.id;
    const memberName = target.dataset.name;

    memberIdInput.value = memberId;
    memberNameInput.value = memberName;
    resultDiv.innerHTML = '';

    try {
      const res = await fetch(`/manage/members/${memberId}/coupons`);
      const data = await res.json();

      // 쿠폰 셋팅
      const couponSelect = document.getElementById("couponSelect");
      couponSelect.innerHTML = `<option value="" data-discount-type="" data-discount-value="0" data-minimum-amount="0">선택 안 함</option>`;
      data.coupons.forEach(coupon => {
        const option = document.createElement("option");
        option.value = coupon.memberCouponId ?? "";
        option.dataset.discountType = coupon.discountType;
        option.dataset.discountValue = coupon.discountValue;
        option.dataset.minimumAmount = coupon.minimumAmount;
        option.textContent = `${coupon.name} (${coupon.discountType === 'PERCENT' ? coupon.discountValue + '%' : coupon.discountValue.toLocaleString() + '원'})`;
        couponSelect.appendChild(option);
      });

      // 정액권 셋팅
      const prepaid = data.ticketBalance || 0;
      const ticketAmountInput = document.getElementById("ticketAmount");
      ticketAmountInput.value = 0;  // 초기값 0으로 두고
      ticketAmountInput.setAttribute("data-max", prepaid);

      // 정액권 입력 칸 숨김 처리 (체크박스 상태 반영)
      const useTicketCheckbox = document.getElementById("useTicket");
      document.getElementById("ticketAmountRow").style.display = useTicketCheckbox.checked ? "block" : "none";

      calculateFinalPrice();

    } catch (e) {
      console.error("쿠폰/정액권 조회 오류:", e);
    }
  });

  // 쿠폰 변경 시 처리
  document.getElementById("couponSelect").addEventListener("change", function () {
    const selected = this.options[this.selectedIndex];
    const servicePrice = parseInt(document.getElementById("servicePrice").value || 0);
    const minAmount = parseInt(selected.dataset.minimumAmount || 0);

    if (servicePrice < minAmount) {
      alert("이 쿠폰은 최소 " + minAmount.toLocaleString() + "원 이상 시술 금액에서만 사용 가능합니다.");
      this.value = "none";
      document.getElementById("couponDiscount").value = 0;
      calculateFinalPrice();
      return;
    }

    const discountType = selected.dataset.discountType;
    const discountValue = parseInt(selected.dataset.discountValue || 0);
    const discount = discountType === 'PERCENT'
      ? Math.floor(servicePrice * (discountValue / 100))
      : discountValue;

    document.getElementById("couponDiscount").value = discount;
    calculateFinalPrice();
  });

  // 시술 금액 입력 시 쿠폰 할인 재계산 + 정액권 계산
  document.getElementById("servicePrice").addEventListener("input", function () {
    const couponSelect = document.getElementById("couponSelect");
    const selected = couponSelect.options[couponSelect.selectedIndex];
    const minAmount = parseInt(selected.dataset.minimumAmount || 0);
    const servicePrice = parseInt(this.value || 0);

    if (selected.value !== "none") {
      if (servicePrice < minAmount) {
        alert("이 쿠폰은 최소 " + minAmount.toLocaleString() + "원 이상 시술 금액에서만 사용 가능합니다.");
        couponSelect.value = "none";
        document.getElementById("couponDiscount").value = 0;
        calculateFinalPrice();
        return;
      }

      const discountType = selected.dataset.discountType;
      const discountValue = parseInt(selected.dataset.discountValue || 0);
      const discount = discountType === 'PERCENT'
        ? Math.floor(servicePrice * (discountValue / 100))
        : discountValue;

      document.getElementById("couponDiscount").value = discount;
    } else {
      document.getElementById("couponDiscount").value = 0;
    }

    calculateFinalPrice();
  });

  // 정액권 사용 체크박스 토글
  document.getElementById("useTicket").addEventListener("change", function () {
    const ticketAmountInput = document.getElementById("ticketAmount");
    const servicePrice = parseInt(document.getElementById("servicePrice").value || 0);
    const couponDiscount = parseInt(document.getElementById("couponDiscount").value || 0);
    const ticketBalance = parseInt(ticketAmountInput.getAttribute("data-max") || 0);

    const maxTicketUse = servicePrice - couponDiscount;
    const maxUse = Math.min(maxTicketUse, ticketBalance);

    if (this.checked) {
      document.getElementById("ticketAmountRow").style.display = "block";
      ticketAmountInput.value = maxUse > 0 ? maxUse : 0;
    } else {
      document.getElementById("ticketAmountRow").style.display = "none";
      ticketAmountInput.value = 0;
    }

    calculateFinalPrice();
  });

  // 정액권 사용 금액 직접 입력 시
  document.getElementById("ticketAmount").addEventListener("input", function () {
    const max = parseInt(this.getAttribute("data-max") || 0);
    let val = parseInt(this.value || 0);
    if (val > max) {
      val = max;
      this.value = max;
    }
    if (val < 0) {
      this.value = 0;
    }
    calculateFinalPrice();
  });

  // 최종 결제금액 계산 함수
  function calculateFinalPrice() {
    const servicePrice = parseInt(document.getElementById("servicePrice").value || 0);
    const couponDiscount = parseInt(document.getElementById("couponDiscount").value || 0);
    const ticketUsed = document.getElementById("useTicket").checked;
    const ticketAmountInput = document.getElementById("ticketAmount");
    const ticketBalance = parseInt(ticketAmountInput.getAttribute("data-max") || 0);

    // 최대 사용 가능한 정액권 금액 = 시술금액 - 쿠폰할인금액
    const maxTicketUse = servicePrice - couponDiscount;
    const maxUse = Math.min(maxTicketUse, ticketBalance);

    if (ticketUsed) {
      // 체크된 경우에는 ticketAmount 값을 자동으로 maxUse로 세팅
      ticketAmountInput.value = maxUse > 0 ? maxUse : 0;
    } else {
      ticketAmountInput.value = 0;
    }

    const ticketAmount = ticketUsed ? parseInt(ticketAmountInput.value || 0) : 0;
    const finalPrice = servicePrice - couponDiscount - ticketAmount;
    document.getElementById("finalPrice").value = finalPrice < 0 ? 0 : finalPrice;
  }

});

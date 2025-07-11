
// 디자이너 섹션
function initDesignerSection() {
  const maxVisible = 3;
  let expanded = false;

  const designerCards = document.querySelectorAll(".designer-card");
  const moreBtn = document.querySelector(".designer-more .btn-more");

  // 초기 3개만 보이기
  designerCards.forEach((card, i) => {
    card.style.display = i < maxVisible ? "block" : "none";
  });

  moreBtn.addEventListener("click", () => {
    expanded = !expanded;
    designerCards.forEach((card, i) => {
      card.style.display = expanded ? "block" : i < maxVisible ? "block" : "none";
    });
    moreBtn.textContent = expanded ? "닫기" : "더보기";
  });

  designerCards.forEach(card => {
    card.addEventListener("click", () => {
      const selectedId = card.dataset.id;
      document.querySelector("#designer-fixed-section").style.display = "block";
      document.querySelector("#designer-name").textContent = card.querySelector(".name").textContent;

      designerCards.forEach(c => {
        if (c !== card) c.classList.add("disabled"); // 선택 안된 디자이너 비활성화
        else c.classList.add("selected");
      });

      document.querySelector(".designer-more").style.display = "none";
    });
  });

  document.querySelector(".btn-reserve")?.addEventListener("click", () => {
    document.querySelector("#designer-section").style.display = "none";
  });
}


// 시술 리스트 섹션
function initServiceSection() {
  const categoryTabs = document.querySelectorAll(".service-category-tab");
  const serviceLists = document.querySelectorAll(".service-list");
  const maxVisible = 3;

  categoryTabs.forEach(tab => {
    tab.addEventListener("click", () => {
      const targetId = tab.dataset.category;
      serviceLists.forEach(list => {
        list.style.display = list.dataset.category === targetId ? "block" : "none";

        const items = list.querySelectorAll(".service-item");
        items.forEach((item, i) => {
          item.style.display = i < maxVisible ? "block" : "none";
        });

        const moreBtn = list.querySelector(".btn-more");
        moreBtn.textContent = "더보기";
        moreBtn.addEventListener("click", () => {
          const expanded = moreBtn.textContent === "더보기";
          items.forEach((item, i) => {
            item.style.display = expanded ? "block" : i < maxVisible ? "block" : "none";
          });
          moreBtn.textContent = expanded ? "닫기" : "더보기";
        });
      });
    });
  });

  document.querySelectorAll(".service-item").forEach(item => {
    item.addEventListener("click", () => {
      const serviceName = item.querySelector(".name").textContent;
      document.querySelector(".selected-service-name").textContent = serviceName;

      // 전체 리스트 비활성화
      document.querySelectorAll(".service-item").forEach(i => i.classList.add("disabled"));
      item.classList.add("selected");

      document.querySelector(".service-change-btn").style.display = "block";
    });
  });
}


// 날짜 및 시간 선택 섹션
function initScheduleSection() {
  const dateSlots = document.querySelectorAll(".calendar-date");
  const timeSlotsContainer = document.querySelector("#time-slot-container");
  const reserveBtn = document.querySelector(".btn-reserve-final");

  dateSlots.forEach(date => {
    date.addEventListener("click", () => {
      dateSlots.forEach(d => d.classList.remove("selected"));
      date.classList.add("selected");

      // AJAX로 시간 슬롯 가져오기 or mock render
      timeSlotsContainer.style.display = "block";
    });
  });

  document.querySelectorAll(".time-slot").forEach(slot => {
    slot.addEventListener("click", () => {
      const isSelected = slot.classList.contains("selected");
      document.querySelectorAll(".time-slot").forEach(s => s.classList.remove("selected"));

      if (!isSelected) {
        slot.classList.add("selected");
      }

      updateReservationSummary();
    });
  });

  reserveBtn.addEventListener("click", () => {
    const missing = [];
    if (!document.querySelector(".selected-designer-name").textContent) missing.push("디자이너");
    if (!document.querySelector(".selected-service-name").textContent) missing.push("시술");
    if (!document.querySelector(".calendar-date.selected")) missing.push("날짜");
    if (!document.querySelector(".time-slot.selected")) missing.push("시간");

    if (missing.length > 0) {
      displayWarning(`${missing.join(", ")}을(를) 선택해주세요`);
      return;
    }

    submitReservation();
  });
}

function updateReservationSummary() {
  // 금액 및 시간 표시
  document.querySelector(".price-summary").style.display = "block";
}

function displayWarning(msg) {
  const warnBox = document.querySelector(".reserve-warning");
  warnBox.textContent = msg;
  warnBox.style.display = "block";
}


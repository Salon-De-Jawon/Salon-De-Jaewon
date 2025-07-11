
// 디자이너 선택 섹션

function initDesignerSection() {
  const maxVisible = 3;
  let expanded = false;

  const designerCards = document.querySelectorAll(".designer-card");
  const moreBtn = document.querySelector(".designer-more .btn-more");
  const designerSection = document.querySelector("#designer-section");
  const fixedSection = document.querySelector("#designer-fixed-section");
  const selectedDesignerNameDisplay = document.querySelector(".selected-designer-name");
  const hiddenDesignerIdInput = document.querySelector('input[name="shopDesignerId"]');

  // 초기에 3개만 표시
  designerCards.forEach((card, i) => {
    card.style.display = i < maxVisible ? "block" : "none";
  });

  moreBtn?.addEventListener("click", () => {
    expanded = !expanded;
    designerCards.forEach((card, i) => {
      card.style.display = expanded ? "block" : i < maxVisible ? "block" : "none";
    });
    moreBtn.textContent = expanded ? "닫기" : "더보기";
  });

  designerCards.forEach(card => {
    card.addEventListener("click", () => {
      const selectedId = card.dataset.id;
      const designerName = card.querySelector(".name")?.textContent;

      // 값 반영
      hiddenDesignerIdInput.value = selectedId;
      selectedDesignerNameDisplay.textContent = designerName;

      fixedSection.style.display = "block";
      designerSection.style.display = "none";

      designerCards.forEach(c => {
        if (c !== card) c.classList.add("disabled");
        else c.classList.add("selected");
      });

      document.querySelector(".designer-more")?.style.display = "none";
    });
  });
}


// 시술 리스트 선택 섹션

function initServiceSection() {
  const categoryTabs = document.querySelectorAll(".category-btn");
  const categorySections = document.querySelectorAll(".service-category");
  const selectedServiceSummary = document.querySelector(".service-selected-summary");
  const selectedServiceInfo = document.querySelector(".selected-service-info");
  const hiddenServiceInput = document.querySelector("#selectedServiceId");
  const maxVisible = 3;

  categoryTabs.forEach(tab => {
    tab.addEventListener("click", () => {
      const target = tab.dataset.tab;

      categoryTabs.forEach(t => t.classList.remove("active"));
      tab.classList.add("active");

      categorySections.forEach(section => {
        section.style.display = (target === "all" || section.dataset.category === target) ? "block" : "none";

        const items = section.querySelectorAll("li");
        items.forEach((item, i) => {
          item.style.display = i < maxVisible ? "block" : "none";
        });

        const moreBtn = section.querySelector(".btn-more");
        if (moreBtn) {
          moreBtn.textContent = "더보기";
          moreBtn.addEventListener("click", () => {
            const expanded = moreBtn.textContent === "더보기";
            items.forEach((item, i) => {
              item.style.display = expanded ? "block" : i < maxVisible ? "block" : "none";
            });
            moreBtn.textContent = expanded ? "닫기" : "더보기";
          });
        }
      });
    });
  });

  // 시술 선택
  document.querySelectorAll("input[name='shopServiceId']").forEach(input => {
    input.addEventListener("change", () => {
      const serviceName = input.closest("li").querySelector(".service-name").textContent;
      selectedServiceInfo.textContent = serviceName;
      hiddenServiceInput.value = input.value;

      selectedServiceSummary.style.display = "block";
    });
  });
}


// 날짜 / 시간 선택 섹션

function initScheduleSection() {
  const dateButtons = document.querySelectorAll(".date-btn");
  const timeButtons = document.querySelectorAll(".time-btn");
  const hiddenDateTimeInput = document.querySelector("#selectedDateTime");

  let selectedDate = null;
  let selectedTime = null;

  dateButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      dateButtons.forEach(b => b.classList.remove("selected"));
      btn.classList.add("selected");

      selectedDate = btn.dataset.date;
      updateHiddenDateTime();
    });
  });

  timeButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      timeButtons.forEach(b => b.classList.remove("selected"));
      btn.classList.add("selected");

      selectedTime = btn.dataset.time;
      updateHiddenDateTime();
    });
  });

  function updateHiddenDateTime() {
    if (selectedDate && selectedTime) {
      hiddenDateTimeInput.value = `${selectedDate}T${selectedTime}`;
    }
  }
}


// 마지막 초기화 실행
document.addEventListener("DOMContentLoaded", () => {
  initDesignerSection();
  initServiceSection();
  initScheduleSection();
});
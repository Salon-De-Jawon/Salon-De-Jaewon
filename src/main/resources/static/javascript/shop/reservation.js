document.addEventListener("DOMContentLoaded", () => {
  initDesignerSection();
  initServiceSection();
  initDateTimeSection(); // 초기에는 비어 있는 캘린더만 준비
  initReservationSubmit();
});


function initDesignerSection() {
  const maxVisible = 3;
  let expanded = false;
  let selectedDesigner = null;

  const designerSection = document.getElementById("designer-section");
  const designerCards = document.querySelectorAll(".designer-card");
  const designerMoreWrapper = designerSection.querySelector(".designer-more");
  const hiddenDesignerIdInput = document.querySelector("#selectedDesignerInput");

  const summaryBox = document.querySelector(".designer-selected-summary");
  const summaryName = summaryBox.querySelector(".selected-designer-name");
  const changeBtn = summaryBox.querySelector(".btn-reset-designer");

  const moreBtn = document.createElement("button");
  moreBtn.type = "button";
  moreBtn.className = "btn-more";
  moreBtn.textContent = "더보기";
  designerMoreWrapper.innerHTML = "";
  designerMoreWrapper.appendChild(moreBtn);

  moreBtn.addEventListener("click", () => {
    expanded = !expanded;
    designerCards.forEach((card, i) => {
      card.style.display = expanded || i < maxVisible ? "block" : "none";
    });
    moreBtn.textContent = expanded ? "닫기" : "더보기";
  });

  designerCards.forEach(card => {
    card.addEventListener("click", () => {
      if (selectedDesigner) return;

      const id = card.dataset.id;
      const name = card.querySelector("h3")?.textContent;

      selectedDesigner = card;
      hiddenDesignerIdInput.value = id;
      summaryName.textContent = name;

      //  리스트의 모든 카드 숨김
      designerCards.forEach(c => c.style.display = "none");

      //  더보기 버튼도 숨기기
      designerMoreWrapper.style.display = "none";

      //  요약 영역만 보여주기
      summaryBox.style.display = "block";

      resetDateTimeSection();
      generateSlidingDateGrid();
    });
  });

  changeBtn.onclick = () => {
    selectedDesigner = null;
    hiddenDesignerIdInput.value = "";
    summaryName.textContent = "이름 없음";

    //  요약 영역 숨기기
    summaryBox.style.display = "none";

    //  카드들 다시 표시
    designerCards.forEach((c, i) => {
      c.classList.remove("selected", "disabled");
      c.style.display = i < maxVisible ? "block" : "none";
    });

    //  더보기 버튼 다시 보이기
      designerMoreWrapper.style.display = "block";

    //  더보기 버튼 복원
    designerMoreWrapper.innerHTML = "";
    moreBtn.textContent = "더보기";
    designerMoreWrapper.appendChild(moreBtn);

    resetDateTimeSection();
  };
}


function initServiceSection() {
  const categoryTabs = document.querySelectorAll(".category-btn");
  const categorySections = document.querySelectorAll(".service-category");
  const summaryBox = document.querySelector(".service-selected-summary");
  const summaryText = document.querySelector(".selected-service-info");
  const hiddenServiceInput = document.querySelector("#selectedServiceId");

  categoryTabs.forEach(tab => {
    tab.addEventListener("click", () => {
      const target = tab.dataset.tab;

      categoryTabs.forEach(t => t.classList.remove("active"));
      tab.classList.add("active");

      categorySections.forEach(section => {
        const matched = target === "all" || section.dataset.category === target;
        section.style.display = matched ? "block" : "none";
      });
    });
  });

  document.querySelectorAll("input[name='shopServiceId']").forEach(input => {
    input.addEventListener("change", () => {
      const li = input.closest("li");
      const serviceName = li.querySelector(".service-name").textContent;

      hiddenServiceInput.value = input.value;
      summaryText.textContent = serviceName;
      summaryBox.style.display = "block";

      document.querySelectorAll(".service-category").forEach(cat => {
        cat.style.display = "none";
      });

      const changeBtn = summaryBox.querySelector(".change-service-btn");
      changeBtn.onclick = () => {
        hiddenServiceInput.value = "";
        summaryBox.style.display = "none";
        document.querySelector(".category-btn[data-tab='all']").click();
      };
    });
  });
}

let startOffset = 0;

function resetDateTimeSection() {
  document.querySelector("#selectedDateTime").value = "";
  document.getElementById("dateList").innerHTML = "";
  document.querySelector(".time-grid").innerHTML = "";
  document.getElementById("currentMonth").textContent = "";
  document.querySelector(".reservation-warning-message").innerHTML = "";
}

function generateSlidingDateGrid() {
  const dateList = document.getElementById("dateList");
  const monthTitle = document.getElementById("currentMonth");
  const hiddenDateInput = document.querySelector("#selectedDateTime");
  const today = new Date();
  dateList.innerHTML = "";

  const firstDate = new Date(today);
  firstDate.setDate(today.getDate() + startOffset);
  const displayMonth = firstDate.getMonth() + 1;
  const displayYear = firstDate.getFullYear();

  if (monthTitle) {
    monthTitle.textContent = `${displayYear}년 ${displayMonth}월`;
  }

  for (let i = 0; i < 14; i++) {
    const date = new Date(today);
    date.setDate(today.getDate() + startOffset + i);

    const dayNum = date.getDate();
    const dayOfWeek = date.getDay();
    const isToday = startOffset + i === 0;
    const isoDate = date.toISOString().split("T")[0];

    const dateBox = document.createElement("div");
    dateBox.classList.add("date-box");
    if (isToday) dateBox.classList.add("selected");
    dateBox.dataset.date = isoDate;

    dateBox.innerHTML = `
      <div class="date-day">${["일","월","화","수","목","금","토"][dayOfWeek]}</div>
      <div class="date-number">${dayNum}</div>
      <div class="date-label">${isToday ? '오늘' : ''}</div>
    `;

    dateBox.addEventListener("click", () => {
      document.querySelectorAll(".date-box").forEach(d => d.classList.remove("selected"));
      dateBox.classList.add("selected");
      hiddenDateInput.value = isoDate;
      fetchAvailableTimes(isoDate);
    });

    dateList.appendChild(dateBox);
  }
}

function moveDateWindow(direction) {
  if (direction === 'prev') {
    startOffset = Math.max(0, startOffset - 14);
  } else if (direction === 'next') {
    startOffset += 14;
  }
  generateSlidingDateGrid();
}

function initDateTimeSection() {
  document.querySelector("#btn-prev")?.addEventListener("click", () => moveDateWindow("prev"));
  document.querySelector("#btn-next")?.addEventListener("click", () => moveDateWindow("next"));
}

function fetchAvailableTimes(dateStr) {
  const designerId = document.querySelector("#selectedDesignerInput").value;
  if (!designerId) return;

  fetch(`/reservation/available-times?designerId=${designerId}&date=${dateStr}`)
    .then(res => res.json())
    .then(data => {
      const timeGrid = document.querySelector(".time-grid");
      const warning = document.querySelector(".reservation-warning-message");
      timeGrid.innerHTML = "";

      if (data.holiday) {
        warning.innerHTML = `<span>휴무일입니다</span>`;
        return;
      }

      warning.innerHTML = `<span>정상 운영일입니다</span>`;

      data.availableTimes.forEach(timeStr => {
        const btn = document.createElement("button");
        btn.className = "time-btn";
        btn.dataset.time = timeStr;
        btn.textContent = timeStr;

        btn.addEventListener("click", () => {
          document.querySelectorAll(".time-btn").forEach(b => b.classList.remove("selected"));
          btn.classList.add("selected");

          const selectedDate = document.querySelector("#selectedDateTime").value;
                  document.querySelector("#selectedDateTime").value = `${selectedDate}T${timeStr}`;
                  updateSummary();
                });

                timeGrid.appendChild(btn);
              });
            });
        }

        function updateSummary() {
          const designerName = document.querySelector(".selected-designer-name").textContent;
          const serviceName = document.querySelector(".selected-service-info").textContent;
          const dateTime = document.querySelector("#selectedDateTime").value;
          const summaryArea = document.querySelector(".reservation-summary");
          if (!summaryArea) return;

          summaryArea.innerHTML = `
            <p><strong>${designerName}</strong> 디자이너</p>
            <p>시술: ${serviceName}</p>
            <p>일정: ${dateTime.replace("T", " ")}</p>
          `;
        }
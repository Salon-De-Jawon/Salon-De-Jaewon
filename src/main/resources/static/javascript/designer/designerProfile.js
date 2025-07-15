// 디자이너 프로필 상세보기 (더보기)
document.addEventListener("DOMContentLoaded", function () {
  const toggleBtn = document.querySelector(".toggle-description");
  const descBox = document.querySelector(".designer-description");
  const icon = toggleBtn.querySelector("i");

  toggleBtn.addEventListener("click", () => {
    const isOpen = descBox.style.display === "block";
    descBox.style.display = isOpen ? "none" : "block";

    icon.classList.toggle("fa-chevron-down", isOpen);
    icon.classList.toggle("fa-chevron-up", !isOpen);

    toggleBtn.firstChild.textContent = isOpen ? "더보기 " : "닫기 ";
  });
});

// 미용실 이름 클릭 → 이동
document.querySelector(".designer-salon")?.addEventListener("click", () => {
  window.location.href = "/shop-detail"; // 실제 URL로 교체 필요
});

// 디자이너 프로필 메뉴바 전환
document.addEventListener("DOMContentLoaded", () => {
  const tabItems = document.querySelectorAll(".tab-item");
  const tabContents = document.querySelectorAll(".tab-content");

  // 초기 상태: 모든 콘텐츠 숨기고 active만 보여줌
  tabContents.forEach((content) => (content.style.display = "none"));
  const defaultTab = document.querySelector(".tab-item.active");
  if (defaultTab) {
    const defaultContent = document.getElementById(
      defaultTab.getAttribute("data-tab")
    );
    if (defaultContent) defaultContent.style.display = "block";
  }

  tabItems.forEach((item) => {
    item.addEventListener("click", () => {
      // 모든 탭 비활성화 + 콘텐츠 숨김
      tabItems.forEach((el) => el.classList.remove("active"));
      tabContents.forEach((content) => (content.style.display = "none"));

      // 클릭한 탭 활성화
      item.classList.add("active");
      const tabId = item.getAttribute("data-tab");
      const activeContent = document.getElementById(tabId);
      if (activeContent) {
        activeContent.style.display = "block";
      }
    });
  });
});

// 찜하기 버튼 클릭 시 하트색상 변경 및 alert 띄우기 + 내 찜 목록에 저장
const heartIcon = document.querySelector(".icon-text-btn svg.lucide-heart");
const heartBtn = heartIcon?.closest("button");

heartBtn?.addEventListener("click", () => {
  const isActive = heartIcon.classList.toggle("active");

  if (isActive) {
    heartIcon.style.stroke = "#e74c3c"; // 빨간 하트
    alert("내 찜목록에 저장되었습니다");
  } else {
    heartIcon.style.stroke = "currentColor"; // 원래대로
  }
});

// 공유 버튼 클릭 → 모달 열기
document
  .querySelector(".lucide-share-2")
  ?.closest("button")
  .addEventListener("click", () => {
    document.getElementById("share-modal").style.display = "flex";
  });

// 공유 모달 내 복사 버튼
document.querySelector("#share-modal button")?.addEventListener("click", () => {
  const input = document.querySelector("#share-modal input");
  input.select();
  document.execCommand("copy");
  alert("링크가 복사되었습니다!");
  document.getElementById("share-modal").style.display = "none";
});

// 모달 외부 클릭 시 닫기
document.getElementById("share-modal")?.addEventListener("click", (e) => {
  if (e.target.id === "share-modal") {
    e.target.style.display = "none";
  }
});

// 할인 퍼센트가 없을 시 줄 안긋기
document.addEventListener("DOMContentLoaded", function () {
  const categoryButtons = document.querySelectorAll(".category-btn");
  const sections = document.querySelectorAll(".category-section");

  let isClicking = false; // 스크롤과 클릭 이벤트 구분용

  // 현재 메뉴/카테고리 바 높이 계산
  function getOffset() {
    const tabBar = document.querySelector(".designer-tabs");
    const categoryBar = document.querySelector(".category-tabs");
    return (tabBar?.offsetHeight || 0) + (categoryBar?.offsetHeight || 0) + 8;
  }

  // 버튼 클릭 시 → 스크롤 이동 및 스타일 직접 지정
  categoryButtons.forEach((btn) => {
    btn.addEventListener("click", function () {
      const category = this.dataset.category;
      const target = document.getElementById(category);
      const offset = getOffset();

      if (target) {
        isClicking = true;

        // 직접 스크롤
        const targetPos = target.offsetTop - offset;

        window.scrollTo({
          top: targetPos,
          behavior: "smooth",
        });

        // 클릭 시 active 및 highlight 직접 지정
        categoryButtons.forEach((b) => {
          b.classList.remove("active", "highlight");
        });
        this.classList.add("active", "highlight");

        // 일정 시간 후 스크롤 이벤트 재활성화
        setTimeout(() => {
          isClicking = false;
        }, 500); // smooth scroll 시간 고려
      }
    });
  });

  // 스크롤 감지 시 강조
  window.addEventListener("scroll", function () {
    if (isClicking) return; // 클릭 중이면 스크롤 강조 건너뜀

    const scrollPos = window.scrollY || document.documentElement.scrollTop;
    const offset = getOffset();

    let currentId = "";

    sections.forEach((section) => {
      const top = section.offsetTop - offset;
      const bottom = top + section.offsetHeight;

      if (scrollPos >= top && scrollPos < bottom) {
        currentId = section.id;
      }
    });

    categoryButtons.forEach((btn) => {
      btn.classList.remove("highlight", "active");
      if (btn.dataset.category === currentId) {
        btn.classList.add("highlight", "active");
      }
    });
  });
});

// 리뷰 섹션 카테고리별 보여지는 리스트 3개로 제한 + 더보기 버튼 활성화
document.addEventListener("DOMContentLoaded", function () {
  const sections = document.querySelectorAll(".category-section");

  sections.forEach((section) => {
    const items = section.querySelectorAll(".service-item");
    const moreBtn = section.querySelector(".more-btn");
    const label = moreBtn?.querySelector(".label");
    const icon = moreBtn?.querySelector(".chevron");

    if (items.length > 3) {
      items.forEach((item, idx) => {
        if (idx >= 3) item.style.display = "none";
      });

      let expanded = false;

      moreBtn.addEventListener("click", () => {
        expanded = !expanded;

        items.forEach((item, idx) => {
          if (idx >= 3) {
            item.style.display = expanded ? "flex" : "none";
          }
        });

        if (expanded) {
          label.textContent = "접기";
          icon.classList.remove("fa-chevron-down");
          icon.classList.add("fa-chevron-up");
        } else {
          label.textContent = "더보기";
          icon.classList.remove("fa-chevron-up");
          icon.classList.add("fa-chevron-down");
        }
      });
    } else {
      moreBtn.style.display = "none"; // 3개 이하면 더보기 숨김
    }
  });
});

// 탭 (메뉴) 클릭시 카테고리 탭 보이기 or 숨기기
document.querySelectorAll(".tab-item").forEach((tab) => {
  tab.addEventListener("click", function () {
    const selected = this.dataset.tab;

    document.querySelectorAll(".tab-content").forEach((el) => {
      el.style.display = "none";
    });

    document.getElementById(selected).style.display = "block";

    document.querySelectorAll(".tab-item").forEach((el) => {
      el.classList.remove("active");
    });
    this.classList.add("active");

    // 카테고리 바는 메뉴탭에서만 보이게
    const categoryBar = document.getElementById("categoryTabs");
    if (selected === "tab-menu") {
      categoryBar.style.display = "block";
    } else {
      categoryBar.style.display = "none";
    }
  });
});

// 리뷰섹션 필터링
document.addEventListener("DOMContentLoaded", function () {
  const tabButtons = document.querySelectorAll(".r-tab");

  tabButtons.forEach((button) => {
    button.addEventListener("click", function () {
      tabButtons.forEach((btn) => btn.classList.remove("active"));
      this.classList.add("active");

      const selectedCategory = this.dataset.category;
      const reviewItems = document.querySelectorAll(".r-item");

      reviewItems.forEach((item) => {
        // 카테고리에 따라 필터링 로직 구현 (예시: data-category 속성 사용 가능)
        if (selectedCategory === "all") {
          item.style.display = "block";
        } else {
          const itemCategory = item.dataset.category;
          item.style.display =
            itemCategory === selectedCategory ? "block" : "none";
        }
      });
    });
  });
});

// 모든 r-tab 버튼 가져오기
document.addEventListener("DOMContentLoaded", function () {
  const tabButtons = document.querySelectorAll(".r-tab");
  const reviewItems = document.querySelectorAll(".r-item");
  const moreBtn = document.querySelector(".reviewmore-btn");

  let currentCategory = "all";
  let expanded = false;

  function updateVisibleReviews() {
    let count = 0;
    reviewItems.forEach((item) => {
      const itemCategory = item.getAttribute("data-category");
      const match =
        currentCategory === "all" || itemCategory === currentCategory;

      if (match) {
        if (!expanded && count >= 3) {
          item.style.display = "none";
        } else {
          item.style.display = "block";
        }
        count++;
      } else {
        item.style.display = "none";
      }
    });

    // 버튼 텍스트와 꺾새 변경
    if (expanded) {
      moreBtn.innerHTML = `닫기 <span style="font-size:12px;">▲</span>`;
    } else {
      moreBtn.innerHTML = `더보기 <span style="font-size:12px;">▼</span>`;
    }

    // 리뷰 수가 3개 이하라면 버튼 숨김
    const totalInCategory = [...reviewItems].filter(
      (item) =>
        currentCategory === "all" ||
        item.getAttribute("data-category") === currentCategory
    ).length;

    moreBtn.style.display = totalInCategory > 3 ? "inline-block" : "none";
  }
  // 리뷰 작성일 -> n일전 방문으로 변환하여 r-meta 코드에 추가
  document.querySelectorAll(".r-meta").forEach((meta) => {
    const text = meta.textContent.trim();
    const match = text.match(/(\d{4}\.\d{2}\.\d{2})/); // 날짜 추출
    if (!match) return;

    const dateStr = match[1];
    const [year, month, day] = dateStr.split(".").map(Number);
    const writtenDate = new Date(year, month - 1, day);
    const today = new Date();
    const diffTime = today - writtenDate;
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

    let visitStr = "";
    if (diffDays === 0) visitStr = "오늘 ";
    else if (diffDays === 1) visitStr = "어제 ";
    else visitStr = `${diffDays}일 전`;

    // 기존 날짜 뒤에 ' · n일 전 방문' 추가
    meta.textContent = text.replace(dateStr, `${dateStr} · ${visitStr}`);
  });

  // 리뷰버튼 클릭시 작성페이지로 이동
  document.querySelector(".r-photo-btn")?.addEventListener("click", () => {
    window.location.href = "/review-write";
  });

  // 리뷰 필터링
  document.querySelector(".r-sort")?.addEventListener("change", (e) => {
    const order = e.target.value;
    const list = document.querySelector(".r-list");
    const items = Array.from(list.children);

    items.sort((a, b) => {
      const aText = a.querySelector(".r-rating")?.textContent || "";
      const bText = b.querySelector(".r-rating")?.textContent || "";
      const aScore = parseFloat(aText.match(/\d+(\.\d+)?/)?.[0] || "0");
      const bScore = parseFloat(bText.match(/\d+(\.\d+)?/)?.[0] || "0");
      return order === "high"
        ? bScore - aScore
        : order === "low"
        ? aScore - bScore
        : 0;
    });

    list.innerHTML = "";
    items.forEach((el) => list.appendChild(el));
  });

  // 카테고리 버튼 클릭 시
  tabButtons.forEach((button) => {
    button.addEventListener("click", () => {
      tabButtons.forEach((btn) => btn.classList.remove("active"));
      button.classList.add("active");

      currentCategory = button.getAttribute("data-category");
      expanded = false;
      updateVisibleReviews();
    });
  });

  // 더보기 버튼 클릭 시
  moreBtn.addEventListener("click", () => {
    expanded = !expanded;
    updateVisibleReviews();
  });

  // 초기화
  updateVisibleReviews();
});

export function enableDragScroll(
  target,
  { multiplier = 1.2, clickThreshold = 5 } = {}
) {
  if (!target) return;

  let isDown = false;
  let startX = 0;
  let scrollX = 0;
  let moved = false;

  function removeDragging() {
    isDown = false;
    moved = false;
    target.classList.remove("dragging");
    document.body.style.userSelect = "auto";
  }

  // PC: 마우스 시작
  target.addEventListener("mousedown", (e) => {
    isDown = true;
    moved = false;
    startX = e.pageX;
    scrollX = target.scrollLeft;
    document.body.style.userSelect = "none";
  });

  // PC: 마우스 이동
  target.addEventListener("mousemove", (e) => {
    if (!isDown) return;

    const dx = e.pageX - startX;
    if (Math.abs(dx) > clickThreshold) {
      moved = true;
      target.classList.add("dragging");
    }

    e.preventDefault();
    target.scrollLeft = scrollX - dx * multiplier;
  });

  // PC: 마우스 해제
  const onMouseUp = (e) => {
    if (!isDown) return;

    removeDragging();

    // ✅ 드래그 중이면 강제로 커서 초기화
    document.body.style.cursor = '';
    target.style.cursor = '';

    // ✅ 마우스가 DOM 바깥에서 떼졌을 가능성 → 안전하게 제거
    if (!target.contains(e.target)) {
      target.classList.remove("dragging");
    }

    if (moved) {
      const cancel = (ev) => {
        ev.stopImmediatePropagation();
        ev.preventDefault();
        target.removeEventListener("click", cancel, true);
      };
      target.addEventListener("click", cancel, true);
      return;
    }

    const clickEvt = new MouseEvent("click", {
      bubbles: true,
      cancelable: true,
      clientX: e.clientX,
      clientY: e.clientY,
    });
    e.target.dispatchEvent(clickEvt);
  };


  window.addEventListener("mouseup", onMouseUp);
  target.addEventListener("mouseleave", removeDragging);
  window.addEventListener("blur", removeDragging);
  document.addEventListener("visibilitychange", () => {
    if (document.visibilityState === "hidden") removeDragging();
  });

  // 📱 모바일 터치
  let touchStartX = 0;
  target.addEventListener(
    "touchstart",
    (e) => {
      touchStartX = e.touches[0].pageX;
      scrollX = target.scrollLeft;
    },
    { passive: true }
  );

  target.addEventListener(
    "touchmove",
    (e) => {
      const dx = e.touches[0].pageX - touchStartX;
      target.scrollLeft = scrollX - dx * multiplier;
    },
    { passive: true }
  );
}

// 자동 바인딩
export function autoBindDragScroll(selector = "[data-drag-scroll]") {
  document.querySelectorAll(selector).forEach((el) => {
    el.classList.remove("dragging");
    enableDragScroll(el);
  });
}


export function resetAllDragScrollState() {
  document.querySelectorAll('[data-drag-scroll]').forEach(el => {
    el.classList.remove('dragging');
    el.style.cursor = ''; // 💡 추가: 커서 초기화
  });
  document.body.style.userSelect = 'auto';
  document.body.style.cursor = ''; // 💡 추가: body 커서도 초기화
}

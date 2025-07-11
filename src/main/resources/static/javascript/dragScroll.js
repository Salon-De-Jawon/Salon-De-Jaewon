export function enableDragScroll(target, { multiplier = 1.2 } = {}) {
  console.log("✅ enableDragScroll 불림", target);
  if (!target) return;
  let isDown = false;
  let startX = 0;
  let scrollStart = 0;

  /* PC mouse */
  target.addEventListener("mousedown", (e) => {
    isDown = true;
    target.classList.add("dragging");
    startX = e.pageX - target.offsetLeft;
    scrollStart = target.scrollLeft;
  });

  ["mouseup", "mouseleave"].forEach((ev) =>
    target.addEventListener(ev, () => {
      isDown = false;
      target.classList.remove("dragging");
    })
  );

  target.addEventListener("mousemove", (e) => {
    if (!isDown) return;
    e.preventDefault();
    const x = e.pageX - target.offsetLeft;
    const walk = (x - startX) * multiplier;
    target.scrollLeft = scrollStart - walk;
  });

  /* Mobile touch */
  let touchStartX = 0;
  target.addEventListener(
    "touchstart",
    (e) => {
      touchStartX = e.touches[0].pageX;
      scrollStart = target.scrollLeft;
    },
    { passive: true }
  );

  target.addEventListener(
    "touchmove",
    (e) => {
      const moveX = e.touches[0].pageX;
      const walk = (moveX - touchStartX) * multiplier;
      target.scrollLeft = scrollStart - walk;
    },
    { passive: true }
  );
}

export function autoBindDragScroll(selector = "[data-drag-scroll]") {
  document.querySelectorAll(selector).forEach((el) => enableDragScroll(el));
}

console.log("js 출근");

import { renderStars } from '../ratingStarUtil.js';
import { adjustImageFitAll, adjustImageFit } from '../imageFitUtil.js';

document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.rating-area').forEach(area => {
    const score = parseFloat(area.dataset.rating || '0');
    const slot  = area.querySelector('.rating-stars');
    if (slot) renderStars(score, slot);
  });

  adjustImageFitAll('.service-img', 1); // ← 1:1 비율 적용
});
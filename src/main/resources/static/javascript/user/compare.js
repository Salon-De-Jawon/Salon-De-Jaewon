console.log("js 출근");

import { renderStars } from '../ratingStarUtil.js';
import { adjustImageFitAll } from '../imageFitUtil.js';
import { getStoredLocation } from '/javascript/user/locationUtil.js';

document.addEventListener('DOMContentLoaded', () => {
  // 별점 렌더링
  document.querySelectorAll('.rating-area').forEach(area => {
    const score = parseFloat(area.dataset.rating || '0');
    const slot  = area.querySelector('.rating-stars');
    if (slot) renderStars(score, slot);
  });

  // 이미지 비율 조정
  adjustImageFitAll('.service-img', 1);

  // URL에 위도/경도 없으면 로컬스토리지 값으로 다시 요청
  const urlParams = new URLSearchParams(window.location.search);
  if (!urlParams.has('userLat') || !urlParams.has('userLon')) {
    const userId = window.currentUserId || null;
    const location = getStoredLocation(userId, true);

    if (location) {
      const { userLatitude, userLongitude } = location;
      if (userLatitude && userLongitude) {
        const newUrl = new URL(window.location.href);
        newUrl.searchParams.set('userLat', userLatitude);
        newUrl.searchParams.set('userLon', userLongitude);
        window.location.href = newUrl.toString();
      }
    } else {
      console.warn("위치 정보가 없습니다. 위치 제공에 먼저 동의해주세요.");
    }
  }

  // 거리 정보 없으면 숨기기
  document.querySelectorAll('.distance').forEach(el => {
    const text = el.textContent.trim();
    if (!text || text === 'nullkm' || text === '0km') {
      el.style.display = 'none';
    }
  });
});
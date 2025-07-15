

import { initAddressSearchToggle }  from '/javascript/user/addressSearchUtil.js';
import { setStoredLocation, getStoredLocation } from '/javascript/user/locationUtil.js';
import { adjustImageFitAll }        from '/javascript/imageFitUtil.js';

/* ───────────── 전역 상태 ───────────── */
let region  = '';
let userLat = null;
let userLon = null;
let allShops = [];
let markers  = [];
let page = 0;
let endOfList = false;

/* ───────────── 회원 / 동의 여부 ───────────── */
const currentUserId = window.currentUserId ?? null;
const isGuest       = !currentUserId;
let   agreeLocation = isGuest ? false : !!window.userAgreeLocation;
const guestAgreed   = localStorage.getItem('guestLocationConsent') === 'true';
const needConsent   = () => (isGuest ? !guestAgreed : !agreeLocation);

/* ───────────── DOM 캐시 ───────────── */
const dqs  = sel => document.querySelector(sel);
const $regionLabel      = dqs('#user-region');
const $overlay          = dqs('.split-overlay');
const $mapBox           = dqs('.main-map');
const $locationModal    = dqs('#location-agree-modal');
const $confirmBtn       = dqs('#location-agree-accept');
const $cancelBtn        = dqs('#location-agree-cancel');

/* ───────────── Kakao 지도 초기화 ───────────── */
const DEFAULT_CENTER = new kakao.maps.LatLng(36.3504119, 127.3845475); // 대전시청
const map = new kakao.maps.Map(document.getElementById('map'), {
  center: DEFAULT_CENTER,
  level : 3,
});

/* ───────────── 오버레이 Split 효과 ───────────── */
(function initOverlay() {
  const imgUrl = $overlay.dataset.img;
  $overlay.style.setProperty('--img-url',  `url("${imgUrl}")`);
  $overlay.style.setProperty('--img-left', `url("${imgUrl}")`);
  $overlay.style.setProperty('--img-right',`url("${imgUrl}")`);
  const sheet = document.styleSheets[0];
  sheet.insertRule(`.split-overlay::before{background-image:var(--img-left);}`,  sheet.cssRules.length);
  sheet.insertRule(`.split-overlay::after{background-image:var(--img-right);}`,  sheet.cssRules.length);

  $overlay.addEventListener('click', () => {
    if ($overlay.classList.contains('open')) return;
    $overlay.classList.add('open');
    $mapBox.style.opacity = '1';
    $overlay.addEventListener('transitionend', function onEnd(e) {
      if (e.propertyName === 'transform') {
        $mapBox.classList.add('front');
        $overlay.removeEventListener('transitionend', onEnd);
      }
    });
  });

  document.addEventListener('click', e => {
    if (!$overlay.classList.contains('open') || $overlay.contains(e.target) || $mapBox.contains(e.target)) return;
    $mapBox.classList.remove('front');
    setTimeout(() => $overlay.classList.remove('open'));
  });
})();

/* ───────────── 로컬스토리지 위치 적용 ───────────── */
const storedLocation = getStoredLocation(currentUserId);
if (storedLocation) {
  ({ region1depth: region, userLatitude: userLat, userLongitude: userLon } = storedLocation);
  $regionLabel.textContent = `${storedLocation.region1depth} ${storedLocation.region2depth}`;
  map.setCenter(new kakao.maps.LatLng(userLat, userLon));
  loadShopMarkers(userLat, userLon);
  loadAreaResources(region);
} else {
  // 기본값: 대전
  region = "대전";
  userLat = 36.3504119;
  userLon = 127.3845475;
  $regionLabel.textContent = "대전 광역시청";
  loadAreaResources(region);
}

/* ───────────── 이벤트 바인딩 ───────────── */
document.addEventListener('click', e => {
  if (!e.target.closest('.location-now-text')) return;
  if (needConsent()) return openConsentModal();
  detectAndSaveLocation();
});

$confirmBtn?.addEventListener('click', () => {
  $locationModal.style.display = 'none';
  if (isGuest) localStorage.setItem('guestLocationConsent', 'true');
  else patchMemberConsent();
  detectAndSaveLocation();
});

$cancelBtn?.addEventListener('click', () => {
  $locationModal.style.display = 'none';
});

/* ───────────── 주소 검색 토글 ───────────── */
initAddressSearchToggle({
  onSelectAddress: ({ userAddress, userLatitude, userLongitude, region1depth, region2depth }) => {
    applyDetectedLocation({ lat: userLatitude, lon: userLongitude, userAddress, region1depth, region2depth });
  },
});

/* ============================================================================ */
/*                                함수 정의                                    */
/* ============================================================================ */

function openConsentModal() {
  $locationModal.classList.remove('hidden');
  $locationModal.style.display = 'flex';
}

function patchMemberConsent() {
  const token  = dqs('meta[name="_csrf"]')?.content;
  const header = dqs('meta[name="_csrf_header"]')?.content;
  fetch('/api/member/location-consent', {
    method : 'PATCH',
    headers: { 'Content-Type':'application/json', [header]: token },
  })
    .then(r => { if (!r.ok) throw new Error(); agreeLocation = true; })
    .catch(() => alert('동의 처리 실패'));
}

function detectAndSaveLocation() {
  navigator.geolocation.getCurrentPosition(
    pos => {
      const lat = pos.coords.latitude;
      const lon = pos.coords.longitude;
      fetch(`/api/coord-to-address?x=${lon}&y=${lat}`)
        .then(r => r.json())
        .then(({ userAddress, region1depth, region2depth }) => {
          applyDetectedLocation({ lat, lon, userAddress, region1depth, region2depth });
        })
        .catch(() => alert('주소 변환 실패'));
    },
    () => alert('위치 정보 접근 거부'),
    { enableHighAccuracy: true, timeout: 10000 }
  );
}

function applyDetectedLocation({ lat, lon, userAddress, region1depth, region2depth }) {
  region  = region1depth;
  userLat = lat;
  userLon = lon;
  $regionLabel.textContent = `${region1depth} ${region2depth}`;
  setStoredLocation(currentUserId, {
    userAddress, userLatitude: lat, userLongitude: lon, region1depth, region2depth,
  });
  map.setCenter(new kakao.maps.LatLng(lat, lon));
  loadShopMarkers(lat, lon);
  loadAreaResources(region1depth);


  //검색창 리셋

  document.querySelector('.initial-display')?.classList.remove('hidden');
  document.querySelector('.initial-display')?.classList.add('active');
  document.getElementById('expandedShopSearchArea')?.classList.add('hidden');
  document.getElementById('expandedAddressInputArea')?.classList.add('hidden');
}

/* ───────────── 샵 마커 / 뷰포트 ───────────── */
function loadShopMarkers(lat, lon) {
  fetch(`/api/shops?lat=${lat}&lon=${lon}`)
    .then(r => r.json())
    .then(list => {
      allShops = Array.isArray(list) ? list : [];
      updateMarkersInViewport();
      kakao.maps.event.addListener(map, 'idle', updateMarkersInViewport);
    })
    .catch(console.error);
}

function updateMarkersInViewport() {
  const { sw, ne } = map.getBounds();
  const visible = allShops.filter(({ latitude, longitude }) =>
    latitude  >= sw.getLat() && latitude  <= ne.getLat() &&
    longitude >= sw.getLng() && longitude <= ne.getLng()
  );
  clearMarkers();
  visible.forEach(shop => {
    const marker = new kakao.maps.Marker({
      map,
      position: new kakao.maps.LatLng(shop.latitude, shop.longitude),
    });
    const info = new kakao.maps.InfoWindow({
      content: `<div style="padding:5px;font-size:14px;">${shop.shopName}</div>`,
    });
    kakao.maps.event.addListener(marker, 'mouseover', () => info.open(map, marker));
    kakao.maps.event.addListener(marker, 'mouseout', () => info.close());
    kakao.maps.event.addListener(marker, 'click', () => location.href = `/shop/${shop.id}`);
    markers.push(marker);
  });
}

function clearMarkers() { markers.splice(0).forEach(m => m.setMap(null)); }

/* ───────────── 지역별 리소스 ───────────── */
function loadAreaResources(region1) {
  fetch(`/api/main-banners?region=${encodeURIComponent(region1)}`)
    .then(r => r.json())
    .then(b => Array.isArray(b) && b.length && renderAdBannerSlides(randomItems(b, 5)))
    .catch(console.error);

  fetch(`/api/recommend-shops?region=${encodeURIComponent(region1)}`)
    .then(r => r.json())
    .then(s => Array.isArray(s) && s.length && renderRecommendShopSlides(s))
    .catch(console.error);

  fetch(`/api/salon/designers/recommend?region=${encodeURIComponent(region1)}`)
    .then(r => r.json())
    .then(d => Array.isArray(d) && d.length && renderRecommendedDesigners(d))
    .catch(console.error);
}

/* ───────────── Renderer (배너·샵·디자이너) ───────────── */
const randomItems = (arr, n) => arr.slice().sort(() => 0.5 - Math.random()).slice(0, n);

function renderAdBannerSlides(banners) {
  const wrapper = dqs('.ad-banner-swiper .swiper-wrapper');
  if (!wrapper) return;
  wrapper.innerHTML = '';
  banners.forEach(b => {
    wrapper.insertAdjacentHTML('beforeend', `
      <div class="swiper-slide">
        <a href="/shop/${b.shopId}">
          <img src="${b.imgUrl}" alt="배너" style="width:100%;border-radius:12px;" />
        </a>
      </div>
    `);
  });
  window.adBannerSwiper?.update();
}

function renderRecommendShopSlides(shops) {
  const wrapper = dqs('.shopSwiper .shop-slider');
  if (!wrapper) return;
  wrapper.innerHTML = '';
  shops.forEach(s => {
    wrapper.insertAdjacentHTML('beforeend', `
      <div class="swiper-slide">
        <div class="shop-content" onclick="location.href='/shop/${s.id}'">
          <div class="skew-box">
            <img src="${s.shopImageDto?.imgUrl || '/images/default.png'}" class="img-fit" alt="" />
          </div>
          <div class="shop-info">
            <div class="shop-info-detail">
              <div class="shop-name">${s.shopName}</div>
              ${s.distance != null ? `<div class="shop-distance">${s.distance}km</div>` : ''}
            </div>
            <div class="shop-review-detail">
              <img src="/images/pointed-star.png" alt="">
              <div class="shop-rating">${s.avgRating}</div>
              <img src="/images/comment.png" alt="">
              <div class="shop-review-count">${s.reviewCount.toLocaleString()}</div>
            </div>
          </div>
        </div>
      </div>
    `);
  });
  window.shopSwiper?.update();
  adjustImageFitAll('.shopSwiper img.img-fit', 4 / 3);
}

function renderRecommendedDesigners(list) {
  const box   = dqs('#designer-recommend-box');
  const bubble= dqs('.designer-bubble');
  if (!box || !bubble) return;

  box.innerHTML    = '';
  bubble.innerHTML = '';

  list.forEach((d, idx) => {
    box.insertAdjacentHTML('beforeend', `
      <div class="best-designer-box">
        <div class="designer-profile-box">
          <img src="${d.profileImgUrl || '/images/default-profile.png'}"
          +               class="designer-photo img-fit" alt="">
        </div>
        <div class="designer-info-box">
          <div class="designer-name">${d.designerName}</div>
          <div class="designer-shop">${d.shopName}</div>
          <div class="designer-specialty-area">
            ${(d.tags || ['헤어']).map(t => `<span class="designer-specialty-tag">${t}</span>`).join('')}
          </div>
        </div>
      </div>
    `);
    if (idx === 0) renderDesignerBubble(d);
    box.lastElementChild.addEventListener('click', () => renderDesignerBubble(d));
  });

  adjustImageFitAll('#designer-recommend-box img.img-fit', 1);
}

function renderDesignerBubble(d) {
  const bubble = dqs('.designer-bubble');
  if (!bubble) return;
  const review = d.reviewImgList?.[0]?.imgUrl || '/images/default.png';
  bubble.innerHTML = `
    <div class="bubble-tall"></div>
    <div class="bubble-content">
      <div class="review-img">
        <img src="${review}" class="img-fit" alt="">
      </div>
      <div class="review-info-area">
        <div class="review-rating-box">
          <img src="/images/pointed-star.png" alt="">
          <div class="review-rating">${d.reviewRating?.toFixed?.(1) ?? '5.0'}</div>
        </div>
        <div class="review-create-at">${d.createAt ?? ''}</div>
      </div>
    </div>
  `;
  adjustImageFitAll('.designer-bubble img.img-fit', 4 / 3);
}

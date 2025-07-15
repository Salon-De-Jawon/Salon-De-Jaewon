// locationUitl.js

const buildKey = (userId) => userId ? `user_${userId}_location` : `guest_location`;

//로컬 스트리지에서 불러오기
export function getStoredLocation(userId) {
  const key = buildKey(userId);
  const raw = localStorage.getItem(key);
  if (!raw) return null;

  try {
    const parsed = JSON.parse(raw);
    const { userLatitude, userLongitude, region1depth, region2depth, userAddress } = parsed;

    if (
      userLatitude === undefined ||
      userLongitude === undefined ||
      !region1depth ||
      !region2depth ||
      !userAddress
    ) {
      return null;
    }

    return parsed;
  } catch (e) {
    console.error("위치 정보 파싱 오류:", e);
    return null;
  }
}

// 저장 함수
export function setStoredLocation(userId, locationData) {
  const key = buildKey(userId);
  try {
    localStorage.setItem(key, JSON.stringify(locationData));
  } catch (e) {
    console.error("위치 정보 저장 실패:", e);
  }
}

// 비회원 위치 제공 동의 여부 저장
export function setGuestLocationConsent(consent) {
  localStorage.setItem("guest_location_agree", consent ? "true" : "false");
}

// 비회원 위치 제공 동의 여부 조회
export function getGuestLocationConsent() {
  return localStorage.getItem("guest_location_agree") === "true";
}
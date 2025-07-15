export function renderStars(rating, container) {
  const fullStar = '★';
  const halfStar = '☆'; // 필요하면 반별 아이콘으로 바꿔도 됨
  const emptyStar = '☆';

  const rounded = Math.round(rating * 2) / 2;
  let html = '';

  for (let i = 1; i <= 5; i++) {
    if (i <= rounded - 0.5) {
      html += fullStar;
    } else if (i - 0.5 === rounded) {
      html += halfStar;
    } else {
      html += emptyStar;
    }
  }

  container.innerHTML = html;
}
 document.addEventListener('DOMContentLoaded', () => {
      const openDesignerModalBtn = document.getElementById('openDesignerModal');
      const designerModal = document.getElementById('designerModal');
      const closeButton = designerModal.querySelector('.close-button');
      const designerSearchInput = document.getElementById('designerSearchInput');
      const searchDesignerBtn = document.getElementById('searchDesignerBtn');
      const designerSearchResults = document.getElementById('designerSearchResults');
      const addSelectedDesignerBtn = document.getElementById('addSelectedDesignerBtn');
      const designerCardList = document.getElementById('designerCardList');

      let availableDesigners = []; // 백엔드에서 가져올 전체 디자이너 목록입니다.
      let selectedDesigner = null;

      // 디자이너를 가져오는 함수 (실제 AJAX 호출로 대체)
      const fetchDesigners = async () => {
        // 실제 애플리케이션에서는 여기에 AJAX 요청을 합니다. 예시:
        // const response = await fetch('/api/designers/all');
        // const data = await response.json();
        // availableDesigners = data;

        // 데모를 위해 더미 데이터를 사용합니다:
        availableDesigners = [
          { id: 3, designerName: '초급디자이너 민수', imgUrl: '/images/default_designer.jpg', workingYears: 1 },
          { id: 4, designerName: '베테랑 디자이너 유리', imgUrl: '/images/default_designer_2.jpg', workingYears: 8 },
          { id: 5, designerName: '실장 디자이너 지혜', imgUrl: '/images/default_designer_3.jpg', workingYears: 5 }
        ];
      };

      // 모달 열기
      openDesignerModalBtn.addEventListener('click', (e) => {
        e.preventDefault(); // 기본 링크 동작 방지
        designerModal.style.display = 'flex'; // 모달을 중앙에 배치하기 위해 flex 사용
        fetchDesigners(); // 모달이 열릴 때 디자이너 가져오기
        designerSearchResults.innerHTML = '<p class="no-results">검색 결과가 없습니다.</p>'; // 이전 검색 결과 지우기
        designerSearchInput.value = ''; // 검색 입력 필드 지우기
        selectedDesigner = null; // 선택된 디자이너 초기화
        addSelectedDesignerBtn.style.display = 'none'; // 추가 버튼 숨기기
      });

      // 모달 닫기
      closeButton.addEventListener('click', () => {
        designerModal.style.display = 'none';
      });

      // 모달 콘텐츠 외부 클릭 시 모달 닫기
      window.addEventListener('click', (event) => {
        if (event.target === designerModal) {
          designerModal.style.display = 'none';
        }
      });

      // 검색 기능
      searchDesignerBtn.addEventListener('click', () => {
        performSearch();
      });

      designerSearchInput.addEventListener('keyup', (event) => {
        if (event.key === 'Enter') {
          performSearch();
        }
      });

      const performSearch = () => {
        const searchTerm = designerSearchInput.value.trim().toLowerCase();
        designerSearchResults.innerHTML = ''; // 이전 결과 지우기
        selectedDesigner = null;
        addSelectedDesignerBtn.style.display = 'none';

        if (searchTerm === '') {
          designerSearchResults.innerHTML = '<p class="no-results">검색어를 입력해주세요.</p>';
          return;
        }

        const filteredDesigners = availableDesigners.filter(designer =>
          designer.name.toLowerCase().includes(searchTerm) || designer.phone.includes(searchTerm)
        );

        if (filteredDesigners.length > 0) {
          filteredDesigners.forEach(designer => {
            const designerItem = document.createElement('div');
            designerItem.classList.add('search-result-item');
            designerItem.setAttribute('data-designer-id', designer.id);
            designerItem.innerHTML = `
              <img src="${designer.imgUrl}" alt="${designer.designerName} 사진" />
              <div class="search-result-info">
                <p class="name">${designer.designerName}</p>
                <p>경력: ${designer.workingYears}년차</p>
              </div>
            `;
            designerItem.addEventListener('click', () => {
              // 이전에 선택된 항목에서 'selected' 클래스 제거
              const currentSelected = designerSearchResults.querySelector('.search-result-item.selected');
              if (currentSelected) {
                currentSelected.classList.remove('selected');
              }
              // 클릭된 항목에 'selected' 클래스 추가
              designerItem.classList.add('selected');
              selectedDesigner = designer;
              addSelectedDesignerBtn.style.display = 'block'; // 디자이너가 선택되면 추가 버튼 표시
            });
            designerSearchResults.appendChild(designerItem);
          });
        } else {
          designerSearchResults.innerHTML = '<p class="no-results">검색 결과가 없습니다.</p>';
        }
      };

      // 선택된 디자이너를 목록에 추가
      addSelectedDesignerBtn.addEventListener('click', () => {
        if (selectedDesigner) {
          // 중복을 방지하기 위해 디자이너가 이미 목록에 있는지 확인
          const existingDesignerTitles = Array.from(designerCardList.querySelectorAll('.designer-card .designer-title')).map(titleElement =>
            titleElement.textContent.trim()
          );

          if (existingDesignerTitles.includes(selectedDesigner.name)) {
            alert('이미 추가된 디자이너입니다.');
            return;
          }

          const newDesignerCard = document.createElement('div');
          newDesignerCard.classList.add('designer-card');
          newDesignerCard.innerHTML = `
            <img src="${selectedDesigner.imgUrl}" alt="${selectedDesigner.designerName} 사진" class="designer-photo" />
            <div class="designer-info">
              <p class="designer-title">${selectedDesigner.designerName}</p>
              <p class="designer-desc">경력: ${selectedDesigner.workingYears}년</p>
              <div class="designer-stats">
                <span>🤍 0</span>
                <span>💬 0</span>
              </div>
            </div>
            <a href="/shop/designer/${selectedDesigner.id}" class="btn-book">관리</a>
          `;
          designerCardList.appendChild(newDesignerCard);
          designerModal.style.display = 'none'; // 추가 후 모달 닫기
          selectedDesigner = null; // 선택된 디자이너 초기화
          addSelectedDesignerBtn.style.display = 'none'; // 추가 버튼 숨기기
          designerSearchInput.value = ''; // 검색 입력 필드 지우기
          designerSearchResults.innerHTML = '<p class="no-results">검색 결과가 없습니다.</p>'; // 결과 지우기
        } else {
          alert('디자이너를 선택해주세요.');
        }
      });
    });
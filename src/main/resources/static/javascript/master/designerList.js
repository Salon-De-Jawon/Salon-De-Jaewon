document.addEventListener('DOMContentLoaded', () => {
  const openDesignerModalBtn = document.getElementById('openDesignerModal');
  const designerModal = document.getElementById('designerModal');
  const closeButton = designerModal.querySelector('.close-button');
  const designerSearchInput = document.getElementById('designerSearchInput');
  const searchDesignerBtn = document.getElementById('searchDesignerBtn');
  const designerSearchResults = document.getElementById('designerSearchResults');
  const addSelectedDesignerBtn = document.getElementById('addSelectedDesignerBtn');
  const designerCardList = document.getElementById('designerCardList');

  let selectedDesigner = null; // 현재 모달에서 선택된 디자이너 (DesignerResultDto 형태)

  // --- AJAX 호출로 디자이너를 가져오는 함수 (DesignerResultDto 반환) ---
  const fetchDesignersFromBackend = async (searchName = '', searchTel = '') => {
    try {
      const queryParams = new URLSearchParams();
      if (searchName) {
        queryParams.append('name', searchName);
      }
      if (searchTel) {
        queryParams.append('tel', searchTel);
      }

      const response = await fetch(`/master/designer-search?${queryParams.toString()}`);
      console.log('Backend response status:', response.status); // 응답 상태 코드 확인
      console.log('Backend response OK:', response.ok);       // 응답 성공 여부 확인

      if (!response.ok) {
        const errorText = await response.text(); // 오류 메시지 확인
        console.error('HTTP error response body:', errorText);
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log('Received data from backend:', data); // 받은 데이터 확인
      return data;
    } catch (error) {
      console.error('디자이너 검색 정보를 가져오는 데 실패했습니다:', error);
      return [];
    }
  };

  // 모달 열기
  openDesignerModalBtn.addEventListener('click', (e) => {
    e.preventDefault();
    designerModal.style.display = 'flex';
    designerSearchResults.innerHTML = '<p class="no-results">검색어를 입력해주세요.</p>';
    designerSearchInput.value = '';
    selectedDesigner = null;
    addSelectedDesignerBtn.style.display = 'none';
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

  const performSearch = async () => {
    const searchTerm = designerSearchInput.value.trim();
    designerSearchResults.innerHTML = '';
    selectedDesigner = null;
    addSelectedDesignerBtn.style.display = 'none';

    if (searchTerm === '') {
      designerSearchResults.innerHTML = '<p class="no-results">검색어를 입력해주세요.</p>';
      return;
    }

    // DesignerSearchDto를 활용하는 백엔드 API에 맞춰 검색어를 전달합니다.
    // 여기서는 이름과 전화번호를 모두 searchTerm으로 전달하여 백엔드에서 처리하도록 가정합니다.
    const searchName = searchTerm;
    const searchTel = searchTerm; // 필요하다면 이 필드를 사용. 현재 백엔드에서 DesignerSearchDto로 받으므로 RequestParam으로 명확히 구분됩니다.

    // 실제 백엔드 API 호출. DesignerResultDto 리스트를 반환합니다.
    const foundDesigners = await fetchDesignersFromBackend(searchName, searchTel);


    if (foundDesigners.length > 0) {
      foundDesigners.forEach(designer => {
        const designerItem = document.createElement('div');
        designerItem.classList.add('search-result-item');
        // DesignerResultDto의 id를 사용합니다.
        designerItem.setAttribute('data-designer-id', designer.id);
        designerItem.innerHTML = `
          <img src="${designer.imgUrl || '/images/default_designer.jpg'}" alt="${designer.designerName} 사진" />
          <div class="search-result-info">
            <p class="name">${designer.designerName}</p>
            <p>경력: ${designer.workingYears}년차</p>
          </div>
        `;
        designerItem.addEventListener('click', () => {
          const currentSelected = designerSearchResults.querySelector('.search-result-item.selected');
          if (currentSelected) {
            currentSelected.classList.remove('selected');
          }
          designerItem.classList.add('selected');
          selectedDesigner = designer; // DesignerResultDto 객체 전체를 저장
          addSelectedDesignerBtn.style.display = 'block';
        });
        designerSearchResults.appendChild(designerItem);
      });
    } else {
      designerSearchResults.innerHTML = '<p class="no-results">검색 결과가 없습니다.</p>';
    }
  };

  // 선택된 디자이너를 목록에 추가
  addSelectedDesignerBtn.addEventListener('click', async () => {
       if (selectedDesigner) {
         const existingDesignerTitles = Array.from(designerCardList.querySelectorAll('.designer-card .designer-title'))
           .map(titleElement => titleElement.textContent.trim());

         if (existingDesignerTitles.includes(selectedDesigner.designerName)) {
           alert('이미 추가된 디자이너입니다.');
           return;
         }

         // --- 백엔드로 디자이너 추가 요청 보내기 (RequestParam 방식) ---
         try {
           // POST 요청이지만, RequestParam으로 보내므로 쿼리 스트링 형태로 데이터를 보냅니다.
           // Content-Type은 'application/x-www-form-urlencoded' 또는 생략 가능
           const addParams = new URLSearchParams();
           addParams.append('designerId', selectedDesigner.id);

           const response = await fetch(`/master/add-designer?${addParams.toString()}`, { // URL에 파라미터 추가
             method: 'POST',
             headers: {
               // 'Content-Type': 'application/x-www-form-urlencoded' // 필요시 명시적으로 설정
               // 'X-CSRF-TOKEN': 'YOUR_CSRF_TOKEN' // CSRF 토큰 사용 시 필요
             },
             // body는 비워둡니다. 데이터는 URL 쿼리 파라미터에 포함됩니다.
           });

           if (response.status === 400) {
               const errorResponse = await response.text();
               console.error("Add designer 400 error:", errorResponse);
               alert('디자이너 추가 실패: ' + (errorResponse || '이미 추가되었거나 유효하지 않은 디자이너입니다.'));
               return;
           }

           if (!response.ok) {
             const errorText = await response.text();
             console.error('Failed to add designer:', response.status, errorText);
             alert('디자이너 추가에 실패했습니다. 서버 오류.');
             return;
           }

           const newDesignerData = await response.json();

           const newDesignerCard = document.createElement('div');
           newDesignerCard.classList.add('designer-card');
           newDesignerCard.innerHTML = `
             <img src="${newDesignerData.imgUrl || '/images/default_designer.jpg'}" alt="${newDesignerData.name} 사진" class="designer-photo" />
             <div class="designer-info">
               <p class="designer-title">${newDesignerData.name}</p>
               <p class="designer-desc">${newDesignerData.profileSummary}</p>
               <div class="designer-stats">
                 <span>🤍 ${newDesignerData.likeCount}</span>
                 <span>💬 ${newDesignerData.reviewCount}</span>
               </div>
             </div>
             <a href="/shop/designer/${newDesignerData.id}" class="btn-book">관리</a>
           `;
           designerCardList.appendChild(newDesignerCard);

           designerModal.style.display = 'none';
           selectedDesigner = null;
           addSelectedDesignerBtn.style.display = 'none';
           designerSearchInput.value = '';
           designerSearchResults.innerHTML = '<p class="no-results">검색 결과가 없습니다.</p>';

         } catch (error) {
           console.error('디자이너 추가 중 네트워크 또는 파싱 오류 발생:', error);
           alert('디자이너 추가 중 예상치 못한 오류가 발생했습니다.');
         }
       } else {
         alert('디자이너를 선택해주세요.');
       }
     });
  });
});
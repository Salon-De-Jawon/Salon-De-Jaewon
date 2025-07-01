document.addEventListener('DOMContentLoaded', function () {
  const toggleButton = document.getElementById('btn-toggle-attendance');
  const leaveButton = document.getElementById('btn-request-leave');
  const timeDisplay = document.getElementById('attendance-time-display');

  if (!toggleButton || !leaveButton) return;

  function getTodayString() {
    const now = new Date();
    return now.toISOString().slice(0, 10); // YYYY-MM-DD
  }

  // 저장된 출근/퇴근 시간 (문자열)
  const savedStart = localStorage.getItem('attendanceStartTime');
  const savedLeave = localStorage.getItem('attendanceLeaveTime');
  const today = getTodayString();

  // 시간 문자열에서 날짜(YYYY-MM-DD) 추출 함수
  function extractDate(dateTimeStr) {
    if (!dateTimeStr) return null;
    // 예: '2025. 07. 01. 09:10' 형태일 경우 공백 기준 앞부분만 추출하거나,
    // 혹은 정확한 포맷에 맞게 파싱해야 함
    // 여기서는 'YYYY. MM. DD.' 형태라면 정규식으로 숫자만 추출
    const match = dateTimeStr.match(/(\d{4})[.\-년 ]+(\d{1,2})[.\-월 ]+(\d{1,2})/);
    if (!match) return null;
    const y = match[1].padStart(4, '0');
    const m = match[2].padStart(2, '0');
    const d = match[3].padStart(2, '0');
    return `${y}-${m}-${d}`;
  }

  const savedStartDate = extractDate(savedStart);
  const savedLeaveDate = extractDate(savedLeave);

  let isWorking = false;

  if (savedLeaveDate === today) {
    toggleButton.disabled = true;
    toggleButton.textContent = '오늘 퇴근 완료';
    toggleButton.style.backgroundColor = '#aaa';
    timeDisplay.innerHTML = `출근시간: ${savedStart || '-'}<br>퇴근시간: ${savedLeave || '-'}`;
    isWorking = false;
  } else if (savedStartDate === today) {
    toggleButton.disabled = false;
    toggleButton.textContent = '퇴근하기';
    toggleButton.style.backgroundColor = '#566a8e';
    timeDisplay.innerHTML = `출근시간: ${savedStart || '-'}`;
    isWorking = true;
  } else {
    toggleButton.disabled = false;
    toggleButton.textContent = '출근하기';
    toggleButton.style.backgroundColor = '#8c9ed9';
    timeDisplay.innerHTML = '';
    isWorking = false;
  }

  toggleButton.addEventListener('click', function () {
    const now = new Date();
    const formattedTime = now.toLocaleString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',

    });

    if (!isWorking) {
      if (!confirm('출근하시겠습니까?')) return;

      isWorking = true;
      toggleButton.textContent = '퇴근하기';
      toggleButton.style.backgroundColor = '#566a8e';
      timeDisplay.innerHTML = `출근시간: ${formattedTime}`;
      localStorage.setItem('attendanceStartTime', formattedTime);
      localStorage.removeItem('attendanceLeaveTime');
    } else {
      if (!confirm('퇴근하시겠습니까?')) return;

      isWorking = false;
      toggleButton.textContent = '오늘 퇴근 완료';
      toggleButton.style.backgroundColor = '#aaa';
      toggleButton.disabled = true;
      timeDisplay.innerHTML += `<br>퇴근시간: ${formattedTime}`;
      localStorage.setItem('attendanceLeaveTime', formattedTime);
    }
  });

  leaveButton.addEventListener('click', function () {
    alert('📅 휴가 신청은 추후 구현될 예정입니다!');
  });
});

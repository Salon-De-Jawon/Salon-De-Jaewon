document.addEventListener('DOMContentLoaded', function () {
  const toggleButton = document.getElementById('btn-toggle-attendance');
  const leaveButton = document.getElementById('btn-request-leave');
  const timeDisplay = document.getElementById('attendance-time-display');

  if (!toggleButton || !leaveButton) return;

  // 근태 상태 (출근했으면 true)
  let isWorking = false;

  // 페이지 로딩 시 출퇴근 상태를 서버에서 받아서 초기화하는 함수 (선택사항)
  async function initAttendanceStatus() {
    try {
      const res = await fetch('/api/attendance/status');
      if (res.ok) {
        const data = await res.json();
        isWorking = data.isWorking;

        if (data.clockIn) {
          timeDisplay.innerHTML = `출근시간: ${data.clockIn}`;
          toggleButton.textContent = isWorking ? '퇴근하기' : '오늘 퇴근 완료';
          toggleButton.disabled = !isWorking ? true : false;
          toggleButton.style.backgroundColor = isWorking ? '#566a8e' : '#aaa';
        } else {
          toggleButton.textContent = '출근하기';
          toggleButton.disabled = false;
          toggleButton.style.backgroundColor = '#8c9ed9';
          timeDisplay.innerHTML = '';
        }
      }
    } catch (e) {
      console.error('출퇴근 상태 불러오기 실패', e);
    }
  }

  initAttendanceStatus();

  toggleButton.addEventListener('click', async function () {
    const now = new Date();
    const isoTime = now.toISOString();

    if (!isWorking) {
      if (!confirm('출근하시겠습니까?')) return;

      try {
        const res = await fetch('/api/attendance?type=START', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(isoTime),
        });

        if (res.ok) {
          isWorking = true;
          toggleButton.textContent = '퇴근하기';
          toggleButton.style.backgroundColor = '#566a8e';
          timeDisplay.innerHTML = `출근시간: ${now.toLocaleString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
          })}`;
        } else {
          alert('출근 등록에 실패했습니다.');
        }
      } catch (e) {
        alert('서버 통신 중 오류가 발생했습니다.');
      }

    } else {
      if (!confirm('퇴근하시겠습니까?')) return;

      try {
        const res = await fetch('/api/attendance?type=END', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(isoTime),
        });

        if (res.ok) {
          isWorking = false;
          toggleButton.textContent = '오늘 퇴근 완료';
          toggleButton.style.backgroundColor = '#aaa';
          toggleButton.disabled = true;
          timeDisplay.innerHTML += `<br>퇴근시간: ${now.toLocaleString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
          })}`;
        } else {
          alert('퇴근 등록에 실패했습니다.');
        }
      } catch (e) {
        alert('서버 통신 중 오류가 발생했습니다.');
      }
    }
  });

  leaveButton.addEventListener('click', function () {
    alert('📅 휴가 신청은 추후 구현될 예정입니다!');
  });
});

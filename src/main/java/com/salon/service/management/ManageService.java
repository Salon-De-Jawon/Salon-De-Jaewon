package com.salon.service.management;

import com.salon.constant.AttendanceStatus;
import com.salon.constant.LeaveStatus;
import com.salon.dto.management.LeaveRequestDto;
import com.salon.entity.Member;
import com.salon.entity.management.LeaveRequest;
import com.salon.entity.management.ShopDesigner;
import com.salon.entity.management.master.Attendance;
import com.salon.repository.management.LeaveRequestRepo;
import com.salon.repository.management.PaymentRepo;
import com.salon.repository.management.ShopDesignerRepo;
import com.salon.repository.management.master.AttendanceRepo;
import com.salon.repository.management.master.DesignerServiceRepo;
import com.salon.repository.management.master.ServiceRepo;
import com.salon.util.DayOffUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ManageService {

    private final AttendanceRepo attendanceRepo;
    private final DesignerServiceRepo designerServiceRepo;
    private final ServiceRepo serviceRepo;
    private final LeaveRequestRepo leaveRequestRepo;
    private final PaymentRepo paymentRepo;
    private final ShopDesignerRepo shopDesignerRepo;


    // 디자이너 출근시 메서드
    @Transactional
    public void clockIn(ShopDesigner designer){

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        int lateMin = designer.getShop().getLateMin(); // 지각 유예시간

        // 휴무일인지 확인
        if (DayOffUtil.isDayOff(designer.getShop().getDayOff(), today.getDayOfWeek())) {
            throw new IllegalStateException("디자이너 쉬는날");
        }

        // 이미 출근 기록이 있으면 예외
        boolean exists = attendanceRepo.existsByDesignerIdAndClockInBetween(
                designer.getId(),
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
        if (exists) {
            throw new IllegalStateException("이미 출근핢");
        }

        // 지각 여부 판단
        LocalTime scheduledStart = designer.getScheduledStartTime();
        boolean isLate = now.toLocalTime().isAfter(scheduledStart.plusMinutes(lateMin));

        Attendance attendance = new Attendance();
        attendance.setDesigner(designer);
        attendance.setClockIn(now);
        attendance.setStatus(isLate ? AttendanceStatus.LATE : AttendanceStatus.PRESENT);

        attendanceRepo.save(attendance);
    }

    // 디자이너 퇴근시 메서드
    @Transactional
    public void clockOut(ShopDesigner designer) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        int earlyLeaveMin = designer.getShop().getEarlyLeaveMin();

        // 오늘 출근 기록 가져오기
        Attendance attendance = attendanceRepo.findByDesignerIdAndClockInBetween(designer.getId(), today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        ).orElseThrow(() -> new IllegalStateException("아직 출근 ㄴㄴ"));

        // 이미 퇴근한 경우
        if (attendance.getClockOut() != null) {
            throw new IllegalStateException("이미 퇴근핢");
        }

        // 조퇴 여부 판단
        LocalTime scheduledEnd = designer.getScheduledEntTime();
        boolean isEarly = now.toLocalTime().isBefore(scheduledEnd.minusMinutes(earlyLeaveMin));

        attendance.setClockOut(now);

        if (isEarly) {
            attendance.setStatus(AttendanceStatus.LEFT_EARLY);
        } else if (attendance.getStatus() == AttendanceStatus.LATE) {
            // 지각한 상태로 퇴근하면 유지
        } else {
            attendance.setStatus(AttendanceStatus.LEFT);
        }
    }

    // 디자이너 휴가 신청 시
    @Transactional
    public void saveLeaveRequest(LeaveRequestDto dto, ShopDesigner designer){

        dto.setStatus(LeaveStatus.REQUESTED);
        LeaveRequest leaveRequest = dto.to(designer);

        leaveRequestRepo.save(leaveRequest);

    }
    
    // 디자이너 개인 출퇴근 목록




}

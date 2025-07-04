package com.salon.service.management;

import com.salon.constant.AttendanceStatus;
import com.salon.constant.LeaveStatus;
import com.salon.dto.management.WeekDto;
import com.salon.dto.management.*;
import com.salon.entity.management.LeaveRequest;
import com.salon.entity.management.Payment;
import com.salon.entity.management.ShopDesigner;
import com.salon.entity.management.master.Attendance;
import com.salon.entity.shop.Reservation;
import com.salon.repository.management.LeaveRequestRepo;
import com.salon.repository.management.PaymentRepo;
import com.salon.repository.management.ShopDesignerRepo;
import com.salon.repository.management.master.AttendanceRepo;
import com.salon.repository.management.master.DesignerServiceRepo;
import com.salon.repository.management.master.ServiceRepo;
import com.salon.repository.shop.ReservationRepo;
import com.salon.util.DayOffUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageService {

    private final AttendanceRepo attendanceRepo;
    private final DesignerServiceRepo designerServiceRepo;
    private final ServiceRepo serviceRepo;
    private final LeaveRequestRepo leaveRequestRepo;
    private final PaymentRepo paymentRepo;
    private final ShopDesignerRepo shopDesignerRepo;
    private final ReservationRepo reservationRepo;


    // 메인페이지 출력용 메서드
    public DesignerMainPageDto getMainPage(Long memberId){

        ShopDesigner designer = shopDesignerRepo.findByDesigner_Member_IdAndIsActiveTrue(memberId);

        DesignerMainPageDto dto = new DesignerMainPageDto();

        dto.setTodayReservationCount(reservationRepo.countTodayReservations(designer.getId()));
        dto.setTodayCompletedPayments(paymentRepo.countTodayCompletePayments(designer.getId()) );
        dto.setTodayNewCustomers(reservationRepo.countTodayNewCustomers(designer.getId()));

        List<Reservation> todayRes = reservationRepo.findTodayReservations(designer.getId());

        List<TodayScheduleDto> dtoList = new ArrayList<>();
        for(Reservation res : todayRes) {
            dtoList.add(TodayScheduleDto.from(res));
        }

        dto.setTodayScheduleList(dtoList);

        return dto;
    }

    // 디자이너 출근시 메서드
    @Transactional
    public void clockIn(Long memberId){

        ShopDesigner designer = shopDesignerRepo.findByDesigner_Member_IdAndIsActiveTrue(memberId);

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        int lateMin = designer.getShop().getLateMin(); // 지각 유예시간

        // 휴무일인지 확인
        if (DayOffUtil.isDayOff(designer.getShop().getDayOff(), today.getDayOfWeek())) {
            throw new IllegalStateException("디자이너 쉬는날");
        }

        // 이미 출근 기록이 있으면 예외
        boolean exists = attendanceRepo.existsByShopDesignerIdAndClockInBetween(
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
        attendance.setShopDesigner(designer);
        attendance.setClockIn(now);
        attendance.setStatus(isLate ? AttendanceStatus.LATE : AttendanceStatus.PRESENT);

        attendanceRepo.save(attendance);
    }

    // 디자이너 퇴근시 메서드
    @Transactional
    public void clockOut(Long memberId) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        ShopDesigner designer = shopDesignerRepo.findByDesigner_Member_IdAndIsActiveTrue(memberId);
        int earlyLeaveMin = designer.getShop().getEarlyLeaveMin();

        // 오늘 출근 기록 가져오기
        Attendance attendance = attendanceRepo.findByShopDesignerIdAndClockInBetween(designer.getId(), today.atStartOfDay(),
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
    public void saveLeaveRequest(LeaveRequestDto dto, Long memberId){

        ShopDesigner designer = shopDesignerRepo.findByDesigner_Member_IdAndIsActiveTrue(memberId);

        dto.setStatus(LeaveStatus.REQUESTED);
        LeaveRequest leaveRequest = dto.to(designer);

        leaveRequestRepo.save(leaveRequest);

    }
    
//    // 디자이너 개인 출퇴근 목록
//    public List<AttendanceListDto> getAttendanceList(Long memberId, LocalDate selectedDate){
//
//        ShopDesigner designer = shopDesignerRepo.findByDesigner_Member_IdAndIsActiveTrue(memberId);
//
//        // selectedDate 00 : 00
//        LocalDateTime start = selectedDate.atStartOfDay();
//        // selectedDate 23 : 59 : 59.999
//        LocalDateTime end = selectedDate.atTime(LocalTime.MAX);
//        List<Attendance> attendanceList = attendanceRepo.findByShopDesignerIdAndClockInBetweenOrderByIdDesc(designer.getId(), start, end);
//
//        List<AttendanceListDto> dtoList = new ArrayList<>();
//
//        for(Attendance att : attendanceList){
//            dtoList.add(AttendanceListDto.from(att));
//        }
//
//        return dtoList;
//    }

    // 근태 목록 페이지 주간 목록 생성
    public List<WeekDto> getWeeksOfMonth(LocalDate monthStart){

        List<WeekDto> weeks = new ArrayList<>();
        LocalDate firstDayOfMonth = monthStart.withDayOfMonth(1);
        LocalDate lastDayOfMonth = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        LocalDate date = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDate = lastDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));


        while ( !date.isAfter(endDate) ) {
            LocalDate start = date;
            LocalDate end = date.plusDays(6);
            weeks.add(new WeekDto(start, end, getLabel(weeks.size() + 1, start, end)));
            date = date.plusWeeks(1);
        }

        return weeks;
    }

    // 위의 주간 라벨 표시용
    private String getLabel(int weekNum, LocalDate start, LocalDate end) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM.dd");
        return weekNum + "주차 (" + start.format(fmt) + "~" + end.format(fmt) + ")";
    }

    // 디자이너 개인 예약 현황
    public List<ReservationListDto> getReservationList(Long memberId, LocalDate selectedDate){

        ShopDesigner designer = shopDesignerRepo.findByDesigner_Member_IdAndIsActiveTrue(memberId);
        LocalDateTime start = selectedDate.atStartOfDay();
        LocalDateTime end = selectedDate.atTime(LocalTime.MAX);

        List<Reservation> reservationListlist =
                reservationRepo.findByShopDesignerIdAndReservationDateBetweenOrderByReservationDateDesc(designer.getId(), start, end);

        List<ReservationListDto> dtoList = new ArrayList<>();
        for(Reservation entity : reservationListlist) {

            ReservationListDto dto = ReservationListDto.from(entity);
            // 당일 예약 구분
            dto.setToday(entity.getReservationDate().toLocalDate().isEqual(LocalDate.now()));

            dtoList.add(dto);
        }

        return dtoList;
    }

    // 디자이너 매출 목록
    public List<PaymentListDto> getPaymentList(Long designerId){

        List<Payment> paymentList = paymentRepo.findByDesignerOrderByPayDate(designerId);

        List<PaymentListDto> dtoList = new ArrayList<>();
        for(Payment payment : paymentList) {
            dtoList.add(PaymentListDto.from(payment));
        }

        return dtoList;
    }
    
    // 결제내역 등록
    @Transactional
    public void savePayment(PaymentForm form){

        Reservation reservation = reservationRepo.findById(form.getReservationId()).orElse(null);
        ShopDesigner designer = form.getDesignerId() != null ? shopDesignerRepo.findById(form.getDesignerId()).orElse(null) : null;

        Payment payment = form.to(reservation, designer);

        paymentRepo.save(payment);
    }








}

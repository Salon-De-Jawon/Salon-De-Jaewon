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
import com.salon.entity.shop.Shop;
import com.salon.repository.management.LeaveRequestRepo;
import com.salon.repository.management.PaymentRepo;
import com.salon.repository.management.ShopDesignerRepo;
import com.salon.repository.management.master.AttendanceRepo;
import com.salon.repository.management.master.DesignerServiceRepo;
import com.salon.repository.management.master.ServiceRepo;
import com.salon.repository.shop.ReservationRepo;
import com.salon.repository.shop.ShopRepo;
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
import java.util.Optional;

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
    private final ShopRepo shopRepo;


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
    public void clockIn(Long memberId, LocalDateTime clockInTime) {

        ShopDesigner designer = shopDesignerRepo.findByDesigner_Member_IdAndIsActiveTrue(memberId);
        LocalDateTime start = clockInTime.toLocalDate().atStartOfDay();
        LocalDateTime end = clockInTime.toLocalDate().atTime(LocalTime.MAX);


        // 오늘 이미 출근 기록이 있으면 예외 혹은 무시 처리 가능
        boolean exists = attendanceRepo.existsByShopDesignerIdAndClockInBetween(designer.getId(), start, end);
        if (exists) {
            throw new IllegalStateException("이미 오늘 출근 기록이 존재합니다.");
        }

        LocalTime officialStart = designer.getShop().getOpenTime();
        int lateMin = designer.getShop().getLateMin();
        LocalTime clockIn = clockInTime.toLocalTime();

        Attendance attendance = new Attendance();
        attendance.setShopDesigner(designer);
        attendance.setClockIn(clockInTime);
        // 출근 지각 여부
        if (clockIn.isAfter(officialStart.plusMinutes(lateMin))) {
            attendance.setStatus(AttendanceStatus.LATE);
        } else {
            attendance.setStatus(AttendanceStatus.PRESENT);
        }

        attendanceRepo.save(attendance);
    }

    // 디자이너 퇴근시 메서드
    @Transactional
    public void clockOut(Long memberId, LocalDateTime clockOutTime) {
        ShopDesigner designer = shopDesignerRepo.findByDesigner_Member_IdAndIsActiveTrue(memberId);

        LocalDateTime start = clockOutTime.toLocalDate().atStartOfDay();
        LocalDateTime end = clockOutTime.toLocalDate().atTime(LocalTime.MAX);

        // 오늘 출근 기록 조회
        Attendance attendance = attendanceRepo.findByShopDesignerIdAndClockInBetween(designer.getId(), start, end).orElseThrow(
                () -> new IllegalArgumentException("디자이너 못찾음"));

        attendance.setClockOut(clockOutTime);

        // 지각, 조퇴 계산 (예시)
        Shop shop = designer.getShop();

        LocalTime officialStart = shop.getOpenTime();
        LocalTime officialEnd = shop.getCloseTime();

        int earlyLeaveMin = shop.getEarlyLeaveMin();
        LocalTime clockOutLocalTime = clockOutTime.toLocalTime();


        // 퇴근 조퇴 여부
        if (clockOutLocalTime.isBefore(officialEnd.minusMinutes(earlyLeaveMin))) {
            attendance.setStatus(AttendanceStatus.LEFT_EARLY);
        } else {
            attendance.setStatus(AttendanceStatus.LEFT);
        }

        attendanceRepo.save(attendance);
    }

    // 오늘 출퇴근 시간 출력용
    public AttendanceStatusDto getTodayStatus(Long memberId){

        ShopDesigner designer = shopDesignerRepo.findByDesigner_Member_IdAndIsActiveTrue(memberId);

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);


        Optional<Attendance> attendanceOpt = attendanceRepo.findByShopDesignerIdAndClockInBetween(designer.getId(), start, end);

        if (attendanceOpt.isEmpty()) {
            return new AttendanceStatusDto(false, null, null);
        }

        Attendance attendance = attendanceOpt.get();

        String clockInStr = attendance.getClockIn() == null ? null :
                attendance.getClockIn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        String clockOutStr = attendance.getClockOut() == null ? null :
                attendance.getClockOut().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        boolean isWorking = attendance.getClockOut() == null;

        return new AttendanceStatusDto(isWorking, clockInStr, clockOutStr);

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

    // 디자이너 매출 목록 (일별)
    public List<PaymentListDto> getPaymentList(Long memberId, LocalDate selectedDate){

        LocalDateTime start = selectedDate.atStartOfDay();
        LocalDateTime end = selectedDate.atTime(LocalTime.MAX);
        ShopDesigner designer = shopDesignerRepo.findByDesigner_Member_IdAndIsActiveTrue(memberId);

        List<Payment> paymentList = paymentRepo.findByDesignerAndPeriod(designer.getId(), start, end);

        System.out.println(paymentList);

        List<PaymentListDto> dtoList = new ArrayList<>();
        for(Payment payment : paymentList) {
            dtoList.add(PaymentListDto.from(payment));
        }

        return dtoList;
    }
    
    // 결제내역 등록
    @Transactional
    public void savePayment(PaymentForm form, Long memberId){

        Reservation reservation = null;
        if(form.getReservationId() != null){
            reservation = reservationRepo.findById(form.getReservationId())
                    .orElseThrow(() -> new IllegalArgumentException("예약 ID가 유효하지 않습니다."));
        }
        ShopDesigner designer = shopDesignerRepo.findByDesigner_Member_IdAndIsActiveTrue(memberId);

        Payment payment = form.to(reservation, designer);

        paymentRepo.save(payment);
    }








}

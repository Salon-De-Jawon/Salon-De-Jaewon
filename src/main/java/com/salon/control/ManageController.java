package com.salon.control;

import com.salon.config.CustomUserDetails;
import com.salon.dto.management.*;
import com.salon.entity.Member;
import com.salon.entity.management.ShopDesigner;
import com.salon.entity.management.master.Attendance;
import com.salon.repository.management.ShopDesignerRepo;
import com.salon.repository.management.master.AttendanceRepo;
import com.salon.repository.management.master.TicketRepo;
import com.salon.service.management.ManageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/manage")
public class ManageController {

    private final ManageService manageService;
    private final AttendanceRepo attendanceRepo;
    private final ShopDesignerRepo shopDesignerRepo;
    private final TicketRepo ticketRepo;

    // 메인페이지
    @GetMapping("")
    public String getMain(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){

        Long memberId = userDetails.getMember().getId();
        DesignerMainPageDto dto = manageService.getMainPage(memberId);

        model.addAttribute("dto", dto);

        return "management/main";
    }

    // 근태 관리
    @GetMapping("/attendance")
    public String attendance(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().withDayOfMonth(1)}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate monthStart,
                             @RequestParam(required = false) Integer selectedWeek,
                             Model model){

        ShopDesigner designer = shopDesignerRepo.findByDesigner_Member_IdAndIsActiveTrue(userDetails.getMember().getId());

        LocalDate startOfMonth = monthStart.withDayOfMonth(1);
        LocalDate endOfMonth = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
        LocalDate today =LocalDate.now();

        List<WeekDto> weeks = manageService.getWeeksOfMonth(startOfMonth);
        int defaultWeekIndex = 0;


        for (int i = 0; i < weeks.size(); i++) {
            WeekDto week = weeks.get(i);
            if (!today.isBefore(week.getStartDate()) && !today.isAfter(week.getEndDate())) {
                defaultWeekIndex = i;
                break;
            }
        }

        int currentWeekIndex = selectedWeek != null ? selectedWeek : defaultWeekIndex;

        WeekDto selectedWeekDto = weeks.get(currentWeekIndex);

        List<Attendance> attendanceList = attendanceRepo.findByShopDesignerIdAndClockInBetweenOrderByIdDesc(
                designer.getId(), selectedWeekDto.getStartDate().atStartOfDay(), selectedWeekDto.getEndDate().atTime(LocalTime.MAX));

        List<AttendanceListDto> dtoList = new ArrayList<>();
        for(Attendance att : attendanceList){
            dtoList.add(AttendanceListDto.from(att));
        }


        model.addAttribute("weeks", weeks);
        model.addAttribute("selectedWeekIndex", currentWeekIndex);
        model.addAttribute("monthStart", startOfMonth);
        model.addAttribute("monthStartStr", startOfMonth.format(DateTimeFormatter.ISO_LOCAL_DATE)); // 날짜 표시용
        model.addAttribute("attendanceList", dtoList);


        return "management/attendance";
    }

    // 출퇴근용 json
    @PostMapping("/attendance/{type}")
    public ResponseEntity<?> saveAttendance(
            @RequestBody String isoTime,
            @PathVariable("type") String type,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long memberId = userDetails.getMember().getId();
        String trimmed = isoTime.replace("\"", "");
        LocalDateTime time = OffsetDateTime.parse(trimmed).toLocalDateTime();

        if ("start".equalsIgnoreCase(type)) {
            manageService.clockIn(memberId, time);
        } else if ("end".equalsIgnoreCase(type)) {
            manageService.clockOut(memberId, time);
        } else {
            return ResponseEntity.badRequest().body("Invalid attendance type");
        }

        return ResponseEntity.ok().build();
    }

    // 페이지 초기화 시 출퇴근 상태 확인용 API
    @GetMapping("/attendance/status")
    public ResponseEntity<?> getTodayAttendanceStatus(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMember().getId();
        AttendanceStatusDto status = manageService.getTodayStatus(memberId);
        return ResponseEntity.ok(status);
    }



    // 일일 통계
    @GetMapping("/statistics")
    public String statistics(){

        return "management/statistics";
    }

    // 매출 내역
    @GetMapping("/sales")
    public String sales(@AuthenticationPrincipal CustomUserDetails userDetails,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                        Model model){

        // 선택한 날 가져오기
        LocalDate selectedDate = (date != null) ? date : LocalDate.now();

        // 날짜 3일 범위 생성
        List<LocalDate> dateList = new ArrayList<>();
        for(int i = -3; i <=3; i++){
            dateList.add(selectedDate.plusDays(i));
        }

        List<PaymentListDto> paymentListDtoList = manageService
                .getPaymentList(userDetails.getMember().getId(), selectedDate);

        System.out.println("selectedDate: " + selectedDate);


        model.addAttribute("dateList", dateList);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("paymentList", paymentListDtoList);

        return "management/sales";
    }

    // 방문 결제 등록 페이지
    @GetMapping("/sales/new")
    public String newSales(Model model){

        model.addAttribute("newPay", new PaymentForm());

        return "management/paymentForm";
    }
    
    // 방문 결제 등록
    @PostMapping("/sales/new")
    public String saveSale(@Valid PaymentForm form,
                           @AuthenticationPrincipal CustomUserDetails userDetails){

        manageService.savePayment(form, userDetails.getMember().getId());

        return "redirect:/manage/sales";
    }

    // 결제 상세페이지
    @GetMapping("/sales/detail")
    public String salesDetail(){

        return "management/paymentDetail";
    }

    // 예약 내역
    @GetMapping("/reservations")
    public String reservation(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                              Model model){

        // 선택한 날 가져오기
        LocalDate selectedDate = (date != null) ? date : LocalDate.now();

        // 날짜 3일 범위 생성
        List<LocalDate> dateList = new ArrayList<>();
        for(int i = -3; i <=3; i++){
            dateList.add(selectedDate.plusDays(i));
        }

        List<ReservationListDto> reservationList = manageService.getReservationList(userDetails.getMember().getId(), selectedDate);

        model.addAttribute("dateList", dateList);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("reservationList", reservationList);

        return "management/reservations";
    }

    // 예약 등록 페이지
    @GetMapping("/reservations/new")
    public String newRes(Model model){

        model.addAttribute("newRes", new ReservationForm());

        return "management/reservationForm";
    }

    // 멤버 검색 시 api
    @ResponseBody
    @GetMapping("/members/search")
    public List<MemberSearchDto> searchMembers(@RequestParam String keyword) {
        return manageService.searchByNameOrPhone(keyword);
    }

    // 쿠폰 조회용 api
    @ResponseBody
    @GetMapping("/members/{memberId}/coupons")
    public MemberCouponDto getMemberCoupons(@PathVariable Long memberId, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member designer  = userDetails.getMember();

        return manageService.getCoupons(memberId, designer.getId());
    }


    // 예약 저장
    @PostMapping("/reservations/save")
    public String saveRes(@Valid ReservationForm newRes, @AuthenticationPrincipal CustomUserDetails userDetails){

        manageService.saveReservation(newRes, userDetails.getMember().getId());

        return "redirect:/manage/reservations";
    }

    // 회원관리카드
    @GetMapping("/member-card")
    public String memberCard(){

        return "management/memberCard";
    }





}

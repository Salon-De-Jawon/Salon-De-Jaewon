package com.salon.control;


import com.salon.dto.designer.DesignerListDto;
import com.salon.dto.management.MemberCouponDto;
import com.salon.dto.shop.*;
import com.salon.entity.Member;
import com.salon.entity.management.ShopDesigner;
import com.salon.repository.MemberRepo;
import com.salon.repository.management.ShopDesignerRepo;
import com.salon.repository.management.master.ShopServiceRepo;
import com.salon.service.shop.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {


    private final ReservationService reservationServcie;
    private final ShopDesignerRepo shopDesignerRepo;
    private final MemberRepo memberRepo;



    // 예약 작성 페이지 Get매핑
    @GetMapping("/write")
    public String writeReservation(@RequestParam Long shopId, @RequestParam(required = false) Long shopDesignerId, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date,Principal principal ,Model model) {


        // 로그인 사용자 정보 바인딩
        if (principal != null) {
            String loginId = principal.getName();
            Member member = memberRepo.findByLoginId(loginId);
            model.addAttribute("member",member);
        }

        model.addAttribute("shopId", shopId);
        model.addAttribute("selectedDesignerId", shopDesignerId);

        if (shopDesignerId == null){
            // 디자이너 선택이 안 된 상태일때 - 디자이너 리스트만 보여주기
            List<DesignerListDto> designerList = reservationServcie.getDesignerList(shopId);
             model.addAttribute("designerList", designerList);

        }else {
            // 디자이너가 선택된 경우
            ShopDesigner shopDesigner = shopDesignerRepo.findByIdAndIsActiveTrue(shopDesignerId);
            if (shopDesignerId == null){
                throw new IllegalArgumentException("존재하지 않거나 비활성화된 디자이너 입니다 ID : " + shopDesignerId);
            }
            model.addAttribute("designer",shopDesigner);

            List<DesignerServiceCategoryDto> serviceCategories  = reservationServcie.getDesignerServiceCategoeies(shopDesignerId);
            model.addAttribute("serviceCategories", serviceCategories);

            AvailableTimeSlotDto timeSlotDto = reservationServcie.getAvailableTimeSlots(shopDesignerId,date);
            model.addAttribute("availableTimeSlot", timeSlotDto);
        }

        return "shop/reservationSelect";
    }

    // 예약 작성 Post -> 예약 확인 페이지로
    @PostMapping("/confirm")
    public String confirmReservaiton(@ModelAttribute ReservationRequestDto requestDto, Principal principal, Model model){

        // 예약자 정보 조회
        String loginId = principal.getName();
        Member member = memberRepo.findByLoginId(loginId);


        // 예약확인용 dto 구성
        ReservationCheckDto checkDto = reservationServcie.bulidReservationCheck(requestDto);

        model.addAttribute("memeber",member); // 예약자 정보
        model.addAttribute("checkDto", checkDto); // 예약 확인 화면에 보여줄 dto
        model.addAttribute("requestDto", requestDto); // 이후 저장시 그대로 넘길 원본 dto

        return "shop/reCheckCoupon";
    }

    // 예약 확인 페이지 Get
    @GetMapping("/confirm")
    public String confirmReservationPage(@ModelAttribute ReservationRequestDto requestDto,Principal principal ,Model model){

        // 예약자 정보 조회
        String loginId = principal.getName();
        Member member = memberRepo.findByLoginId(loginId);
        model.addAttribute("member", member);


        // 예약 정보 요약 (디자이너, 시술, 날짜 등)
        ReservationPreviewDto preview = reservationServcie.getReservationPreview(requestDto.getMemberId(),requestDto.getShopDesignerId(),
                requestDto.getShopServiceId(),requestDto.getDateTime().toLocalDate(), requestDto.getDateTime().toLocalTime());

        // 확인용 금액 정보 dto 생성
        ReservationCheckDto checkDto = reservationServcie.bulidReservationCheck(requestDto);


        // 사용가능한 쿠폰 / 티켓
        MemberCouponDto couponAndTicket = reservationServcie.getAvailableCouponAndTicket(requestDto.getMemberId(),
                preview.getSelectedDesigner().getShopId());

        model.addAttribute("preview",preview);
        model.addAttribute("checkDto", checkDto);
        model.addAttribute("requestDto", requestDto);
        model.addAttribute("couponDto", couponAndTicket);

        return "shop/reCheckCoupon";

    }

    // 예약 확정 및 저장 (post)
    @PostMapping("/complete")
    public String completeReservation(@ModelAttribute ReservationRequestDto requestDto, RedirectAttributes redirect,Principal principal,Model model){

        // 예약자 정보 조회
        String loginId = principal.getName();
        Member member = memberRepo.findByLoginId(loginId);
        model.addAttribute("memeber",member); // 예약자 정보


        reservationServcie.saveReservation(requestDto);


        redirect.addFlashAttribute("message", "예약이 성공적으로 완료 되었습니다");
        return "redirect:/shop/complete";
    }

}

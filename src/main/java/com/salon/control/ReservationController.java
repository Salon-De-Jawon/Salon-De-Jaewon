package com.salon.control;


import com.salon.dto.designer.DesignerListDto;
import com.salon.dto.management.MemberCouponDto;
import com.salon.dto.shop.DesignerServiceCategoryDto;
import com.salon.dto.shop.ReservationCheckDto;
import com.salon.dto.shop.ReservationPreviewDto;
import com.salon.dto.shop.ReservationRequestDto;
import com.salon.service.shop.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {


    private final ReservationService reservationServcie;


    @GetMapping("/write")
    public String getrservationSelect(){

        return "shop/reservationSelect";
    }

    @GetMapping("/check")
    public String getreservationCheck(){

        return "shop/reCheckCoupon";
    }


    // 예약 작성 페이지 Get매핑
    @GetMapping("/write")
    public String writeReservation(@RequestParam Long shopId, @RequestParam(required = false) Long shopDeisngerId,Model model) {

        // 디자이너 리스트 (선택이 안 된 경우에만 노출)
        if (shopDeisngerId == null){
            List<DesignerListDto> designerList = reservationServcie.getDesignerList(shopId);
            model.addAttribute("designerList", designerList);
        } else {
            // 선택된 디자이너의 전문 시술 분야 가져오기
            List<DesignerServiceCategoryDto> serviceCategories = reservationServcie.getDesignerServiceCategoeies(shopDeisngerId);
            model.addAttribute("serviceCategories", serviceCategories);
        }

        model.addAttribute("shopId",shopId);
        model.addAttribute("selectedDesignerId", shopDeisngerId);

        return "shop/reservationSelect";
    }

    // 예약 작성 Post -> 예약 확인 페이지로
    @PostMapping("/comfirm")
    public String confirmReservaiton(@ModelAttribute ReservationRequestDto requestDto, Model model){

        // 예약확인용 dto 구성
        ReservationCheckDto checkDto = reservationServcie.bulidReservationCheck(requestDto);

        model.addAttribute("checkDto", checkDto); // 예약 확인 화면에 보여줄 dto
        model.addAttribute("requestDto", requestDto); // 이후 저장시 그대로 넘길 원본 dto

        return "shop/reCheckCoupon";
    }

    // 예약 확인 페이지 Get
    @GetMapping("/confirm")
    public String confirmReservationPage(@ModelAttribute ReservationRequestDto requestDto, Model model){

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
    @PostMapping("complete")
    public String completeReservation(@ModelAttribute ReservationRequestDto requestDto, RedirectAttributes redirect){


        reservationServcie.saveReservation(requestDto);

        redirect.addFlashAttribute("message", "예약이 성공적으로 완료 되었습니다");
        return "redirect:/shop/complete";
    }

}

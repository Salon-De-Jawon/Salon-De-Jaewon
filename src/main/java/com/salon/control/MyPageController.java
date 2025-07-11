package com.salon.control;

import com.salon.config.CustomUserDetails;
import com.salon.dto.shop.CouponListDto;
import com.salon.dto.user.LikeDesignerDto;
import com.salon.dto.user.MyReservationDto;
import com.salon.dto.user.MyTicketListDto;
import com.salon.dto.user.ReviewCreateDto;
import com.salon.service.user.MyReservationService;
import com.salon.service.user.MypageService;
import com.salon.service.user.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/myPage")
public class MyPageController {

    private final MyReservationService myReservationService;
    private final ReviewService reviewService;
    private final MypageService mypageService;


    @GetMapping("/reservation")
    public String myReservationPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long memberId = userDetails.getMember().getId();

        List<MyReservationDto> reservationDtoList = myReservationService.getMyReservations(memberId);

        model.addAttribute("myReservations", reservationDtoList);

        return "/user/myReservation";
    }


    @PostMapping("/review/create")
    public String reviewSave(@ModelAttribute ReviewCreateDto dto) {
        reviewService.saveReview(dto);
        return "redirect:/myPage/reservation";
    }

    // 내쿠폰/정액권


    @GetMapping("/coupons")
    public String myCouponsPage(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long memberId = userDetails.getId();

        List<CouponListDto> couponListDtos = mypageService.getMyCoupons(memberId);
        List<MyTicketListDto> myTicketListDtos = mypageService.getMyTicket(memberId);

        model.addAttribute("myCoupons", couponListDtos);
        model.addAttribute("myTickets", myTicketListDtos);
        System.out.println("정액권 개수: " + myTicketListDtos.size());

        return "/user/myCoupons";
    }



    //찜목록

    @GetMapping("/likeList")
    public String likeList(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long memberId=userDetails.getId();

        List<LikeDesignerDto> likeDesignerDtos = mypageService.getDesignerLike(memberId);

        model.addAttribute("myLikeDesigner", likeDesignerDtos);


        return "/user/myLike";
    }


    //리뷰

    @GetMapping("/review")
    public String myReviewPage() {
        return "/user/myReview";
    }



}

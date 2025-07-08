package com.salon.control;

import com.salon.config.CustomUserDetails;
import com.salon.dto.user.MyReservationDto;
import com.salon.service.user.MyReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/myPage")
public class MyPageController {

    private final MyReservationService myReservationService;


    @GetMapping("/reservation")
    public String myReservationPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long memberId = userDetails.getMember().getId();

        List<MyReservationDto> reservationDtoList = myReservationService.getMyReservations(memberId);

        model.addAttribute("myReservations", reservationDtoList);

        return "/user/myReservation";
    }
}

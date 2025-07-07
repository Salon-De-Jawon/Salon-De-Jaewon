package com.salon.control;

import com.salon.config.CustomUserDetails;
import com.salon.dto.management.master.CouponDto;
import com.salon.dto.management.master.MainDesignerPageDto;
import com.salon.dto.management.master.ShopEditDto;
import com.salon.entity.management.ShopDesigner;
import com.salon.entity.management.master.Coupon;
import com.salon.service.management.master.MasterService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/master")
public class MasterController {

    private final MasterService masterService;

    // 메인페이지
    @GetMapping("")
    public String getMain(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){

        Long memberId = userDetails.getMember().getId();

        model.addAttribute("dto", masterService.getMainPage(memberId));

        return "master/main";
    }

    // 매장 근태 관리
    @GetMapping("/attendance")
    public String attendance(){

        return "master/attendance";
    }

    // 디자이너 관리
    @GetMapping("/designer-list")
    public String designerList(){

        return "master/designerList";
    }


    // 예약 관리
    @GetMapping("/reservations")
    public String reservations(){

        return "master/reservations";
    }

    // 매장 관리
    @GetMapping("/shop-edit")
    public String shopEdit(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){

        model.addAttribute("shop", masterService.getShopEdit(userDetails.getMember().getId()));

        return "master/shopEdit";
    }

    // 매장 수정 저장
    @PostMapping("/shop-edit/update")
    public String saveShop(@Valid ShopEditDto dto, @AuthenticationPrincipal CustomUserDetails userDetails){

        masterService.saveShopEdit(dto, userDetails.getMember().getId());

        return "redirect:/master";
    }



    // 쿠폰 관리
    @GetMapping("/coupons")
    public String coupon(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){

        model.addAttribute("couponList", masterService.getCouponList(userDetails.getMember().getId()));
        model.addAttribute("couponDto", new CouponDto());

        return "master/coupons";
    }

    // 쿠폰 등록
    @PostMapping("coupons/new")
    public String newCoupon(@Valid CouponDto dto, @AuthenticationPrincipal CustomUserDetails userDetails){

        masterService.saveCoupon(dto, userDetails.getMember().getId());

        return "redirect:/master/coupons";
    }




}

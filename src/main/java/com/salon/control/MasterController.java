package com.salon.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // 디자이너 수정 페이지
    @GetMapping("/designer/edit")
    public String designerEdit(){

        return "master/designerEdit";
    }


    // 예약 관리
    @GetMapping("/reservations")
    public String reservations(){

        return "master/reservations";
    }

    // 매장 관리
    @GetMapping("/shop-edit")
    public String shopEdit(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){

        ShopEditDto shopEdit = masterService.getShopEdit(userDetails.getMember().getId());

        System.out.println( shopEdit);
        model.addAttribute("shop", shopEdit);

        return "master/shopEdit";
    }

    // 매장 수정 저장
    @PostMapping("/shop-edit/update")
    public String saveShop(
            @RequestParam("shopEditDto") String shopEditDtoJson,
            @RequestParam("files") List<MultipartFile> files
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ShopEditDto dto = objectMapper.readValue(shopEditDtoJson, ShopEditDto.class);

        System.out.println(dto);
        System.out.println("받아온 파일: " + (files.isEmpty() ? "없음" : files.get(0).getOriginalFilename()));

        // 저장 로직 호출

        masterService.saveShopEdit(dto, files);

        return "redirect:/master";
    }
//    @PostMapping("/shop-edit/update")
//    public String saveShop(ShopEditDto shopEditDto,
//                           @RequestParam("files")  List<MultipartFile> files){
//
//        System.out.println(shopEditDto);
//        System.out.println( "받아온 파일" + files.get(0).getOriginalFilename() );
//        //masterService.saveShopEdit(dto, userDetails.getMember().getId());
//
//        return "redirect:/master";
//    }



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

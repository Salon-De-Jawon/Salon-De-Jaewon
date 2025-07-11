package com.salon.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.salon.config.CustomUserDetails;
import com.salon.dto.designer.DesignerListDto;
import com.salon.dto.management.master.*;
import com.salon.entity.management.ShopDesigner;
import com.salon.entity.management.master.Coupon;
import com.salon.entity.shop.ShopImage;
import com.salon.service.management.master.MasterService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/master")
public class MasterController {

    private final MasterService masterService;

    // 메인페이지
    @GetMapping("")
    public String getMain(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){


        model.addAttribute("dto", masterService.getMainPage(userDetails.getMember().getId()));

        return "master/main";
    }

    // 매장 근태 관리
    @GetMapping("/attendance")
    public String attendance(){

        return "master/attendance";
    }

    // 디자이너 관리
    @GetMapping("/designer-list")
    public String designerList(@AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model){

        List<DesignerListDto> designerList = masterService.getDesignerList(userDetails.getMember().getId());

        model.addAttribute("designerList", designerList);
        model.addAttribute("designerSearch", new DesignerSearchDto());

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

        model.addAttribute("shop", shopEdit);

        return "master/shopEdit";
    }

    // 매장 수정 저장
    @PostMapping("/shop-edit/update")
    public String saveShop(
            @RequestParam("shopEditDto") String shopEditDtoJson,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "deletedImageIds", required = false) String deletedImageIdsJson,
            @RequestParam(value = "thumbnailImageId", required = false) String thumbnail
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // DTO 파싱
        ShopEditDto dto = objectMapper.readValue(shopEditDtoJson, ShopEditDto.class);

        // 삭제 이미지 ID 파싱
        List<Long> deletedImageIds = new ArrayList<>();
        if (deletedImageIdsJson != null && !deletedImageIdsJson.isEmpty()) {
            deletedImageIds = objectMapper.readValue(deletedImageIdsJson, new TypeReference<List<Long>>() {});
        }

        // 썸네일 ID 처리
        Long thumbnailImageId = null;
        String thumbnailTempId = null;

        if (thumbnail != null && !thumbnail.isEmpty()) {
            if (thumbnail.startsWith("new_")) {
                thumbnailTempId = thumbnail; // new_sample.jpg_1345123
            } else if (thumbnail.matches("\\d+")) {
                thumbnailImageId = Long.parseLong(thumbnail); // 기존 이미지 ID
            }
        }
        dto.setThumbnailImageTempId(thumbnailTempId);


        masterService.saveShopEdit(dto, files, deletedImageIds, thumbnailImageId);

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

package com.salon.control;

import com.salon.constant.ServiceCategory;
import com.salon.dto.designer.DesignerListDto;
import com.salon.dto.management.ServiceForm;
import com.salon.dto.shop.ReviewListDto;
import com.salon.dto.shop.ShopDetailDto;
import com.salon.service.shop.ShopDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {

    private final ShopDetailService shopDetailService;


    @GetMapping("")
    public String getShop(){

        return "shop/shopDetail";
    }

    // 미용실 상세 페이지
    @GetMapping("/{shopId}")
    public String getShopDetail(@PathVariable("shopId") Long shopId, @RequestParam(required = false)ServiceCategory category,@RequestParam(required = false, defaultValue = "lastest") String sort,Model model){

        // 미용실 기본 정보
        ShopDetailDto shopDetail = shopDetailService.getShopDetail(shopId);

        // 추천시술
        List<ServiceForm> recommended = shopDetailService.getRecommededService(shopId);
        // 디자이너 목록
        List<DesignerListDto> designerLists = shopDetailService.getDesignersByShopId(shopId);
        // 리뷰 리스트
        List<ReviewListDto> reviewLists = shopDetailService.getFilteredReviews(shopId, category, sort);

        // 모델 바인딩
        model.addAttribute("shop", shopDetail);
        model.addAttribute("recommendedService", recommended);
        model.addAttribute("designerLists", designerLists);
        model.addAttribute("selectCategory", category);
        model.addAttribute("selectedSort", sort);

        return "shop/shopDetail";

    }
}

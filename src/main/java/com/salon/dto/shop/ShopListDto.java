package com.salon.dto.shop;

import com.salon.dto.management.master.ShopImageDto;
import com.salon.entity.shop.Shop;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ShopListDto {
    private Long id;
    private ShopImageDto shopImageDto;
    private String shopName;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String address;
    private Float rating;  // 샵 전체 평균
    private int reviewCount;
    private boolean hasCoupon;
    private BigDecimal distance;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<ShopDesignerProfileDto> designerList = new ArrayList<>();

    public static ShopListDto from(Shop shop, ShopImageDto shopImageDto, float avgRating, int reviewCount, boolean hasCoupon) {
        ShopListDto dto = new ShopListDto();

        dto.setId(shop.getId());
        dto.setShopImageDto(shopImageDto);
        dto.setShopName(shop.getName());
        dto.setOpenTime(shop.getOpenTime());
        dto.setCloseTime(shop.getCloseTime());
        dto.setAddress(shop.getAddress());
        dto.setRating(Math.round(avgRating * 10) / 10f);
        dto.setReviewCount(reviewCount);
        dto.setHasCoupon(hasCoupon);

        return dto;
    }



    // 모든 리뷰를 가져와서 자바 코드에서 반복문으로 계산, 전체 리뷰 데이터가 필요함. 리뷰 수가 많으면 느려지고 데이터 많이씀.
//    public static ShopListDto from (Shop shop, ShopImageDto shopImageDto, List<ReviewListDto> reviewListDtos, boolean hasCoupon) {
//        ShopListDto dto = new ShopListDto();
//
//        dto.setId(shop.getId());
//        dto.setShopImageDto(shopImageDto);
//        dto.setShopName(shop.getName());
//        dto.setOpenTime(shop.getOpenTime());
//        dto.setCloseTime(shop.getCloseTime());
//        dto.setAddress(shop.getAddress());
//
//        float sum = 0f;
//        int count = 0;
//
//        for(ReviewListDto reviewDto : reviewListDtos) {
//            if(reviewDto.getRating()>0) {
//                sum += reviewDto.getRating();
//                count++;
//            }
//        }
//
//        float avg= count > 0 ? sum/count : 0f;
//        avg = Math.round(avg * 10) /10f;
//
//        dto.setRating(avg);
//        dto.setReviewCount(count);
//        dto.setHasCoupon(hasCoupon);
//
//
//        return dto;
//    }



}

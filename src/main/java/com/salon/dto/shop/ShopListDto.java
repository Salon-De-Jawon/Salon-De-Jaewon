package com.salon.dto.shop;

import com.salon.dto.management.master.ShopImageDto;
import com.salon.entity.shop.Shop;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
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

    public static ShopListDto from (Shop shop, ShopImageDto shopImageDto, List<ReviewListDto> reviewListDtos, boolean hasCoupon) {
        ShopListDto dto = new ShopListDto();

        dto.setId(shop.getId());
        dto.setShopImageDto(shopImageDto);
        dto.setShopName(shop.getName());
        dto.setOpenTime(shop.getOpenTime());
        dto.setCloseTime(shop.getCloseTime());
        dto.setAddress(shop.getAddress());

        float sum = 0f;
        int count = 0;

        for(ReviewListDto reviewDto : reviewListDtos) {
            if(reviewDto.getRating()>0) {
                sum += reviewDto.getRating();
                count++;
            }
        }

        float avg= count > 0 ? sum/count : 0f;
        avg = Math.round(avg * 10) /10f;

        dto.setRating(avg);
        dto.setReviewCount(count);
        dto.setHasCoupon(hasCoupon);


        return dto;
    }



}

package com.salon.dto.user;

import com.salon.dto.designer.DesignerDetailDto;
import com.salon.dto.management.master.ShopImageDto;
import com.salon.dto.shop.ReviewListDto;
import com.salon.entity.shop.Shop;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShopListDto {
    private Long id;
    private ShopImageDto shopImageDto;
    private String shopName;
    private String shopAddress;
    private Float rating;  // 샵 전체 평균
    private int reviewCount;

    public static ShopListDto from (Shop shop, ShopImageDto shopImageDto, List<ReviewListDto> reviewListDtos) {
        ShopListDto dto = new ShopListDto();

        dto.setId(shop.getId());
        dto.setShopImageDto(shopImageDto);
        dto.setShopName(shop.getName());
        dto.setShopAddress(shop.getAddress());

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


        return dto;
    }

}

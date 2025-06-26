package com.salon.dto.user;

import com.salon.dto.designer.DesignerDetailDto;
import com.salon.dto.management.master.ShopImageDto;
import com.salon.entity.shop.Shop;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShopListDto {
    private Long id;
    private ShopImageDto shopImageDto;
    private List<DesignerDetailDto> disgnerDetailList;
    private String shopName;
    private String shopAddress;
    private Float averageRating;  // 샵 전체 평균
    private Float reviewCount;

    public static ShopListDto from (Shop shop, ShopImageDto shopImageDto, List<DesignerDetailDto> designerDetailDtoList) {
        ShopListDto dto = new ShopListDto();

        dto.setId(shop.getId());
        dto.setShopImageDto(shopImageDto);
        dto.setShopName(shop.getName());
        dto.setShopAddress(shop.getAddress());

        float sum = 0f;
        int count = 0;

        for(DesignerDetailDto designerDetailDto : designerDetailDtoList) {
            if (designerDetailDto.getRating() > 0) {
                sum += designerDetailDto.getRating();
                count++;
            }
        }

        float avg = count > 0 ? sum / count : 0f;
        avg = Math.round(avg * 10) /10f;

        return dto;
    }

}

package com.salon.dto.user;

import com.salon.dto.management.ServiceForm;
import com.salon.dto.shop.ShopListDto;
import com.salon.entity.shop.Shop;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class ShopCompareResultDto {
    private Long Id; // 선택된 미용실 아이디
    private ShopListDto shopListDto;
    private List<ServiceForm> serviceForms;

    public static ShopCompareResultDto from(Shop shop, ShopListDto shopListDto, List<ServiceForm> serviceForms) {
        ShopCompareResultDto dto = new ShopCompareResultDto();

        dto.setId(shop.getId());
        dto.setShopListDto(shopListDto);
        dto.setServiceForms(serviceForms);

        return dto;
    }
}

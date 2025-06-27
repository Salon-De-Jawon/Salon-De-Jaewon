package com.salon.dto.user;

import com.salon.entity.shop.Shop;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class ShopMarkerDto {
    // 지도에 미용실 위치 표시 마커용 dto
    private Long shopId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String imgUrl;
    private String shopName;

    public static ShopMarkerDto from(Shop shop) {
        ShopMarkerDto dto = new ShopMarkerDto();
        dto.setShopId(shop.getId());
        dto.setLatitude(shop.getLatitude());

        return dto;
    }
}

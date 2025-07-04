package com.salon.dto.shop;

import com.salon.entity.shop.Shop;
import com.salon.entity.shop.ShopImage;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ShopDetailDto {

    private Long id; // 미용실 테이블 아이디
    private String name; // 미용실 이름
    private String address; // 미용실 주소
    private String addressDetail; // 미용실 상세주소
    private BigDecimal latitude; // 미용실 위도
    private BigDecimal longitude; // 미용실 경도
    private String tel; // 미용실 전화번호
    private LocalTime openTime; // 미용실 오픈시간
    private LocalTime closeTime; // 미용실 마감시간



    // Shop(Entity) -> ShopDetailDto
    public static ShopDetailDto from (Shop shop, List<ShopImage> shopImages, int likeCount){
        ShopDetailDto shopDetailDto = new ShopDetailDto();

        shopDetailDto.setId(shop.getId());
        shopDetailDto.setName(shop.getName());
        shopDetailDto.setAddress(shop.getAddress());
        shopDetailDto.setAddressDetail(shop.getAddressDetail());
        shopDetailDto.setLatitude(shop.getLatitude());
        shopDetailDto.setLatitude(shop.getLongitude());
        shopDetailDto.setOpenTime(shop.getOpenTime());
        shopDetailDto.setCloseTime(shop.getCloseTime());
        shopDetailDto.setTel(shop.getTel());

        return shopDetailDto;

    }

}

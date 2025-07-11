package com.salon.dto.shop;

import com.salon.dto.management.master.ShopImageDto;
import com.salon.entity.shop.Shop;
import com.salon.util.DayOffUtil;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ShopListDto {
    private Long id;
    private ShopImageDto shopImageDto;
    private String shopName;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String address;
    private String addressDetail;
    private Float rating;  // 샵 전체 평균
    private int reviewCount;
    private boolean hasCoupon;
    private BigDecimal distance;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<ShopDesignerProfileDto> designerList = new ArrayList<>();
    private int dayOff; // 미용실 정기 휴무 날짜
    private String dayOffText; // 미용실 정기 휴무 날짜

    public static ShopListDto from(Shop shop, ShopImageDto shopImageDto, float avgRating, int reviewCount, boolean hasCoupon) {
        ShopListDto dto = new ShopListDto();

        dto.setId(shop.getId());
        dto.setShopImageDto(shopImageDto);
        dto.setShopName(shop.getName());
        dto.setOpenTime(shop.getOpenTime());
        dto.setCloseTime(shop.getCloseTime());
        dto.setAddress(shop.getAddress());
        dto.setAddressDetail(shop.getAddressDetail());
        dto.setRating(Math.round(avgRating * 10) / 10f);
        dto.setReviewCount(reviewCount);
        dto.setHasCoupon(hasCoupon);

        return dto;
    }


    public void  setDayOff(int dayOff) {
        this.dayOff = dayOff;
        this.dayOffText = generateDayOffText(dayOff);
    }


    private String generateDayOffText(int dayOffBit ){
        List<DayOfWeek> days = DayOffUtil.decodeDayOff(dayOffBit);
        if (days.isEmpty()) return "휴무일 없음";

        return days.stream()
                .map(DayOffUtil::getKoreanDay)
                .collect(Collectors.joining(", ", "매주 ", ""));
    }


}

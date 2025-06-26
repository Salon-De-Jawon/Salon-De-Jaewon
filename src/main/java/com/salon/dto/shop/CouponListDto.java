package com.salon.dto.shop;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CouponListDto {
    
    private String shopName; // 미용실 이름
    private String couponName; // 쿠폰 이름
    private int minimumAmount; // 최소 예약 금액
    private DiscountType couponType; // 할인 유형
    private boolean isActive; // 활성화 유무
    private LocalDate expireDate; // 쿠폰 소멸일
}

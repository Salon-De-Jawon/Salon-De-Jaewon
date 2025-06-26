package com.salon.dto.management.master;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CouponDto {

    private Long id; // Coupon ID

    private String title;
    private int minimumAmount;


}

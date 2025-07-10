package com.salon.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CouponBannerListDto {
    private Long id;
    private String shopName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String region;
}

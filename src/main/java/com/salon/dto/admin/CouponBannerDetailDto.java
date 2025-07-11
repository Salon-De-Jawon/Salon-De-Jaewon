package com.salon.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CouponBannerDetailDto {
    private Long id;
    private CouponBannerListDto couponBannerListDto;
    private String adminName;
    private String imgUrl;
    private LocalDateTime registerDate;
}

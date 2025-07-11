package com.salon.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeShopDto {
    private Long id; // 라이크 테이블 샵 아이디
    private Long shopId; // 샵 아이디
    private String imgUrl; // 샵 이미지
    private String shopName; // 샵 이름
    private String address; // 주소
    private Float rating; // 평점
    private boolean hasCoupon; // 쿠폰 유무
    private int dayOff; // 미용실 정기 휴무 날짜
    private String dayOffText; // 미용실 정기 휴무 날짜



}

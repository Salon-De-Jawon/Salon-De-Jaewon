package com.salon.dto.shop;

import com.salon.constant.CouponType;
import com.salon.entity.Member;
import com.salon.entity.management.master.Coupon;
import com.salon.entity.management.master.Service;
import com.salon.entity.shop.Reservation;
import com.salon.entity.shop.Shop;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationDetailDto {
    
    private Long id; // 예약테이블 아이디
    private String memberName; // 예약자 이름
    private String designerName; // 지정 디자이너 이름
    private String shopName; // 미용실 이름
    private String serviceName; // 시술 이름
    private String couponName; // 쿠폰 이름
    private CouponType couponType; // 쿠폰 할인 유형
    private int discountAmount; // 할인된 가격
    private LocalDateTime reservationDate; // 예약 날짜


    public static ReservationDetailDto from (Reservation reservation, Member member, Shop shop, Service service, Coupon coupon, LocalDateTime localDateTime){
        ReservationDetailDto reservationDetailDto = new ReservationDetailDto();

        reservationDetailDto.setId(reservation.getId());
        reservationDetailDto.setMemberName(member.getName());
        reservationDetailDto.setShopName(shop.getName());
        reservationDetailDto.setServiceName(service.getName());
        reservationDetailDto.setCouponName(coupon.getName());
        reservationDetailDto.setCouponType(coupon.getDiscountType());
        reservationDetailDto.setReservationDate(reservation.getReservationDate());


        return reservationDetailDto;
    }
}

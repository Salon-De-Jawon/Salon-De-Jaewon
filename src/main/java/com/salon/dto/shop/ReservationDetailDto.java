package com.salon.dto.shop;

import com.salon.entity.shop.Reservation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationDetailDto {
    
    private Long id; // 예약테이블 아이디
    private String membeeName; // 예약자 이름
    private String designerName; // 지정 디자이너 이름
    private String shopName; // 미용실 이름
    private String serviceName; // 시술 이름
    private String couponName; // 쿠폰 이름
    private DiscountType couponType; // 쿠폰 할인 유형
    private int discountAmount; // 할인된 가격
    private LocalDateTime reservationDate; // 예약 날짜


    public static ReservationDetailDto from (Reservation){
        ReservationDetailDto reservationDetailDto = new ReservationDetailDto();

        return reservationDetailDto;
    }
}

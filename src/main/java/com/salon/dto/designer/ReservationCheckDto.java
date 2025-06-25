package com.salon.dto.designer;

import com.salon.dto.shop.CouponListDto;
import com.salon.dto.shop.ReservationWriteDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReservationCheckDto {

    private String comment; // 고객 요청사항
    private List<CouponListDto> couponList; // 쿠폰 목록
    private int serviceAmount; // 시술 금액
    private int AmountDiscount; // 할인된 금액
    private int finalAmount; // 최종 금액
    private ReservationWriteDto writeDto; // 예약 작성 Dto
}

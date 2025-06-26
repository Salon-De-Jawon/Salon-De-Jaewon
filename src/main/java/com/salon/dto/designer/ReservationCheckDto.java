package com.salon.dto.designer;

import com.salon.dto.shop.CouponListDto;
import com.salon.dto.shop.ReservationWriteDto;
import com.salon.entity.Member;
import com.salon.entity.management.ShopDesigner;
import com.salon.entity.management.master.Service;
import com.salon.entity.shop.Reservation;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
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


    public static ReservationCheckDto from(Reservation reservation, List<CouponListDto> couponList, ReservationWriteDto reservationWriteDto) {
        ReservationCheckDto reservationCheckDto = new ReservationCheckDto();

        reservationCheckDto.setComment(reservation.getComment());
        reservationCheckDto.setAmountDiscount(reservation.getDiscountAmount());
        reservationCheckDto.setWriteDto(reservationWriteDto);

        return reservationCheckDto;
    }



    public Reservation to (Member member, ShopDesigner shopDesigner, Service service){
        Reservation reservation = new Reservation();



        return reservation;
    }

}

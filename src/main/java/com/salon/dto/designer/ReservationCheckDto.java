package com.salon.dto.designer;

import com.salon.constant.CouponType;
import com.salon.dto.shop.CouponListDto;
import com.salon.dto.shop.ReservationWriteDto;
import com.salon.entity.shop.Reservation;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class ReservationCheckDto {



    private String comment; // 고객 요청사항
    private List<CouponListDto> couponList; // 쿠폰 목록
    private int serviceAmount; // 시술 금액
    private CouponType discountType; // 쿠폰 할인 방식
    private int amountDiscount; // 할인된 금액
    private int ticketUsedAmount; // 정액권 사용 금액
    private int finalAmount; // 최종 금액
    private ReservationWriteDto writeDto; // 예약 작성 Dto




    // WriteDto -> CheckDto
    public static ReservationCheckDto from (ReservationWriteDto reservationWriteDto, List<CouponListDto> couponListDtos,CouponType type){
        ReservationCheckDto reservationCheckDto = new ReservationCheckDto();

        reservationCheckDto.setCouponList(couponListDtos);
        reservationCheckDto.setServiceAmount(reservationWriteDto.getServiceAmount());
        reservationCheckDto.setServiceAmount(reservationWriteDto.getServiceAmount());
        reservationCheckDto.setComment(reservationCheckDto.getComment()); // 요청사항 전달


        // 쿠폰이 있을 경우 첫번째 쿠폰 기준으로 할인 적용
        if(couponListDtos != null && !couponListDtos.isEmpty()){
            CouponListDto selectedCoupon = couponListDtos.get(0); // 기본 선택된 쿠폰이 1개일 때
            CouponType couponType = selectedCoupon.getCouponType();
            int discountValue = selectedCoupon.getDiscountAmount(); // 퍼센트 or 할인금액

            reservationCheckDto.setDiscountType(type);

            if (type == CouponType.PERCENT){
                int percentDiscount = (reservationWriteDto.getServiceAmount() * discountValue) / 100;
                reservationCheckDto.setAmountDiscount(percentDiscount);
            } else if (type == CouponType.AMOUNT){
                reservationCheckDto.setAmountDiscount(discountValue);
            }
        } else {
            reservationCheckDto.setDiscountType(null); // 쿠폰이 null 일때
            reservationCheckDto.setAmountDiscount(0);
        }

        // 정액권 사용 금액 (없을땐 기본값 0)
        reservationCheckDto.setTicketUsedAmount(0); // 사용시 입력 가능

        // 최종 결제 금액 계산
        int finalAmount = reservationCheckDto.getServiceAmount() - reservationCheckDto.getAmountDiscount() - reservationCheckDto.getTicketUsedAmount();

        if(finalAmount < 0 ) finalAmount = 0;

        reservationCheckDto.setFinalAmount(finalAmount);


        return reservationCheckDto;
    }



}

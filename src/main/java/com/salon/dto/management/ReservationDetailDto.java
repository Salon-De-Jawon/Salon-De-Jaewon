package com.salon.dto.management;


import com.salon.constant.ReservationStatus;
import com.salon.entity.shop.Reservation;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReservationDetailDto {

    private Long id; // Reservation ID
    private String memberName;
    private String serviceName;
    private int servicePrice;
    private String couponName;
    private int couponDiscount; // 쿠폰 할인금액
    private boolean ticketIsUsed; // 정액권 사용유무
    private int ticketUsedAmount; // 정액권 사용금액
    private int finalPrice;
    private ReservationStatus status;

    public static ReservationDetailDto from(Reservation reservation) {

        ReservationDetailDto dto = new ReservationDetailDto();
        
        int discountAmount;
        int ticketUsedAmount;

        dto.setId(reservation.getId());
        dto.setMemberName(reservation.getMember().getName());
        dto.setServiceName(reservation.getService().getName());
        dto.setServicePrice(reservation.getService().getPrice());

        if(reservation.getCoupon() != null){ // 쿠폰 있을시
            dto.setCouponName(reservation.getCoupon().getName());
            discountAmount = reservation.getDiscountAmount();
            dto.setCouponDiscount(discountAmount);
        } else { // 쿠폰 없을시
            dto.setCouponName(null);
            discountAmount = 0;
            dto.setCouponDiscount(0);
        }

        if(reservation.getTicket() != null){ // 정액권 있을시
            dto.setTicketIsUsed(true);
            ticketUsedAmount = reservation.getTicketUsedAmount();
            dto.setTicketUsedAmount(ticketUsedAmount);
        } else { // 정액권 없을시
            dto.setTicketIsUsed(false);
            ticketUsedAmount = 0;
            dto.setTicketUsedAmount(0);
        }

        // 서비스가격 - 할인가격 - 정액권금액
        dto.setFinalPrice(reservation.getService().getPrice() - discountAmount - ticketUsedAmount);

        dto.setStatus(reservation.getStatus());

        return dto;
    }


}

package com.salon.dto.management;

import com.salon.constant.ReservationStatus;
import com.salon.entity.shop.Reservation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ReservationListDto {

    private Long reservationId;


    private String memberName; // 디자이너 전용
    private String designerName; // 회원 전용

    private String serviceName;
    private String comment;
    // 시술 날짜 및 시간
    private LocalDateTime date;
    private ReservationStatus status;
    
    private boolean isToday; // 당일 여부

    public static ReservationListDto from (Reservation reservation){
        ReservationListDto dto = new ReservationListDto();
        dto.setReservationId(reservation.getId());
        dto.setMemberName(reservation.getMember().getName());
        dto.setServiceName(reservation.getShopService().getName());
        dto.setDate(reservation.getReservationDate());
        dto.setStatus(reservation.getStatus());
        dto.setComment(reservation.getComment());

        return dto;
    }
}

package com.salon.dto.management;

import com.salon.constant.ReservationStatus;
import com.salon.entity.shop.Reservation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class DesignerReservationListDto {

    private Long reservationId;
    private String memberName;
    private String serviceName;
    // 시술 날짜 및 시간
    private LocalDateTime date;
    private ReservationStatus status;

    public static DesignerReservationListDto from (Reservation reservation){
        DesignerReservationListDto dto = new DesignerReservationListDto();
        dto.setReservationId(reservation.getId());
        dto.setMemberName(reservation.getMember().getName());
        dto.setServiceName(reservation.getService().getName());
        dto.setDate(reservation.getReservationDate());
        dto.setStatus(reservation.getStatus());

        return dto;
    }
}

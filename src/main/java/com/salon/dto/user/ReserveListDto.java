package com.salon.dto.user;

import com.salon.constant.ReservationStatus;
import com.salon.entity.shop.Reservation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReserveListDto {
    private Long id;
    private LocalDateTime reservationDate;
    private ReservationStatus status;
    private String designerName;
    private String serviceName;

    public static ReserveListDto from(Reservation entity) {
        ReserveListDto dto = new ReserveListDto();
        dto.setId(entity.getId());
        dto.setReservationDate(entity.getReservationDate());
        dto.setStatus(entity.getStatus());


        return dto;
    }
}

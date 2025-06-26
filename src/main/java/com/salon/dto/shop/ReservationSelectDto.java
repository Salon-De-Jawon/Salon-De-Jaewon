package com.salon.dto.shop;


import com.salon.constant.ServiceCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReservationSelectDto {

    private LocalDateTime SelectDateTime;
    private String designerImg;
    private String designerName;
    private int designerWorkingYear;
    private ServiceCategory serviceCategory;


    private List<ReservationSelectDto> reservationSelectDtos;


    // SelectDto -> WriteDto
    public static ReservationWriteDto from (List<ReservationSelectDto> reservationSelectDtos){
        ReservationWriteDto reservationWriteDto = new ReservationWriteDto();

        reservationWriteDto.setDateSelect(reservationSelectDtos);

        return reservationWriteDto;
    }


}

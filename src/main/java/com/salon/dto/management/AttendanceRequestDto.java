package com.salon.dto.management;

import com.salon.constant.AttendanceStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class AttendanceRequestDto {

    private AttendanceStatus status;
    private LocalDateTime time;

}

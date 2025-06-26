package com.salon.dto.management;

import com.salon.constant.AttendanceStatus;
import com.salon.entity.management.master.Attendance;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class AttendanceListDto {

    Long id; // Attendance Id
    String designerName;
    LocalDateTime clockIn;
    LocalDateTime clockOut;
    AttendanceStatus status;
    String note;

    public static AttendanceListDto from (Attendance attendance) {

        AttendanceListDto dto = new AttendanceListDto();
        dto.setId(attendance.getId());
        dto.setDesignerName(attendance.getShopDesigner().getMember().getName());
        dto.setClockIn(attendance.getClockIn());
        dto.setClockOut(attendance.getClockOut());
        dto.setNote(attendance.getNote());

        return dto;
    }

}

package com.salon.dto.management;

import com.salon.constant.AttendanceStatus;
import com.salon.entity.management.master.Attendance;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class AttendanceListDto {

    private Long id; // Attendance Id
    private String designerName;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private AttendanceStatus status;
    private String note;

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

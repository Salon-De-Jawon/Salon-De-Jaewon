package com.salon.dto.management;

import com.salon.entity.management.master.Attendance;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AttendanceStatusDto {
    private String clockIn;
    private String clockOut;
    private boolean isWorking;

    public static AttendanceStatusDto from(Attendance att) {
        AttendanceStatusDto dto = new AttendanceStatusDto();
        dto.setClockIn(att.getClockIn() != null ? att.getClockIn().toString() : null);
        dto.setClockOut(att.getClockOut() != null ? att.getClockOut().toString() : null);
        dto.setWorking(att.getClockIn() != null && att.getClockOut() == null);
        return dto;
    }
}

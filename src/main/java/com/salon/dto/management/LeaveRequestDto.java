package com.salon.dto.management;

import com.salon.constant.LeaveStatus;
import com.salon.constant.LeaveType;
import com.salon.entity.management.LeaveRequest;
import com.salon.entity.management.ShopDesigner;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
public class LeaveRequestDto {

    Long id; // LeaveRequest Id
    Long designerId; // ShopDesignerId
    String designerName;
    LocalDate startDate;
    LocalDate endDate;
    LeaveType leaveType;
    String reason;
    LeaveStatus status;
    LocalDateTime requestAt;
    LocalDateTime approvedAt;

    public static LeaveRequestDto from(LeaveRequest leaveRequest) {

        LeaveRequestDto dto = new LeaveRequestDto();

        dto.setId(leaveRequest.getId());
        dto.setDesignerId(leaveRequest.getDesigner().getId());
        dto.setDesignerName(leaveRequest.getDesigner().getMember().getName());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setLeaveType(leaveRequest.getLeaveType());
        dto.setReason(leaveRequest.getReason());
        dto.setStatus(leaveRequest.getStatus());
        dto.setRequestAt(leaveRequest.getRequestAt());
        dto.setApprovedAt(leaveRequest.getApprovedAt());

        return dto;

    }

    public LeaveRequest to (ShopDesigner designer) {

        LeaveRequest request = new LeaveRequest();

        request.setDesigner(designer);
        request.setStartDate(this.startDate);
        request.setEndDate(this.endDate);
        request.setLeaveType(this.leaveType);
        request.setStatus(this.status);
        request.setRequestAt(LocalDateTime.now());

        return request;
    }




}

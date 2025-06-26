package com.salon.entity.management;

import com.salon.constant.LeaveStatus;
import com.salon.constant.LeaveType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_request_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "shop_designer_id")
    ShopDesigner designer;

    LocalDate startDate;
    LocalDate endDate;
    LeaveType leaveType;

    @Lob // varchar -> text
    String reason;

    LeaveStatus status;
    LocalDateTime requestAt;
    LocalDateTime approvedAt;



}

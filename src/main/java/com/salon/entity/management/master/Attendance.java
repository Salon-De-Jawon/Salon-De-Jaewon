package com.salon.entity.management.master;

import com.salon.constant.AttendanceStatus;
import com.salon.entity.management.ShopDesigner;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "designer_id")
    ShopDesigner shopDesigner;

    LocalDateTime clockIn;
    LocalDateTime clockOut;
    AttendanceStatus status;

    @Lob
    String note;



}
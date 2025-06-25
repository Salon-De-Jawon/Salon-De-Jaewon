package com.salon.entity.management.master;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@Entity
public class DesignerService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "designer_service_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    Member member;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;

    String originalImgName;
    String imgName;
    String imgUrl;
    // 경력 시작일
    LocalDate startAt;
    // 디자이너 연차
    int workingYears;
    String position;
    LocalTime scheduledStartTime;
    LocalTime scheduledEndTime;
    boolean isActive;

}

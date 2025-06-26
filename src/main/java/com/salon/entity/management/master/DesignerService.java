package com.salon.entity.management.master;

import com.salon.entity.Member;
import com.salon.entity.shop.Shop;
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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private String originalImgName;
    private String imgName;
    private String imgUrl;
    // 경력 시작일
    private LocalDate startAt;
    // 디자이너 연차
    private int workingYears;
    private String position;
    private LocalTime scheduledStartTime;
    private LocalTime scheduledEndTime;
    private boolean isActive;

}

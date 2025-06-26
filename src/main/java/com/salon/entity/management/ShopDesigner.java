package com.salon.entity.management;

import com.salon.entity.Member;
import com.salon.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@Entity
public class ShopDesigner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_designer_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;

    @ManyToOne
    @JoinColumn(name = "member_id")
    Member member;

    String originalImgName;
    String imgName;
    String imgUrl;
    LocalDate startAt;
    int workingYears;
    String position;
    LocalTime scheduledStartTime;
    LocalTime scheduledEntTime;
    boolean isActive;

}

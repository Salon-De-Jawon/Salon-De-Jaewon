package com.salon.entity.management.master;

import com.salon.constant.CouponType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@Entity
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;

    String name;
    int minimumAmount;
    CouponType discountType;
    int discountValue;
    boolean isActive;
    LocalDate expireDate;

}

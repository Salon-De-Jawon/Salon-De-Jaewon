package com.salon.entity.admin;

import com.salon.constant.ApplyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name="coupon_banner")
public class CouponBanner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="coupon_id", nullable=false)
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="admin_id", nullable = false)
    private Member admin;

    private LocalDate startDate;
    private LocalDate endDate;
    private String originalName;
    private String imgName;
    private String imgUrl;
    private LocalDateTime createAt;
    private LocalDateTime registerDate;
    private ApplyStatus status;

}

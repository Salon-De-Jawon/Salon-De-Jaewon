package com.salon.entity.shop;

import com.salon.constant.RStauts;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id; // 예약테이블 아이디

    @JoinColumn(name = "member_id")
    @ManyToOne
    private Member memberId; // 유저 아이디

    @JoinColumn(name = "designer_id")
    @ManyToOne
    private Designer dsignerId; // 디자이너 아이디

    @JoinColumn(name = "service_id")
    @ManyToOne
    private Service serviceId; // 서비스 아이디

    @JoinColumn(name = "coupon_id")
    @ManyToOne
    private Coupon couponId; // 쿠폰 아이디

    private int discountAmount; // 할인된 가격
    private LocalDateTime reservationDate; // 예약 날짜
    
    @Enumerated(EnumType.STRING)
    private RStauts stauts; // 예약 상태
    private String comment; // 요청사항
}

package com.salon.entity.management;

import com.salon.constant.PaymentType;
import com.salon.entity.shop.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = true) // 예약시
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "shop_designer_id", nullable = true) // 방문시
    private ShopDesigner shopDesigner;

    private LocalDateTime payDate;
    private int totalPrice;
    private int couponDiscountPrice;
    private int ticketUsedPrice;
    private int finalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Lob
    private String memo;

    private LocalDateTime createdAt;

}

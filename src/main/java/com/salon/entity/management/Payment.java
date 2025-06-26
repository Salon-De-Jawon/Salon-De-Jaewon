package com.salon.entity.management;

import com.salon.constant.PaymentType;
import com.salon.entity.shop.Reservation;
import jakarta.annotation.Nullable;
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
    @JoinColumn(name = "reservation_id")
    @Nullable
    private Reservation reservation;

    private LocalDateTime date;
    private int total_price;
    private int coupon_discount_price;
    private int ticket_used_price;
    private int final_price;
    private PaymentType type;

    @Lob
    private String memo;

    private LocalDateTime createAt;

}

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
    Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    @Nullable
    Reservation reservation;

    LocalDateTime date;
    int total_price;
    int coupon_discount_price;
    int ticket_used_price;
    int final_price;
    PaymentType type;

    @Lob
    String memo;

    LocalDateTime createAt;

}

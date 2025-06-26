package com.salon.entity.management.master;

import com.salon.entity.Member;
import com.salon.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    Member member;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;

    int totalAmount;
    int usedAmount;
    LocalDateTime createAt;

}

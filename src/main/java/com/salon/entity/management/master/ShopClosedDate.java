package com.salon.entity.management.master;

import com.salon.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@Entity
public class ShopClosedDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_closed_date_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;

    LocalDate offStartDate;
    LocalDate offEndDate;
    String reason;

}

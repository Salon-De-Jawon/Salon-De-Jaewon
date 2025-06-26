package com.salon.entity.management.master;

import com.salon.constant.ServiceCategory;
import com.salon.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;

    String name;
    int price;

    @Lob
    String description;

    ServiceCategory category;
    String originalImgName;
    String imgName;
    String imgUrl;

}

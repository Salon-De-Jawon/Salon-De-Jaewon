package com.salon.entity.management.master;

import com.salon.entity.Member;
import com.salon.entity.management.ShopDesigner;
import com.salon.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@Entity
public class DesignerService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "designer_service_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_designer_id")
    private ShopDesigner designer;

    private boolean cut;
    private boolean color;
    private boolean perm;
    private boolean upstyle;
    private boolean dry;
    private boolean hair_extension;
    private boolean clinic;

}

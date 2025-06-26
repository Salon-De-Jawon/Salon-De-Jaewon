package com.salon.entity.management;

import com.salon.entity.shop.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class MemberCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_card_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    Reservation reservation;

    @Lob
    String memo;

}

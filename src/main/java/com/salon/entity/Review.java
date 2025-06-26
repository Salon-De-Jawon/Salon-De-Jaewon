package com.salon.entity;

import com.salon.entity.shop.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    Reservation reservation;

    int rating;

    @Lob // varchar -> text
    String comment;

    // 작성일
    LocalDateTime createAt;
    String replyComment;
    LocalDateTime replyAt;


}

package com.salon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class ReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "review_id")
    Review review;

    String originalName;
    String imgName;
    String imgUrl;

}

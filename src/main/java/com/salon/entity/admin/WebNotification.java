package com.salon.entity.admin;

import com.salon.constant.WebTarget;
import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="web_notification")
public class WebNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name="web_target")
    private WebTarget webTarget;
    private Long targetId;

    private boolean isRead;
    private LocalDateTime createAt;
}

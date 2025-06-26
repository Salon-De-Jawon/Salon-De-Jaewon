package com.salon.entity;

import com.salon.constant.Gender;
import com.salon.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    Long id;

    String loginId;
    String password;
    String name;
    LocalDate birthDate;
    Gender gender;
    String email;
    String tel;
    Role role;
    // 계정 생성일
    LocalDateTime createAt;

    // 위치정보제공동의 여부
    boolean agreeLocation;
    // 웹알림전송동의 여부
    boolean agreeAlarm;

}

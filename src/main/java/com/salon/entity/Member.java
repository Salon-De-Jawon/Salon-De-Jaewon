package com.salon.entity;

import com.salon.constant.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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

}

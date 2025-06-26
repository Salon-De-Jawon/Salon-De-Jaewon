package com.salon.dto.user;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Getter
@Setter
public class SignUpDto {
    private String loginId;
    private String password;
    private String name;

    @Email
    private String email;
    private String tel;
    private Gender gender;
    private LocalDate birthDate;
    private boolean agreeAlert;
    private boolean agreeLocation;

    public Member to(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .loginId(this.loginId)
                .password(passwordEncoder.encode(this.password))
                .name(this.name)
                .email(this.email)
                .tel(this.tel)
                .gender(this.gender)
                .birthDate(this.birthDate)
                .agreeAlert(this.agreeAlert)
                .agreeLocation(this.agreeLocation)
                .build();

    }
}

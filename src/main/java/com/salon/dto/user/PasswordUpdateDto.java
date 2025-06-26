package com.salon.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateDto {
    //전송용
    private String newPassword;
    private String confirmPassword;
}

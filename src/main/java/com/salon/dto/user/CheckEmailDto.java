package com.salon.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckEmailDto {
    @NotBlank
    @Email
    private String email;
}

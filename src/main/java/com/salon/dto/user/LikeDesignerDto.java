package com.salon.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LikeDesignerDto {

    private Long id;
    private String designerName;
    private LocalDate workingYear;
    private String shopName;
}

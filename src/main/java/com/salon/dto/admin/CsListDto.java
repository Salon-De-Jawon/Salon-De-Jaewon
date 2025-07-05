package com.salon.dto.admin;

import com.salon.constant.CsCategory;
import com.salon.constant.CsStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CsListDto {
    private Long id;
    private String questionText;
    private CsCategory csCategory;
    private LocalDateTime questionAt;
    private CsStatus csStatus;
}

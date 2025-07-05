package com.salon.dto.admin;

import com.salon.constant.CsCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CsCreateDto {
    private CsCategory csCategory;
    private String questionText;
    private String originalName;
    private String fileName;
    private String fileUrl;
}

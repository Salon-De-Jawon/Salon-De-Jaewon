package com.salon.dto.designer;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DesignerListDto {

    private Long id; // 디자이너 id
    private String name; // 디자이너 이름
    private LocalDate workingYear; // 디자이너 연차
    private String position; // 디자이너 직급
    private String description; // 디자이너 소개
    private String designerImg; // 디자이너 프로필 이미지
}

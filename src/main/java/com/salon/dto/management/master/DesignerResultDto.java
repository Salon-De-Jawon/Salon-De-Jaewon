package com.salon.dto.management.master;

import com.salon.entity.management.Designer;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DesignerResultDto {

    private Long id;
    private String designerName;
    private String imgUrl;
    private int workingYears;

    public static DesignerResultDto from(Designer designer){

        DesignerResultDto dto = new DesignerResultDto();
        dto.setId(designer.getId());
        dto.setDesignerName(designer.getMember().getName());
        dto.setImgUrl(designer.getImgUrl());
        dto.setWorkingYears(designer.getWorkingYears());

        return dto;

    }

}

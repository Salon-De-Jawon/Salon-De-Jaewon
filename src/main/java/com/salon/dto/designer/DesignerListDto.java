package com.salon.dto.designer;

import com.salon.entity.management.ShopDesigner;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DesignerListDto {

    private Long id; // 디자이너 id
    private String name; // 디자이너 이름
    private int workingYear; // 디자이너 연차
    private String position; // 디자이너 직급
    private String description; // 디자이너 소개
    private String designerImg; // 디자이너 프로필 이미지


    // ShopDesigner(Entity) -> DesignerListDto
    public static DesignerListDto from (ShopDesigner shopDesigner){
        DesignerListDto designerListDto = new DesignerListDto();

        designerListDto.setId(shopDesigner.getId());
        designerListDto.setName(shopDesigner.getMember().getName());
        designerListDto.setWorkingYear(shopDesigner.getWorkingYears());
        designerListDto.setPosition(shopDesigner.getPosition());

        return designerListDto;
    }
}

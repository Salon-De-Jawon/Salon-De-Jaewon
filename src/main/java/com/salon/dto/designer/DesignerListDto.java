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
    private String imgUrl; // 디자이너 프로필 이미지
    private int reviewCount; // 디자이너 리뷰 갯수
    private int likeCount; // 디자이너 찜 갯수
    private int rating; // 디자이너 평점



    // ShopDesigner(Entity) -> DesignerListDto
    public static DesignerListDto from (ShopDesigner shopDesigner,int likeCount, int reviewCount){
        DesignerListDto designerListDto = new DesignerListDto();

        designerListDto.setId(shopDesigner.getId());
        designerListDto.setName(shopDesigner.getDesigner().getMember().getName());
        designerListDto.setWorkingYear(shopDesigner.getDesigner().getWorkingYears());
        designerListDto.setPosition(shopDesigner.getPosition());
        designerListDto.setImgUrl(shopDesigner.getDesigner().getImgUrl());
        designerListDto.setLikeCount(likeCount);
        designerListDto.setReviewCount(reviewCount);
        return designerListDto;
    }
}

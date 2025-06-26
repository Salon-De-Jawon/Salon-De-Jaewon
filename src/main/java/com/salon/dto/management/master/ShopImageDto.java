package com.salon.dto.management.master;

import com.salon.entity.shop.ShopImage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class ShopImageDto {

    Long id;
    String originalName;
    String imgName;
    String imgUrl;

    // 이미지 파일
    MultipartFile imgFile;

    public static ShopImageDto from (ShopImage image) {
        ShopImageDto dto = new ShopImageDto();
        dto.setId(image.getId());
        dto.setOriginalName(image.getOriginalName());
        dto.setImgName(image.getImgName());
        dto.setImgUrl(image.getImgUrl());

        return dto;
    }


}

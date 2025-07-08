package com.salon.dto.management.master;

import com.salon.entity.shop.ShopImage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class ShopImageDto {

    private Long id;
    private String originalName;
    private String imgName;
    private String imgUrl;
    private boolean isThumbnail;

    // 이미지 파일
    private MultipartFile imgFile;

    public static ShopImageDto from (ShopImage image) {
        ShopImageDto dto = new ShopImageDto();
        dto.setImgUrl(image.getImgUrl());
        dto.setThumbnail(image.isThumbnail());

        return dto;
    }


}

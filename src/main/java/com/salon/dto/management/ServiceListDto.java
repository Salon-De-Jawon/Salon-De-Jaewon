package com.salon.dto.management;

import com.salon.constant.ServiceCategory;
import com.salon.entity.management.master.Service;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServiceListDto {

    private Long id;
    private Long shopId;
    private String name;
    private int price;
    private String description;
    private ServiceCategory category;
    private String originalImgName;
    private String imgName;
    private String imgUrl;

    public static ServiceListDto from (Service service) {

        ServiceListDto dto = new ServiceListDto();

        dto.setId(service.getId());
        dto.setShopId(service.getShop().getId());
        dto.setName(service.getName());
        dto.setPrice(service.getPrice());
        dto.setDescription(service.getDescription());
        dto.setCategory(service.getCategory());
        dto.setOriginalImgName(service.getOriginalImgName());
        dto.setImgName(service.getImgName());
        dto.setImgUrl(service.getImgUrl());

        return dto;
    }

}

package com.salon.dto.management;

import com.salon.constant.ServiceCategory;
import com.salon.entity.management.master.Service;
import com.salon.entity.shop.Shop;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class ServiceForm {

    private Long id;
    private Long shopId;
    private String name;
    private int price;
    private String description;
    private ServiceCategory category;
    private String originalImgName;
    private String imgName;
    private String imgUrl;

    private MultipartFile imgFile;

    public static ServiceForm from (Service service) {

        ServiceForm dto = new ServiceForm();

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

    public Service to (Shop shop) {

        Service service = new Service();

        service.setName(this.name);
        service.setShop(shop);
        service.setPrice(this.price);
        service.setDescription(this.description);

        return service;

    }

}

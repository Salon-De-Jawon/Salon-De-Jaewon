package com.salon.dto.shop;

import com.salon.constant.ServiceCategory;
import com.salon.entity.management.master.ShopService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceListDto {

    private Long id; // 시술테이블 아이디
    private String name; // 시술 이름
    private int price; // 시술 가격
    private String Description; // 시술상세 설명
    private ServiceCategory serviceCategory; // 시술유형



    // Service(Entity) -> ServiceListDto
    public static ServiceListDto from (ShopService shopService){
        ServiceListDto serviceListDto = new ServiceListDto();

        serviceListDto.setId(shopService.getId());
        serviceListDto.setName(shopService.getName());
        serviceListDto.setPrice(shopService.getPrice());
        serviceListDto.setDescription(shopService.getDescription());
        serviceListDto.setServiceCategory(shopService.getCategory());

        return serviceListDto;
    }
}

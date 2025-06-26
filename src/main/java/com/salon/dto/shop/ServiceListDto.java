package com.salon.dto.shop;

import com.salon.constant.ServiceCategory;
import com.salon.entity.management.master.Service;
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
    public static ServiceListDto from (Service service){
        ServiceListDto serviceListDto = new ServiceListDto();

        serviceListDto.setId(service.getId());
        serviceListDto.setName(service.getName());
        serviceListDto.setPrice(service.getPrice());
        serviceListDto.setDescription(service.getDescription());
        serviceListDto.setServiceCategory(service.getCategory());

        return serviceListDto;
    }
}

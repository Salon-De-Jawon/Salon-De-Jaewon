package com.salon.dto.shop;


import com.salon.constant.ServiceCategory;
import com.salon.entity.management.ShopDesigner;
import com.salon.entity.management.master.ShopService;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationSelectDto {


    private Long serviceId; // 시술 테이블 아이디
    private Long shopDesignerId; // 디자이너 테이블 아이디
    private LocalDateTime SelectDateTime; // 선택가능한 날짜 및 시간
    private String designerImg; // 디자이너 프로필 이미지
    private String designerName; // 디자이너 이름
    private int designerWorkingYear; // 디자이너 연차
    private ServiceCategory serviceCategory; // 시술 카테고리





    public static ReservationSelectDto from (ShopDesigner shopDesigner, ShopService shopService){
        ReservationSelectDto reservationSelectDto = new ReservationSelectDto();
        
        reservationSelectDto.setShopDesignerId(shopDesigner.getId());
        reservationSelectDto.setDesignerImg(shopDesigner.getImgUrl());
        reservationSelectDto.setDesignerName(shopDesigner.getMember().getName());
        reservationSelectDto.setDesignerWorkingYear(shopDesigner.getWorkingYears());
        reservationSelectDto.setServiceCategory(shopService.getCategory());
        
        return reservationSelectDto;
    }


}

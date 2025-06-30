package com.salon.dto.designer;

import com.salon.entity.management.Designer;
import com.salon.entity.management.ShopDesigner;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class DesignerUpdateDto {
    
    private String shopName; // 미용실 이름
    private Long designerId; // 디자이너 아이디
    private String designerImg; // 디자이너 프로필
    private String Name; // 디자이너 이름
    private LocalDate startAt; // 디자이너 시작일
    private String position; // 디자이너 직급
    private LocalTime scheduledStartTime; // 출근시간
    private LocalTime scheduledEndTime; // 퇴근 시간
    private boolean isActive; // 재직 여부
    private MultipartFile imgFile; // 디자이너 프로필 이미지



    // ShopDesigner(Entity) -> DesignerUpdateDto
    public static DesignerUpdateDto from (ShopDesigner shopDesigner, Designer designer){
        DesignerUpdateDto designerUpdateDto = new DesignerUpdateDto();
        designerUpdateDto.setShopName(shopDesigner.getShop().getName());
        designerUpdateDto.setDesignerId(designer.getId());
        designerUpdateDto.setDesignerImg(designer.getImgUrl());
        designerUpdateDto.setName(designer.getMember().getName());
        designerUpdateDto.setStartAt(designer.getStartAt());
        designerUpdateDto.setPosition(shopDesigner.getPosition());
        designerUpdateDto.setScheduledStartTime(shopDesigner.getScheduledStartTime());
        designerUpdateDto.setScheduledEndTime(shopDesigner.getScheduledEntTime());
        designerUpdateDto.setActive(designerUpdateDto.isActive());

        return designerUpdateDto;
    }

    // DesignerUpdateDto -> ShopDesigner(Entity)
    public ShopDesigner to (DesignerUpdateDto designerUpdateDto){
        ShopDesigner shopDesigner = new ShopDesigner();



        return shopDesigner;

    }
}

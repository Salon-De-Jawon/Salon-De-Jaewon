package com.salon.dto.shop;


import com.salon.dto.designer.ReservationCheckDto;
import com.salon.entity.Member;
import com.salon.entity.management.ShopDesigner;
import com.salon.entity.management.master.Service;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class ReservationWriteDto {

    private Long shopDesignerId; // 디자이너 테이블 아이디
    private String designerName; // 디자이너 이름
    private Long serviceId; // 서비스 테이블 아이디
    private String memberName; // 사용자 이름
    private Long memberId; // 사용자 테이블 아이디
    private String ShopName; // 미용실 이름
    private String serviceName; //시술이름
    private int serviceAmount; // 시술 가격


    private List<ReservationSelectDto> dateSelect;




    // SelectDto -> WriteDto
    public static ReservationWriteDto from (List<ReservationSelectDto> reservationSelectDtos, ShopDesigner shopDesigner, Member member, Service service){
        ReservationWriteDto reservationWriteDto = new ReservationWriteDto();

        reservationWriteDto.setDesignerName(shopDesigner.getMember().getName());
        reservationWriteDto.setShopName(shopDesigner.getShop().getName());
        reservationWriteDto.setMemberName(member.getName());
        reservationWriteDto.setServiceAmount(service.getPrice());
        reservationWriteDto.setServiceName(service.getName());

        reservationWriteDto.setDateSelect(reservationSelectDtos);

        return reservationWriteDto;
    }


}

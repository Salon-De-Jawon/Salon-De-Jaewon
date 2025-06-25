package com.salon.dto.shop;

import com.salon.dto.designer.DesignerDetailDto;
import com.salon.entity.shop.Reservation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReservationWriteDto {

    private String memberName; // 사용자 이름
    private Long memberId; // 사용자 테이블 아이디
    private String ShopName; // 미용실 이름
    private Long shopId; // 미용실 테이블 아이디
    private String serviceName; //시술이름
    private int serviceAmount; // 시술 가격
    private List<DesignerDetailDto> desingerProfile; // 시술 가능 디자이너 목록
    private LocalDateTime reservationTime; // 예약 일시



   public Reservation to (ReservationWriteDto reservationWriteDto, List<DesignerDetailDto> designerDetailDtos){
       Reservation reservation = new Reservation();




       return reservation;
   }
}

package com.salon.dto.shop;


import com.salon.dto.designer.ReservationCheckDto;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class ReservationWriteDto {

    private Long shopDesignerId; // 디자이너 테이블 아이디
    private Long serviceId; // 서비스 테이블 아이디
    private String memberName; // 사용자 이름
    private Long memberId; // 사용자 테이블 아이디
    private String ShopName; // 미용실 이름
    private Long shopId; // 미용실 테이블 아이디
    private String serviceName; //시술이름
    private int serviceAmount; // 시술 가격


    private List<ReservationSelectDto> dateSelect;


    // WriteDto -> CheckDto
    public static ReservationCheckDto from (ReservationWriteDto reservationWriteDto, List<CouponListDto> couponListDtos){
       ReservationCheckDto reservationCheckDto = new ReservationCheckDto();

       reservationCheckDto.setWriteDto(reservationWriteDto);
       reservationCheckDto.setCouponList(couponListDtos);

       return reservationCheckDto;
    }

}

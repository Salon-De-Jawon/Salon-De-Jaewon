package com.salon.dto.management;

import com.salon.entity.management.MemberCard;
import com.salon.entity.management.Payment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MemberCardListDto {

    private Long id; // MemberCard ID
    private ReservationDetailDto detailDto;
    private String memo;
    private LocalDateTime createAt;

    public static MemberCardListDto from(MemberCard memberCard, ReservationDetailDto detailDto) {

        MemberCardListDto dto = new MemberCardListDto();

        dto.setDetailDto(detailDto);
        dto.setMemo(memberCard.getMemo());
        dto.setCreateAt(memberCard.getCreateAt());

        return dto;
    }

    public MemberCard to(MemberCardListDto dto, Payment payment){

        MemberCard entity = new MemberCard();

        entity.setPayment(payment);
        entity.setMemo(dto.getMemo());
        entity.setCreateAt(LocalDateTime.now());

        return entity;
    }


}

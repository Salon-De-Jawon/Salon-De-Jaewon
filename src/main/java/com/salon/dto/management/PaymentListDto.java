package com.salon.dto.management;

import com.salon.entity.management.Payment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class PaymentListDto {

    private Long id;
    private String designerName;
    private LocalDateTime payDate;
    private int finalPrice;
    private String memo;
    private String category;

    public static PaymentListDto from(Payment payment){

        PaymentListDto dto = new PaymentListDto();
        dto.setId(payment.getId());
        if(payment.getReservation() != null) { // 예약시
            dto.setDesignerName(payment.getReservation().getDesigner().getMember().getName());
            dto.setCategory("예약결제");
        } else if(payment.getDesigner() != null) { // 방문시
            dto.setDesignerName(payment.getDesigner().getMember().getName());
            dto.setCategory("방문결제");
        }

        dto.setPayDate(payment.getPayDate());
        dto.setFinalPrice(payment.getFinalPrice());
        dto.setMemo(payment.getMemo());

        return dto;
    }

}

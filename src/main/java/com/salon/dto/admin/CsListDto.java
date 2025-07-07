package com.salon.dto.admin;

import com.salon.constant.CsCategory;
import com.salon.constant.CsStatus;
import com.salon.entity.admin.CsCustomer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CsListDto {
    private Long id;
    private String questionText;
    private CsCategory csCategory;
    private LocalDateTime questionAt;
    private CsStatus csStatus;

    public static CsListDto from(CsCustomer csCustomer) {
        CsListDto csListDto = new CsListDto();
        csListDto.setId(csCustomer.getId());
        csListDto.setQuestionText(csCustomer.getQuestionText());
        csListDto.setCsCategory(csCustomer.getCategory());
        csListDto.setQuestionAt(csCustomer.getQuestionAt());
        csListDto.setCsStatus(csCustomer.getStatus());
        return csListDto;
    }
}

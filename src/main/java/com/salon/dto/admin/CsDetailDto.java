package com.salon.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CsDetailDto {
    private Long id;
    private String loginId;
    private String memberName;
    private CsListDto csListDto;
    private String adminName;
    private LocalDateTime replyAt;
    private String replyText;
}

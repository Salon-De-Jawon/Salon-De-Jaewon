package com.salon.dto.shop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceListDto {

    private Long id; // 시술테이블 아이디
    private String name; // 시술 이름
    private String price; // 시술 가격
    private String Description; // 시술상세 설명
    private ServiceCategory serviceCategory; // 시술유형


}

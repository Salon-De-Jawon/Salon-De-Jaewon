package com.salon.dto.admin;

import com.salon.entity.admin.Announcement;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AncDetailDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime writeAt;
    private String adminName;

    public static AncDetailDto from(Announcement entity){
        AncDetailDto dto = new AncDetailDto();
        dto.

        return dto;
    }
}

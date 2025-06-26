package com.salon.dto.admin;



import com.salon.entity.admin.Announcement;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class AncListDto {
    private Long id;
    private String adminName;
    private String title;
    private LocalDateTime writeAt;

    public static AncListDto form(Announcement entity) {
        AncListDto dto = new AncListDto();
        dto.setId(entity.getId());
        dto.setAdminName(entity.getAdmin().getName());

        return dto;
    }
}

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
    private String originalName;
    private String fileName;
    private String fileUrl;

    public static AncDetailDto from(Announcement announcement, AncListDto ancListDto){
        AncDetailDto ancDetailDto = new AncDetailDto();
        return ancDetailDto;
    }

}

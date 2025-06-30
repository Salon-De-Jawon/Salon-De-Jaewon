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
    private String originalName;
    private String fileName;
    private String fileUrl;

    public static AncListDto from(Announcement announcement){
        AncListDto ancListDto = new AncListDto();
        ancListDto.setId(announcement.getId());
        ancListDto.setTitle(announcement.getTitle());
        ancListDto.setWriteAt(announcement.getWriteAt());
        return ancListDto;
    }
}

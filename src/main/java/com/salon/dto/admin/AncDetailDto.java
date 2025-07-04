package com.salon.dto.admin;

import com.salon.constant.Role;
import com.salon.entity.admin.Announcement;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AncDetailDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime writeAt;
    private Role role;
    private String adminName;
    private String originalName;
    private String fileName;
    private String fileUrl;
    private boolean hasPrev;
    private boolean hasNext;
    private Long prevId;
    private Long nextId;
    private List<String> fileUrls;

    public static AncDetailDto from(Announcement announcement){
        AncDetailDto ancDetailDto = new AncDetailDto();
        ancDetailDto.setId(announcement.getId());
        ancDetailDto.setWriteAt(announcement.getWriteAt());
        ancDetailDto.setContent(announcement.getContent());
        ancDetailDto.adminName=announcement.getAdmin().getName();
        ancDetailDto.setTitle(announcement.getTitle());
        ancDetailDto.setRole(announcement.getRole());

        return ancDetailDto;
    }

}

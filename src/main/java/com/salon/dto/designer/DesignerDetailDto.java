package com.salon.dto.designer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class DesignerDetailDto {
    
    private String shopName; // 미용실 이름
    private Long designerId; // 디자이너 아이디
    private String originalName; // 이미지 원본 이름
    private String imgName; // 이미지 저장 이름
    private String imgUrl; // 이미지 경로
    private String designerName; // 디자이너 이름
    private LocalDate startAt; // 디자이너 시작일
    private String position; // 디자이너 직급
    private LocalTime scheduledStartTime; // 출근 시간
    private LocalTime scheduleEndTime; // 퇴근 시간
    private boolean isActive; // 재직 여부

    private MultipartFile designerProfile;


    
}

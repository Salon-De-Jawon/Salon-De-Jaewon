package com.salon.dto;

import com.salon.constant.WebTarget;
import com.salon.entity.admin.WebNotification;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class WebNotificationDto {

    private String message; // 웹 알림으로 보낼 메세지
    private String createAt; // 생성 날짜 + 시간
    private WebTarget webTarget; // 대상
    private Long targetId; // 대상 아이디
    private Long memberId;

    public static WebNotificationDto from (WebNotification entity) {
        WebNotificationDto dto = new WebNotificationDto();

        dto.setMessage(entity.getMessage());
        dto.setWebTarget(entity.getWebTarget());
        dto.setTargetId(entity.getTargetId());
        dto.setCreateAt(getTimeAgo(entity.getCreateAt()));
        dto.setMemberId(entity.getMemberId());

        return dto;
    }

    // 방금전, 몇시간전, 몇일전, 날짜(7일이상) 으로 날짜 바꾸기 코드
    public static String getTimeAgo(LocalDateTime createAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createAt, now);

        long seconds = duration.getSeconds();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (seconds < 60) return "방금 전";
        if (minutes < 60) return minutes + "분 전";
        if (hours < 24) return hours + "시간 전";
        if (days == 1) return "하루 전";
        if (days == 2) return "이틀 전";
        if (days < 7) return days + "일 전";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return createAt.format(formatter);
    }

    //웹알림 저장 코드

    public WebNotification to() {
        WebNotification entity = new WebNotification();
        entity.setMessage(this.message);
        entity.setWebTarget(this.webTarget);
        entity.setTargetId(this.targetId);
        entity.setRead(false);
        entity.setCreateAt(LocalDateTime.now());
        entity.setMemberId(this.memberId);

        return entity;
    }
}

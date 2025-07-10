package com.salon.service;

import com.salon.constant.WebTarget;
import com.salon.dto.WebNotificationDto;
import com.salon.entity.admin.WebNotification;
import com.salon.repository.WebNotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebNotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final WebNotificationRepo webNotificationRepo;

    // 웹 알림 발생하는 시점에서 receiverId 저장해서(받는 유저) 넘기기
    public void sendWebSocketNotification (Long receiverId, WebNotification entity) {

        // 웹알림 받는 사람
        String destination = "/topic/notify/" + receiverId;
        // 웹 알림 내용
        WebNotificationDto dto = WebNotificationDto.from(entity);

        System.out.println("웹소켓 알림 전송 시도 → 대상: " + destination);
        System.out.println("알림 내용: " + dto.getMessage());

        messagingTemplate.convertAndSend(destination, dto);

        System.out.println("웹소켓 알림 전송 완료");
    }

    // 웹 알림 읽음 표시
    public void markAsReadByTarget(WebTarget webTarget, Long targetId) {
        WebNotification webNotification = webNotificationRepo.findTopByWebTargetAndTargetIdOrderByCreateAtDesc(webTarget, targetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다."));

        if (!webNotification.isRead()) {
            webNotification.setRead(true);
            webNotificationRepo.save(webNotification);
        }
    }
}

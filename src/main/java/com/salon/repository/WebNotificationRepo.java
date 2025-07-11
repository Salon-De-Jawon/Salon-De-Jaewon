package com.salon.repository;


import com.salon.constant.WebTarget;
import com.salon.entity.admin.WebNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WebNotificationRepo extends JpaRepository<WebNotification, Long> {
    Optional<WebNotification> findTopByWebTargetAndTargetIdOrderByCreateAtDesc(WebTarget webTarget, Long targetId);

    List<WebNotification> findAllByMemberIdAndIsReadFalse(Long memberId);
}

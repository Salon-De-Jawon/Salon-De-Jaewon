package com.salon.repository.shop;

import com.salon.entity.shop.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepo extends JpaRepository<Reservation, Long> {

    // 특정 디자이너의 특정 기간내 예약 리스트 조회
    List<Reservation> findByDesignerIdAndDateBetween(Long designerId, LocalDateTime start, LocalDateTime end);

    // 사용자의 전체 예약 조회
    List<Reservation> findByMemberIdOrderByReservationDateDesc(Long memberId);

    // 방문횟수를 카운트하기 위한 메서드
    int countByMemberIdAndDesignerId(Long memberId, Long designerId);
}

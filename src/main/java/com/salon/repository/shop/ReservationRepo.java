package com.salon.repository.shop;

import com.salon.entity.shop.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepo extends JpaRepository<Reservation, Long> {

    // 특정 디자이너의 특정 기간내 예약 리스트 조회
    List<Reservation> findByShopDesignerIdAndReservationDateBetween(Long designerId, LocalDateTime start, LocalDateTime end);

    // 사용자의 전체 예약 조회
    List<Reservation> findByMemberIdOrderByReservationDateDesc(Long memberId);

    // 방문횟수를 카운트하기 위한 메서드
    Reservation countByMemberIdAndShopDesignerId(Long memberId, Long designerId);

    // 특정 디자이너 예약현황
    List<Reservation> findByShopDesignerIdOrderByReservationDateDesc(Long shopDesignerId);

    // 오늘 예약 수 (디자이너)
    @Query("SELECT COUNT(r) FROM Reservation r " +
            "WHERE DATE(r.reservationDate) = CURRENT_DATE " +
            "AND r.shopDesigner.id = :designerId")
    int countTodayReservations(@Param("designerId") Long designerId);

}

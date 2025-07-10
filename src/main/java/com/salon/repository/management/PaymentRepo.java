package com.salon.repository.management;

import com.salon.entity.management.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

    // 디자이너 매출 목록
//    @Query("""
//    SELECT p FROM Payment p WHERE (p.reservation.shopDesigner.id = :designerId) OR (p.shopDesigner.id = :designerId)
//    """)
//    List<Payment> findByDesignerOrderByPayDate(@Param("designerId") Long designerId);

    // 당일 결제 회원 수 (디자이너)
    @Query("SELECT COUNT(p) FROM Payment p LEFT JOIN p.reservation r " +
    "WHERE ((r.shopDesigner.id = :designerId OR p.shopDesigner.id = :designerId) " +
        "AND DATE(p.payDate) = CURRENT_DATE)")
    int countTodayCompletePayments(@Param("designerId") Long designerId);

    // 결제 내역 가져오기 (기간)
    @Query("SELECT p FROM Payment p LEFT JOIN p.reservation r " +
        "WHERE (p.shopDesigner.id = :designerId OR r.shopDesigner.id = :designerId) " +
        "AND p.payDate BETWEEN :start AND :end ORDER BY p.payDate ASC"
        )
    List<Payment> findByDesignerAndPeriod(@Param("designerId") Long designerId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);


    // 디자이너의 하루 매출
    @Query("SELECT COALESCE(SUM(p.totalPrice), 0) " +
            "FROM Payment p WHERE DATE(p.payDate) = CURRENT_DATE " +
            "AND p.shopDesigner.id = :designerId")
    int sumTodayTotalPrice(@Param("designerId") Long designerId);

    // 매장 월간 매출
    @Query("SELECT COALESCE(SUM(p.totalPrice), 0) " +
            "FROM Payment p JOIN p.shopDesigner sd JOIN sd.shop s " +
            "WHERE FUNCTION('MONTH', p.payDate) = FUNCTION('MONTH', CURRENT_DATE) " +
            "AND FUNCTION('YEAR', p.payDate) = FUNCTION('YEAR', CURRENT_DATE) " +
            "AND s.id = :shopId")
    int sumMonthlyTotalPrice(@Param("shopId") Long shopId);

    // 예약 아이디 통해 결제 정보 가져오기

    Optional<Payment> findByReservationId(Long reservationId);
}

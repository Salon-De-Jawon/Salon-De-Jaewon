package com.salon.repository.management;

import com.salon.entity.management.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

    // 디자이너 매출 목록
//    @Query("""
//    SELECT p FROM Payment p WHERE (p.reservation.shopDesigner.id = :designerId) OR (p.shopDesigner.id = :designerId)
//    """)
//    List<Payment> findByDesignerOrderByPayDate(@Param("designerId") Long designerId);

    // 당일 결제 회원 수 (디자이너)
    @Query("SELECT COUNT(p) FROM Payment p WHERE (p.reservation.shopDesigner.id = :designerId)" +
            "OR (p.shopDesigner.id = :designerId) AND DATE(p.payDate) = CURRENT_DATE"
    )
    int countTodayCompletePayments(@Param("designerId") Long designerId);

    @Query("SELECT p FROM Payment p LEFT JOIN p.reservation r " +
        "WHERE (p.shopDesigner.id = :designerId OR r.shopDesigner.id = :designerId) " +
        "AND p.payDate BETWEEN :start AND :end ORDER BY p.payDate ASC"
        )
    List<Payment> findByDesignerAndPeriod(@Param("designerId") Long designerId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);
}

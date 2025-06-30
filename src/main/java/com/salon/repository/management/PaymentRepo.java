package com.salon.repository.management;

import com.salon.entity.management.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    @Query("""
    SELECT p FROM Payment p WHERE (p.reservation.shopDesigner.id = :designerId) OR (p.shopDesigner.id = :designerId)
    """)
    List<Payment> findByDesignerOrderByPayDate(@Param("designerId") Long designerId);
}

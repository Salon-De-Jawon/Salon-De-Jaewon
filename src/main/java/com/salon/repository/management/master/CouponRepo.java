package com.salon.repository.management.master;

import com.salon.entity.management.master.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepo extends JpaRepository<Coupon, Long> {

    // 미용실 쿠폰 목록 (만료일 가까운 순)
    List<Coupon> findByShopIdAndIsActiveTrueOrderByExpireDate(Long shopId);

    @Query("SELECT COUNT(c) > 0 FROM Coupon c WHERE c.shop.id = :shopId AND c.isActive = true AND c.expireDate >= CURRENT_DATE")
    boolean existsActiveCouponByShopId(@Param("shopId") Long shopId);
}

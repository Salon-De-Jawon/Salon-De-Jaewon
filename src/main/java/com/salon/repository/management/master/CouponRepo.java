package com.salon.repository.management.master;

import com.salon.entity.management.master.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepo extends JpaRepository<Coupon, Long> {

    // 미용실 쿠폰 목록 (만료일 가까운 순)
    List<Coupon> findByShopIdAndIsActiveTrueOrderByExpireDate(Long shopId);


}

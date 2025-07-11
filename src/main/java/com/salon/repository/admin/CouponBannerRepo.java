package com.salon.repository.admin;

import com.salon.entity.admin.CouponBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponBannerRepo extends JpaRepository<CouponBanner, Long> {
}

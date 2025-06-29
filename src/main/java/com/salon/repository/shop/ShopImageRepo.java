package com.salon.repository.shop;

import com.salon.entity.shop.ShopImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopImageRepo extends JpaRepository<ShopImage,Long> {
    
    // 특정 매장의 모든 이미지 조회용
    List<ShopImage> findByShopId (Long shopId);
    
    // 썸네일로 지전된 대표 이미지 1개 조회용
    Optional<ShopImage> findByShopIdAndIsThumbnailTrue(Long shopId);
}

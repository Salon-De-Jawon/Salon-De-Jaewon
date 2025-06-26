package com.salon.repository.management;

import com.salon.entity.management.ShopDesigner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopDesignerRepo extends JpaRepository<ShopDesigner, Long> {

    // 미용실 소속 디자이너 목록 가져오기
    List<ShopDesigner> findByShopIdAndIsActiveTrue(Long shopId);

}

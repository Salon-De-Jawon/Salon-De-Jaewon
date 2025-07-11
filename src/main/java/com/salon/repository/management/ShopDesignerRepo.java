package com.salon.repository.management;

import com.salon.entity.management.Designer;
import com.salon.entity.management.ShopDesigner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopDesignerRepo extends JpaRepository<ShopDesigner, Long> {

    // 미용실 소속 디자이너 목록 가져오기
    List<ShopDesigner> findByShopIdAndIsActiveTrue(Long shopId);

    // MemberId 로 디자이너 정보 가져오기
    ShopDesigner findByDesigner_Member_IdAndIsActiveTrue(Long memberId);

    // shopDesignerId 로 찾기
    ShopDesigner findByIdAndIsActiveTrue(Long shopDesignerId);

    // 미용실 소속 디자이너 수
    int countByShopIdAndIsActiveTrue(Long id);

    Optional<ShopDesigner> findByDesigner(Designer designer);
    Optional<ShopDesigner> findByDesignerId(Long designerId);

    // 이미 미용실 소속된 디자이너인지 확인
    boolean existsByDesignerId(Long designerId);
}

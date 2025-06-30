package com.salon.repository.management;

import com.salon.entity.management.ShopDesigner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopDesignerRepo extends JpaRepository<ShopDesigner, Long> {

    // 미용실 소속 디자이너 목록 가져오기
    List<ShopDesigner> findByShopIdAndIsActiveTrue(Long shopId);

    // 해당회원의 디자이너 정보 가져오기
    ShopDesigner findByDesigner_MemberId(Long memberId);

    // 디자이너 검색결과 가져오기
    List<ShopDesigner> findByDesigner_Member_NameAndDesigner_Member_Tel(String name, String tel);

}

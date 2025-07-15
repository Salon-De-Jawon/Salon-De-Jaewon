package com.salon.repository.shop;

import com.salon.entity.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepo extends JpaRepository<Shop, Long>{
    
    // 매장 이름으로 단일 매장 조회
    Optional<Shop> findByName (String name);

    // 지역으로 매장 검색 (따로 지역이 없으니 주소에서 검색하기)
    Page<Shop> findByAddressContaining(String region, Pageable pageable);

    List<Shop> findByAddressContaining(String region);
}
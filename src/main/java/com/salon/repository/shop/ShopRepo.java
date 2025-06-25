package com.salon.repository.shop;

import com.salon.entity.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepo extends JpaRepository<Shop, Long>{
    
    // 매장 이름으로 단일 매장 조회
    Optional<Shop> findByName (String name);
}

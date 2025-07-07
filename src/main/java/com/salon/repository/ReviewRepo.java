package com.salon.repository;

import com.salon.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {

    // 디자이너 리뷰갯수
    int countByReservation_ShopDesigner_Id(Long designerId);

    // 샵에 달린 전체 리뷰 갯수(소속 디자이너 리뷰갯수 더한거 총 수 )
    @Query("""
            SELECT COUNT(r) FROM Review r JOIN r.reservation res 
            JOIN res.shopDesigner sd WHERE sd.shop.id = :shopId""")
    int countAllByShopId(@Param("shopId") Long shopId);

    // 샵 평균 평점
    @Query("""
            SELECT COALESCE(AVG(r.rating), 0) 
            FROM Review r 
            JOIN r.reservation res 
            JOIN res.shopDesigner sd 
            WHERE sd.shop.id = :shopId
            """)
    float averageRatingByShopId(@Param("shopId") Long shopId);

}

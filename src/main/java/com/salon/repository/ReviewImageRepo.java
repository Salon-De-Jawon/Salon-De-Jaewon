package com.salon.repository;

import com.salon.dto.shop.ReviewImageDto;
import com.salon.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepo extends JpaRepository<ReviewImage, Long> {

    // 특정 리뷰에 연결된 모든 이미지 가져오기
    List<ReviewImage> findAllByReview_Id(Long reviewId);

    boolean existsByReview_Id(Long id);

    ReviewImage findTopByReview_IdOrderByIdAsc(Long reviewId);
    // 해당 리뷰의 id를 찾아 이미지 조회하는 메서드
    List<ReviewImage> findByReviewId(Long reviewId);

    // 리뷰 이미지 최신순으로 8장만 가져오기
    List<ReviewImage> findTop8ByOrderByIdDesc();
}

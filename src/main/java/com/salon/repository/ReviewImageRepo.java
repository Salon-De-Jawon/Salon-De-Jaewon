package com.salon.repository;

import com.salon.entity.Review;
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
}

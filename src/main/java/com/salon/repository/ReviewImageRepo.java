package com.salon.repository;

import com.salon.dto.shop.ReviewImageDto;
import com.salon.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepo extends JpaRepository<ReviewImage, Long> {


    // 해당 리뷰의 id를 찾아 이미지 조회하는 메서드
    List<ReviewImage> findByReviewId(Long reviewId);

    // 리뷰 이미지 최신순으로 8장만 가져오기
    List<ReviewImage> findByTop8OrderByDesc();
}

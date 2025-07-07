package com.salon.service.user;

import com.salon.entity.Review;
import com.salon.repository.ReviewRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepo reviewRepo;

    // 샵 리뷰수
    public int getReviewCountByShop(Long shopId) {
        return reviewRepo.countAllByShopId(shopId);
    }
    // 샵 평균 평점
    public float getAverageRatingByShop(Long shopId) {
        float avg = reviewRepo.averageRatingByShopId(shopId);
        return Math.round(avg * 10) / 10f;
    }


}

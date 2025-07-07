package com.salon.service.user;

import com.salon.repository.ReviewRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepo reviewRepo;

    public int getReviewCountByShop(Long shopId) {
        return reviewRepo.countAllByShopId(shopId);
    }

    public float getAverageRatingByShop(Long shopId) {
        float avg = reviewRepo.averageRatingByShopId(shopId);
        return Math.round(avg * 10) / 10f;
    }

}

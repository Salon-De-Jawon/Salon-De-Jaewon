package com.salon.service.user;

import com.salon.dto.management.ServiceForm;
import com.salon.dto.management.master.ShopImageDto;
import com.salon.dto.shop.ShopListDto;
import com.salon.dto.user.ShopCompareResultDto;
import com.salon.entity.shop.Shop;
import com.salon.repository.management.master.ShopServiceRepo;
import com.salon.repository.shop.ShopRepo;
import com.salon.service.management.master.CouponService;
import com.salon.service.shop.ShopImageService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompareService {
    private final ShopRepo shopRepo;
    private final ReviewService reviewService;
    private final CouponService couponService;
    private final ShopImageService shopImageService;
    private final ShopServiceRepo shopServiceRepo;

    public List<ShopCompareResultDto> getCompareResults(List<Long> shopIds) {
        List<ShopCompareResultDto> result = new ArrayList<>();

        for(Long shopId: shopIds) {
            Shop shop = shopRepo.findById(shopId)
                    .orElseThrow(() -> new IllegalArgumentException("미용실이 존재하지 않습니다"));

            float avgRating = reviewService.getAverageRatingByShop(shopId);
            int reviewCount = reviewService.getReviewCountByShop(shopId);
            boolean hasCoupon = couponService.hasActiveCoupon(shopId);
            ShopImageDto imageDto = shopImageService.findThumbnailByShopId(shopId);

            ShopListDto shopListDto = ShopListDto.from(shop, imageDto, avgRating, reviewCount, hasCoupon);

            List<ServiceForm> serviceForms = shopServiceRepo.findByShopId(shopId).stream()
                    .map(ServiceForm::from)
                    .collect(Collectors.toList());

            ShopCompareResultDto dto = ShopCompareResultDto.from(shop, shopListDto, serviceForms);

            result.add(dto);
        }

        return result;
    }

}

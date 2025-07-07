package com.salon.service.shop;

import com.salon.dto.management.master.ShopImageDto;

import com.salon.dto.shop.ShopDesignerProfileDto;
import com.salon.dto.shop.ShopListDto;
import com.salon.dto.user.ShopMapDto;

import com.salon.entity.management.ShopDesigner;
import com.salon.entity.shop.Shop;

import com.salon.repository.management.ShopDesignerRepo;
import com.salon.repository.shop.ShopRepo;
import com.salon.service.management.master.CouponService;

import com.salon.service.user.ReviewService;
import com.salon.util.DistanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepo shopRepo;
    private final ShopImageService shopImageService;
    private final ReviewService reviewService;
    private final CouponService couponService;
    private final ShopDesignerRepo shopDesignerRepo;



    // 메인 맵에서 지도에 표시되는 거리에 존재하는 샵을 불러오는 메서드

    public List<ShopMapDto> getAllShopsForMap(BigDecimal userLat, BigDecimal userLon) {
        List<Shop> shops = shopRepo.findAll();

        return shops.stream()
                .filter(shop->shop.getLatitude() != null && shop.getLongitude() != null) // null 값 필터
                .map(shop -> {
                    BigDecimal distance = DistanceUtil.calculateDistance(userLat, userLon, shop.getLatitude(), shop.getLongitude()

                    );

                    return new ShopMapDto(
                            shop.getId(),
                            shop.getName(),
                            shop.getLatitude(),
                            shop.getLongitude(),
                            distance
                    );
                })
                .sorted(Comparator.comparing(ShopMapDto::getDistance))
                .toList();
    }

    // 사용자가 있는 지역 기반 샵 불러오기
    public List<ShopListDto> getShopByRegion(String region, BigDecimal userLat, BigDecimal userLon, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Shop> shopPage = shopRepo.findByAddressContaining(region, pageable);

        List<ShopListDto> result = new ArrayList<>();

        for (Shop shop : shopPage.getContent()) {
            ShopImageDto shopImageDto = shopImageService.findThumbnailByShopId(shop.getId());
            boolean hasCoupon = couponService.hasActiveCoupon(shop.getId());


            int reviewCount = reviewService.getReviewCountByShop(shop.getId());
            float avgRating = reviewService.getAverageRatingByShop(shop.getId());

            ShopListDto dto = ShopListDto.from(shop, shopImageDto, avgRating, reviewCount, hasCoupon);

            // 거리 계산
            if (userLat != null && userLon != null &&
                    shop.getLatitude() != null && shop.getLongitude() != null) {

                BigDecimal distance = DistanceUtil.calculateDistance(
                        userLat, userLon,
                        shop.getLatitude(), shop.getLongitude()
                );

                dto.setDistance(distance.setScale(2, RoundingMode.HALF_UP));
            }

            dto.setLatitude(shop.getLatitude());
            dto.setLongitude(shop.getLongitude());

            List<ShopDesigner> designers = shopDesignerRepo.findByShopIdAndIsActiveTrue(shop.getId());

            List<ShopDesignerProfileDto> designerProfileDtos = designers.stream()
                            .map(designer -> {
                                ShopDesignerProfileDto profileDto = new ShopDesignerProfileDto();
                                profileDto .setDesignerId(designer.getDesigner().getId());
                                profileDto .setImgUrl(designer.getDesigner().getImgUrl());
                                return profileDto ;
                            }).toList();
            if (!designerProfileDtos.isEmpty()) {
                dto.setDesignerList(designerProfileDtos);
            }


            result.add(dto);
        }

        result.sort(Comparator.comparing(ShopListDto::getDistance, Comparator.nullsLast(Comparator.naturalOrder())));

        return result;
    }



}

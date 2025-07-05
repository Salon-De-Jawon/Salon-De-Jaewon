package com.salon.service.shop;

import com.salon.dto.management.master.ShopImageDto;
import com.salon.dto.shop.ReviewListDto;
import com.salon.dto.shop.ShopListDto;
import com.salon.dto.user.ShopMapDto;
import com.salon.entity.shop.Shop;
import com.salon.repository.ReviewRepo;
import com.salon.repository.management.master.CouponRepo;
import com.salon.repository.shop.ShopRepo;
import com.salon.util.DistanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepo shopRepo;
    private final ReviewRepo reviewRepo;
    private final CouponRepo couponRepo;

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
}

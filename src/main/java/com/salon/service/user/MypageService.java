package com.salon.service.user;


import com.salon.constant.LikeType;
import com.salon.dto.DayOffShowDto;
import com.salon.dto.shop.CouponListDto;
import com.salon.dto.user.LikeDesignerDto;
import com.salon.dto.user.LikeShopDto;
import com.salon.dto.user.MyTicketListDto;

import com.salon.entity.management.MemberCoupon;
import com.salon.entity.management.Payment;
import com.salon.entity.management.master.Ticket;

import com.salon.entity.shop.SalonLike;
import com.salon.entity.shop.Shop;
import com.salon.repository.management.MemberCouponRepo;
import com.salon.repository.management.PaymentRepo;

import com.salon.repository.management.ShopDesignerRepo;
import com.salon.repository.management.master.TicketRepo;
import com.salon.repository.shop.SalonLikeRepo;
import com.salon.repository.shop.ShopImageRepo;
import com.salon.repository.shop.ShopRepo;
import com.salon.service.management.master.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MypageService {
    private final MemberCouponRepo memberCouponRepo;
    private final TicketRepo ticketRepo;
    private final PaymentRepo paymentRepo;
    private final ShopDesignerRepo shopDesignerRepo;
    private final SalonLikeRepo salonLikeRepo;
    private final ShopRepo shopRepo;
    private final CouponService couponService;
    private final ReviewService reviewService;
    private final ShopImageRepo shopImageRepo;

    // 마이 쿠폰

    public List<CouponListDto> getMyCoupons(Long memberId) {
        LocalDate today = LocalDate.now();
        List<MemberCoupon> memberCoupons = memberCouponRepo.findAvailableCouponsByMemberId(memberId);

        return memberCoupons.stream()
                .map(mc -> CouponListDto.from(mc.getCoupon(), mc.getCoupon().getShop()))
                .toList();
    }

    public List<MyTicketListDto> getMyTicket(Long memberId) {
        List<Ticket> tickets = ticketRepo.findByMemberId(memberId);
        List<MyTicketListDto> result = new ArrayList<>();

        for (Ticket ticket : tickets) {
            List<Payment> payments = paymentRepo.findByTicketId(ticket.getId());

            if (payments.isEmpty()) {
                // 결제 이력이 없는 정액권도 보여주기
                result.add(MyTicketListDto.from(ticket, null));
            } else {
                for (Payment payment : payments) {
                    result.add(MyTicketListDto.from(ticket, payment));
                }
            }
        }

        return result;
    }

    // 내 찜 목록
    // 내 디자이너

    public List<LikeDesignerDto> getDesignerLike(Long memberId) {
        List<SalonLike> salonLikes = salonLikeRepo.findByMemberIdAndLikeType(memberId, LikeType.DESIGNER);

        List<LikeDesignerDto> result = new ArrayList<>();

        for (SalonLike salonLike : salonLikes) {
            Long designerId = salonLike.getTypeId();

            shopDesignerRepo.findById(designerId).ifPresent(shopDesigner -> {
                LikeDesignerDto dto = LikeDesignerDto.from(salonLike, shopDesigner);
                result.add(dto);
            });
        }

        return result;
    }

    public List<LikeShopDto> getShopLike (Long memberId) {
        List<SalonLike> salonLikes = salonLikeRepo.findByMemberIdAndLikeType(memberId, LikeType.SHOP);
        List<LikeShopDto> result = new ArrayList<>();

        for(SalonLike salonLike : salonLikes) {
            Shop shop = shopRepo.findById(salonLike.getTypeId())
                    .orElse(null);

            if (shop == null) continue;

            boolean hasCoupon = couponService.hasActiveCoupon(shop.getId());

            int reviewCount = reviewService.getReviewCountByShop(shop.getId());
            float avgRating = reviewService.getAverageRatingByShop(shop.getId());

            DayOffShowDto dayOffShowDto = new DayOffShowDto(shop.getDayOff());



            LikeShopDto dto = LikeShopDto.from(salonLike, shop, shopImageRepo, avgRating, reviewCount, hasCoupon, dayOffShowDto);


            result.add(dto);
        }

        return result;
    }

}

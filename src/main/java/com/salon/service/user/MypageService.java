package com.salon.service.user;

import com.salon.constant.LikeType;
import com.salon.dto.management.master.DesignerSummaryDto;
import com.salon.dto.shop.CouponListDto;
import com.salon.dto.user.MyTicketListDto;
import com.salon.entity.Member;
import com.salon.entity.management.MemberCoupon;
import com.salon.entity.management.Payment;
import com.salon.entity.management.master.Coupon;
import com.salon.entity.management.master.Ticket;
import com.salon.entity.shop.SalonLike;
import com.salon.entity.shop.Shop;
import com.salon.repository.management.MemberCouponRepo;
import com.salon.repository.management.PaymentRepo;
import com.salon.repository.management.master.CouponRepo;
import com.salon.repository.management.master.TicketRepo;
import com.salon.repository.shop.SalonLikeRepo;
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
    private final SalonLikeRepo salonLikeRepo;

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


}

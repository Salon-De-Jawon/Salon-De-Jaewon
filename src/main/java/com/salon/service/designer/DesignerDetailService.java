package com.salon.service.designer;

import com.salon.constant.LikeType;
import com.salon.dto.designer.DesignerDetailDto;
import com.salon.dto.designer.DesignerHomeDto;
import com.salon.dto.designer.ReviewReplyDto;
import com.salon.dto.management.ServiceForm;
import com.salon.dto.shop.ReviewImageDto;
import com.salon.dto.shop.ReviewListDto;
import com.salon.entity.Review;
import com.salon.entity.ReviewImage;
import com.salon.entity.management.ShopDesigner;
import com.salon.entity.management.master.ShopService;
import com.salon.entity.shop.Reservation;
import com.salon.repository.ReviewImageRepo;
import com.salon.repository.ReviewRepo;
import com.salon.repository.management.ShopDesignerRepo;
import com.salon.repository.management.master.ShopServiceRepo;
import com.salon.repository.shop.ReservationRepo;
import com.salon.repository.shop.SalonLikeRepo;
import com.salon.service.shop.SalonService;
import com.salon.service.user.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DesignerDetailService {


    private final ShopDesignerRepo shopDesignerRepo;
    private final ReviewService reviewService;
    private final SalonLikeRepo salonLikeRepo;
    private final ReviewRepo reviewRepo;
    private final ShopServiceRepo shopServiceRepo;
    private final ReviewImageRepo reviewImageRepo;
    private final ReservationRepo reservationRepo;


    // 디자이너 상세정보 조회
    public DesignerDetailDto getDesignerDetail(Long shopDesignerId) {
        ShopDesigner shopDesigner = shopDesignerRepo.findByIdAndIsActiveTrue(shopDesignerId);

        if (shopDesignerId == null) {
            throw new IllegalArgumentException("존재하지 않는 디자이너 입니다");
        }

        // 디자이너 찜 갯수 조회
        int likeCount = salonLikeRepo.countByLikeTypeAndTypeId(LikeType.DESIGNER,shopDesignerId);

        // 디자이너 리뷰 갯수 조회
        int reviewCount = reviewRepo.countByReservation_ShopDesigner_Id(shopDesignerId);

        // dto로 변환하여 반환
        return DesignerDetailDto.from(shopDesigner,likeCount,reviewCount);
    }



    // 디자이너 리뷰, 전문 시술 분야 , 시술 리스트 ( 홈 섹션)
    public DesignerHomeDto getDesignerHomeInfo (Long shopDesignerId) {

        ShopDesigner shopDesigner = shopDesignerRepo.findByIdAndIsActiveTrue(shopDesignerId);
        if (shopDesignerId == null) {
            throw new IllegalArgumentException("존재하지 않는 디자이너 입니다");
        }

        // 추천 시술 목록
        List<ShopService> recommendedServices = shopServiceRepo.findByShopIdAndIsRecommendedTrue(shopDesignerId);
        List<ServiceForm> serviceForms = recommendedServices.stream()
                .map(ServiceForm :: from)
                .collect(Collectors.toList());

        // 리뷰수 조회
        int reviewCount = reviewRepo.countByReservation_ShopDesigner_Id(shopDesignerId);

        // 리뷰 이미지 썸네일 ( 최대 8장 최신순으로 )
        List<ReviewImage> thumbnails = reviewImageRepo.findByTop8OrderByDesc();
        List<ReviewImageDto> thumbnailDtos = thumbnails.stream()
                .map(ReviewImageDto :: from)
                .collect(Collectors.toList());

        // 디자이너 리뷰 조회
        List<Review> reviews = reviewRepo.findByReservation_shopDesiger(shopDesignerId);
        List<ReviewListDto> reviewListDtos = new ArrayList<>();

        for (Review review : reviews) {
            Reservation res = review.getReservation();
            Long memberId = res.getMember().getId();
            Long shopId = shopDesigner.getShop().getId();

            // 리뷰 이미지
            List<ReviewImageDto> imageDtos = getReviewImages(review.getId());

            // 방문횟수
            int visitCount = reservationRepo.countVisitByMemberAndShop(memberId,shopId);

            // 디자이너 답글
            ReviewReplyDto replyDto = null;
            if (review.getReplyComment() != null && review.getReplyAt() != null){
                replyDto = ReviewReplyDto.from(review);
            }
            ReviewListDto dto = ReviewListDto.from(review,shopDesigner,imageDtos,visitCount,replyDto);
            dto.setMemberName(res.getMember().getName());
            reviewListDtos.add(dto);
        }

        return DesignerHomeDto.from(serviceForms,reviewCount,thumbnailDtos,reviewListDtos);

    }

    private List<ReviewImageDto> getReviewImages(Long reviewId) {
        List<ReviewImage> imageList = reviewImageRepo.findByReviewId(reviewId);
        return imageList.stream()
                .filter(Objects::nonNull)
                .map(ReviewImageDto::from)
                .collect(Collectors.toList());
    }

}

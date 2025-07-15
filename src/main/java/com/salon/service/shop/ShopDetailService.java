package com.salon.service.shop;



import com.salon.constant.LikeType;
import com.salon.constant.ServiceCategory;
import com.salon.dto.designer.DesignerListDto;
import com.salon.dto.designer.ReviewReplyDto;
import com.salon.dto.management.ServiceForm;
import com.salon.dto.management.master.ShopImageDto;
import com.salon.dto.shop.ReviewImageDto;
import com.salon.dto.shop.ReviewListDto;
import com.salon.dto.shop.ShopDetailDto;
import com.salon.dto.shop.ShopServiceSectionDto;
import com.salon.entity.Member;
import com.salon.entity.Review;
import com.salon.entity.ReviewImage;
import com.salon.entity.management.Designer;
import com.salon.entity.management.ShopDesigner;
import com.salon.entity.management.master.DesignerService;
import com.salon.entity.management.master.ShopService;
import com.salon.entity.shop.Reservation;
import com.salon.entity.shop.Shop;
import com.salon.entity.shop.ShopImage;
import com.salon.repository.ReviewImageRepo;
import com.salon.repository.ReviewRepo;
import com.salon.repository.management.ShopDesignerRepo;
import com.salon.repository.management.master.DesignerServiceRepo;
import com.salon.repository.management.master.ShopServiceRepo;
import com.salon.repository.shop.ReservationRepo;
import com.salon.repository.shop.SalonLikeRepo;
import com.salon.repository.shop.ShopImageRepo;
import com.salon.repository.shop.ShopRepo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ShopDetailService {

    private final ShopRepo shopRepo;
    private final ShopImageRepo shopImageRepo;
    private final ShopServiceRepo shopServiceRepo;
    private final ShopDesignerRepo shopDesignerRepo;
    private final ReviewRepo reviewRepo;
    private final ReviewImageRepo reviewImageRepo;
    private final SalonLikeRepo salonLikeRepo;
    private final ReservationRepo reservationRepo;
    private  final DesignerServiceRepo designerServiceRepo;


    // 특정 미용실의 상세정보 조회
    public ShopDetailDto getShopDetail(Long shopId) {

        // 미용실 기본 정보 조회
        Shop shop = shopRepo.findById(shopId).orElseThrow(() -> new NoSuchElementException("해당 미용실의 정보가 존재하지 않습니다"));

        // 미용실 이미지 목록 조회 (없을경우 null반환)
        List<ShopImage> images = shopImageRepo.findByShopId(shopId);
        List<ShopImageDto> imageDtos = images.isEmpty() ? null : images.stream().map(ShopImageDto::from).toList();


        // 해당 미용실의 좋아요 수 조회
        int likeCount = salonLikeRepo.countByLikeTypeAndTypeId(LikeType.SHOP,shopId);

        // 미용실 평균 평점 조회
        float avgRating = reviewRepo.averageRatingByShopId(shopId);


        // Shop Entity -> ShopDetailDto로 변환
        ShopDetailDto dto = ShopDetailDto.from(shop,likeCount,avgRating);


        // 이미지 리스트를 Dtod에 추가 세팅
        dto.setShopImageDtos(imageDtos);
        dto.setRating(avgRating); // 반올림 후 정수값으로 세팅
        dto.setDayOff(shop.getDayOff());




        return dto;
    }

    // 특정 미용실의 추천 시술 목록 조회  -> 홈 섹션
    public List<ServiceForm> getRecommededService(Long shopId){
        List<ShopService> recommended = shopServiceRepo.findByShopIdAndIsRecommendedTrue(shopId);
        if (recommended.isEmpty()) return Collections.emptyList();


        return recommended.stream()
                .map(ServiceForm :: from)
                .collect(Collectors.toList());
    }

    // 특정 미용실에 소속되어있는 디자이너 리스트 조회 (찜수 ,리뷰수 포함) -> 홈 섹션
    public List<DesignerListDto> getDesignersByShopId(Long shopId) {

        // 해당 샵에 소속된 디자이너 목록 조회
        List<ShopDesigner> shopDesigners = shopDesignerRepo.findByShopIdAndIsActiveTrue(shopId);

        // 각 디자이너에 대한 dto 변환
        return shopDesigners.stream()
                .map(sd -> {

                    DesignerService service = designerServiceRepo.findByShopDesignerId(sd.getId()).orElse(null);

                    Long designerId = sd.getDesigner().getMember().getId();
                    // 찜 갯수
                    int likeCount = salonLikeRepo.countByLikeTypeAndTypeId(LikeType.DESIGNER, designerId);

                    // 리뷰 갯수

                    int reviewCount = reviewRepo.countByReservation_ShopDesigner_Id(sd.getId());

                    return DesignerListDto.from(sd, likeCount, reviewCount, service);
                }).collect(Collectors.toList());
    }



    // 카테고리별 시술 리스트  -> 시술목록 섹션
    public ShopServiceSectionDto getShopServiceSections(Long shopId) {
        ShopServiceSectionDto serviceSectionDto = new ShopServiceSectionDto();
        serviceSectionDto.setRecommended(new ArrayList<>());
        serviceSectionDto.setCategoryMap(new LinkedHashMap<>());

        // 추천시술
        List<ShopService> recommended = shopServiceRepo.findByShopIdAndIsRecommendedTrue(shopId);
        List<ServiceForm> recommendedForms = recommended.stream()
                .map(ServiceForm :: from)
                .toList();
        serviceSectionDto.setRecommended(recommendedForms);


        // 카테고리별 시술
        Map<ServiceCategory, List<ServiceForm>> categoryMap = new LinkedHashMap<>();

        for(ServiceCategory category : ServiceCategory.values()){
            List<ShopService> services = shopServiceRepo.findByShopIdAndCategoryIn(shopId,List.of(category));
            List<ServiceForm> forms = services.stream()
                    .map(ServiceForm::from)
                    .toList();

            categoryMap.put(category,forms);
        }

        serviceSectionDto.setCategoryMap(categoryMap);
        return serviceSectionDto;
    }

    // 특정 샵에 소속되어 있는 디자이너 리스트 -> 디자이너 섹션
    public List<DesignerListDto> getDesignersByShop(Long shopId, Long memberId) {
        List<ShopDesigner> shopDesigners = shopDesignerRepo.findByShopIdAndIsActiveTrue(shopId);

        List<DesignerListDto> designerListDtos = new ArrayList<>();

        for (ShopDesigner sd : shopDesigners) {

            // 디자이너 연차
            int workingYear = sd.getDesigner().getWorkingYears();

            // 리뷰수 , 찜수 (좋아요 수)
            int reviewCount = reviewRepo.countByReservation_ShopDesigner_Id(sd.getId());
            int likeCount = salonLikeRepo.countByLikeTypeAndTypeId(LikeType.DESIGNER, sd.getId());

            // 디자이너 서비스 조회
            DesignerService designerService = designerServiceRepo.findByShopDesignerId(sd.getId())
                    .orElse(null);

            // 전문 시술 문자열 만들기
            String specialties = " ";
            if (designerService != null) {
                specialties = convertToSpecialtyString(designerService.getAssignedCategories());

            }

            // 전문시술 분야 + 연차 형식의 문자열 조합
            String profileSummary = specialties + "(" + workingYear + "년차)";

            // Dto 가공
            DesignerListDto dto = DesignerListDto.from(sd,likeCount,reviewCount, designerService);
            dto.setWorkingYear(workingYear);
            dto.setProfileSummary(profileSummary);


            designerListDtos.add(dto);
        }

        return  designerListDtos;
    }

    // 전문 시술 분야를 문자열로 만들기 위한 메서드
    private String convertToSpecialtyString(List<ServiceCategory> categories) {
        if (categories == null || categories.isEmpty()) return  "전문시술 분야 없음";
        return categories.stream()
                .map(ServiceCategory::getLabel)
                .collect(Collectors.joining(" , ")) + " 전문";

    }



    // 해당 미용실에 소속되어 있는 디자이너들의 리뷰 리스트
    public List<ReviewListDto> getFilteredReviews(Long shopId, Long designerId ,ServiceCategory category, String sortType) {

        // 1. 미용실의 shopid 추출
        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("해당 미용실이 존재하지 않습니다: " + shopId));


        // 미용실에 소속된 디자이너 id 추출
        List<ShopDesigner> shopDesignerList = shopDesignerRepo.findByShopIdAndIsActiveTrue(shopId);



        //  디자이너들의 예약 정보 조회
        List<Long> shopDesignerIds = new ArrayList<>();

        for(ShopDesigner designer : shopDesignerList) {
            Long shopDesignerId = designer.getId();

            shopDesignerIds.add(shopDesignerId);
        }

        List<Reservation> reservations = reservationRepo.findAllByShopDesignerIdIn(shopDesignerIds);
        if (reservations.isEmpty()) return Collections.emptyList();

        // 예약 ID 리스트 추출
        List<Long> reservationIds = reservations.stream()
                .map(Reservation::getId)
                .toList();

        //  리뷰 조회
        List<Review> reviews = reviewRepo.findByReservation_ShopDesigner_Designer_Id(designerId);

        //  카테고리 필터링
        if (category != null) {
            reviews = reviews.stream()
                    .filter(r -> {
                        ShopService service = r.getReservation().getShopService();
                        return service != null && service.getCategory() == category;
                    })
                    .collect(Collectors.toList());
        }

        //  정렬 조건 적용
        Comparator<Review> comparator = switch (sortType) {
            case "rating_high" -> Comparator.comparingInt(Review::getRating).reversed();
            case "rating_low" -> Comparator.comparingInt(Review::getRating);
            default -> Comparator.comparing(Review::getCreateAt).reversed();
        };
        reviews.sort(comparator);


        //  리뷰 → DTO 변환
        List<ReviewListDto> result = new ArrayList<>();

        for (Review review : reviews) {
            Reservation reservation = review.getReservation();
            if (reservation == null) continue;

            Member member = reservation.getMember();
            if (member == null) continue;
            ShopDesigner shopDesigner = reservation.getShopDesigner();

            // 방문 횟수 (같은 미용실 + 같은 회원 기준)
            int visitCount = (int) reservations.stream()
                    .filter(r -> r.getMember().getId().equals(member.getId()))
                    .count();

            // 리뷰 이미지 최대 8장
            List<ReviewImageDto> imageDtos = reviewImageRepo.findAllByReview_Id(review.getId()).stream()
                    .limit(8)
                    .map(ReviewImageDto:: from)
                    .toList();

            // 디자이너 답글 DTO
            ReviewReplyDto replyDto = ReviewReplyDto.from(review);

            // DTO 변환 및 추가
            ReviewListDto dto = ReviewListDto.from(review, shopDesigner, imageDtos, visitCount, replyDto);
            result.add(dto);
        }

        return result;
    }



}

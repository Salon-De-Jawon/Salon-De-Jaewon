package com.salon.service.user;


import com.salon.constant.UploadType;
import com.salon.dto.UploadedFileDto;
import com.salon.dto.user.ReviewCreateDto;

import com.salon.entity.Review;
import com.salon.entity.ReviewImage;
import com.salon.entity.shop.Reservation;
import com.salon.repository.ReviewImageRepo;
import com.salon.repository.ReviewRepo;
import com.salon.repository.shop.ReservationRepo;
import com.salon.util.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepo reviewRepo;
    private final ReservationRepo reservationRepo;
    private final FileService fileService;
    private final ReviewImageRepo reviewImageRepo;

    // 샵 리뷰수
    public int getReviewCountByShop(Long shopId) {
        return reviewRepo.countAllByShopId(shopId);
    }

    public float getAverageRatingByShop(Long shopId) {
        float avg = reviewRepo.averageRatingByShopId(shopId);
        return Math.round(avg * 10) / 10f;
    }

    // 리뷰 저장 서비스
    public void saveReview(ReviewCreateDto dto) {
        Reservation reservation = reservationRepo.findById(dto.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        Review review = dto.to(reservation);
        reviewRepo.save(review);


        if (dto.getReviewImages() != null) {
            for(MultipartFile multipartFile : dto.getReviewImages()) {
                if(multipartFile != null && !multipartFile.isEmpty()) {
                    UploadedFileDto uploadedFile = fileService.upload(multipartFile, UploadType.REVIEW);

                    ReviewImage image = new ReviewImage();
                    image.setReview(review);
                    image.setOriginalName(uploadedFile.getOriginalFileName());
                    image.setImgName(uploadedFile.getFileName());
                    image.setImgUrl(uploadedFile.getFileUrl());

                    reviewImageRepo.save(image);
                }
            }
        }

    }



}

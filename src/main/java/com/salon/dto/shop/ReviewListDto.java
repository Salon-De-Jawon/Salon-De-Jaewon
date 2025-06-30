package com.salon.dto.shop;

import com.salon.entity.Member;
import com.salon.entity.Review;
import com.salon.entity.management.Designer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReviewListDto {

    private Long id; //리뷰테이블 아이디
    private String memberName; // 리뷰작성자 이름
    private LocalDateTime createAt; // 리뷰작성 날짜
    private int visitCount; // 사용자 방문 횟수
    private int rating; // 평점
    private String comment; // 리뷰내용
    private List<ReviewImageDto> reviewImg; // 리뷰이미지
    private String designerName; // 시술한 디자이너 이름
    private int designerWorkingYears; // 시술한 디자이너 연차



    // Review(Entity) -> ReviewListDto
    public static ReviewListDto from (Review review, List<ReviewImageDto> reviewImg, Designer designer, int visitCount){
        ReviewListDto reviewListDto = new ReviewListDto();

        reviewListDto.setId(review.getId());
        reviewListDto.setMemberName(review.getReservation().getMember().getName());
        reviewListDto.setCreateAt(review.getCreateAt());
        reviewListDto.setRating(review.getRating());
        reviewListDto.setComment(review.getComment());
        reviewListDto.setReviewImg(reviewImg);
        reviewListDto.setVisitCount(visitCount);
        reviewListDto.setDesignerName(designer.getMember().getName());
        reviewListDto.setDesignerWorkingYears(designer.getWorkingYears());

        return reviewListDto;
    }


}

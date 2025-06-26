package com.salon.dto.designer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewReplyDto {

    private Long reviewId; // 리뷰테이블 아이디
    private String designerName; // 디자이너이름
    private String designerPositton; // 디자이너 직급
    private String designerImg; // 디자이너 프로필 이미지
    private String replyComment; // 디자이너 답글 내용
    private String replyAt; //답글 작성 날짜

}

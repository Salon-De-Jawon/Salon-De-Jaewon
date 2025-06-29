package com.salon.repository.shop;

import com.salon.constant.LikeType;
import com.salon.entity.shop.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepo extends JpaRepository<Like,Long> {
    
    // 사용자가 찜한 항목 리스트 조회(디자이너, 매장 구분)
    List<Like> findByMemberIdAndLikeType(Long memberId, LikeType likeType);
    
    // 특정 대상(디자이너, 매장)에 대한 찜 여부 확인
    boolean findByMemberIdAndTypeIdAndLikeType(Long memberId, Long typeId, LikeType likeType);
    
    // 미용실 / 디자이너 좋아요 수
    int countByLikeTypeAndTypeId(LikeType likeType, Long targetId);
}

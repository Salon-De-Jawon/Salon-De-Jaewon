package com.salon.repository.management;

import com.salon.entity.management.MemberCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberCardRepo extends JpaRepository<MemberCard, Long> {

    // 해당 예약의 회원카드 메모 가져오기
    MemberCard findByReservationId(Long reservationId);

    // 해당 회원의 회원카드목록 가져오기(최신순)
//    List<MemberCard> findByMemberIdOrderByCreateAtDesc(Long memberId);



}

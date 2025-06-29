package com.salon.service.management.master;

import com.salon.constant.LeaveStatus;
import com.salon.dto.management.LeaveRequestDto;
import com.salon.entity.Member;
import com.salon.entity.management.LeaveRequest;
import com.salon.entity.management.ShopDesigner;
import com.salon.repository.management.LeaveRequestRepo;
import com.salon.repository.management.PaymentRepo;
import com.salon.repository.management.ShopDesignerRepo;
import com.salon.repository.management.master.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterService {

    private final AttendanceRepo attendanceRepo;
    private final CouponRepo couponRepo;
    private final DesignerServiceRepo designerServiceRepo;
    private final ServiceRepo serviceRepo;
    private final ShopClosedDateRepo shopClosedDateRepo;
    private final TicketRepo ticketRepo;
    private final LeaveRequestRepo leaveRequestRepo;
    private final PaymentRepo paymentRepo;
    private final ShopDesignerRepo shopDesignerRepo;

    // 소속 미용실 휴가요청 목록 가져오기
    public List<LeaveRequestDto> getLeaveRequestList(Member member){

        ShopDesigner designer = shopDesignerRepo.findByMemberId(member.getId());

        List<LeaveRequest> leaveRequestList = leaveRequestRepo.findByDesignerIdOrderByRequestAtDesc(designer.getId());
        List<LeaveRequestDto> list = new ArrayList<>();

        for(LeaveRequest request : leaveRequestList){
            list.add(LeaveRequestDto.from(request));
        }

        return list;
    }

    // 디자이너 휴가 상태 변경시
    @Transactional
    public void updateLeaveRequest(Long leaveRequestId, LeaveStatus status){
        LeaveRequest leaveRequest = leaveRequestRepo.findById(leaveRequestId).orElseThrow(()
                -> new IllegalStateException("없는 요청읾"));

        leaveRequest.setStatus(status);
        leaveRequest.setApprovedAt(LocalDateTime.now());

    }






}

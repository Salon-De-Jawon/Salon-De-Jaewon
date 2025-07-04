package com.salon.service.management.master;

import com.salon.constant.LeaveStatus;
import com.salon.constant.LikeType;
import com.salon.dto.designer.DesignerListDto;
import com.salon.dto.management.LeaveRequestDto;
import com.salon.dto.management.master.DesignerResultDto;
import com.salon.dto.management.master.DesignerSearchDto;
import com.salon.entity.Member;
import com.salon.entity.management.Designer;
import com.salon.entity.management.LeaveRequest;
import com.salon.entity.management.ShopDesigner;
import com.salon.entity.shop.Shop;
import com.salon.repository.MemberRepo;
import com.salon.repository.ReviewRepo;
import com.salon.repository.management.DesignerRepo;
import com.salon.repository.management.LeaveRequestRepo;
import com.salon.repository.management.PaymentRepo;
import com.salon.repository.management.ShopDesignerRepo;
import com.salon.repository.management.master.*;
import com.salon.repository.shop.SalonLikeRepo;
import com.salon.repository.shop.ShopRepo;
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
    private final SalonLikeRepo salonLikeRepo;
    private final ReviewRepo reviewRepo;
    private final MemberRepo memberRepo;
    private final DesignerRepo designerRepo;
    private final ShopRepo shopRepo;



    // 소속 미용실 휴가요청 목록 가져오기
    public List<LeaveRequestDto> getLeaveRequestList(Long shopId){

        List<LeaveRequest> leaveRequestList = leaveRequestRepo.findByShopDesigner_Shop_IdOrderByRequestAtDesc(shopId);
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

    // 미용실 소속 디자이너 목록 가져오기
    public List<DesignerListDto> getDesignerList(Long shopId){

        List<ShopDesigner> designerList = shopDesignerRepo.findByShopIdAndIsActiveTrue(shopId);

        List<DesignerListDto> dtoList = new ArrayList<>();
        for(ShopDesigner designer : designerList){
            int likeCount = salonLikeRepo.countByLikeTypeAndTypeId(LikeType.DESIGNER, designer.getId());
            int reviewCount = reviewRepo.countByReservation_ShopDesigner_Id(designer.getId());
            dtoList.add(DesignerListDto.from(designer, likeCount, reviewCount));
        }

        return dtoList;
    }

    // 미용실 디자이너 검색 후 목록 보여주기
    public List<DesignerResultDto> getSearchResult(DesignerSearchDto searchDto){

        List<Designer> designerList = designerRepo.findByMember_NameAndMember_Tel(searchDto.getName(), searchDto.getTel());

        List<DesignerResultDto> dtoList = new ArrayList<>();
        for(Designer designer : designerList){
            dtoList.add(DesignerResultDto.from(designer));
        }

        return dtoList;
    }

    // 미용실 디자이너 등록 시 저장 메서드
    @Transactional
    public void addDesigner(Long memberId, Long shopId){

        Designer designer = designerRepo.findByMember_Id(memberId);
        Shop shop = shopRepo.findById(shopId).orElseThrow();

        ShopDesigner entity = new ShopDesigner();
        entity.setDesigner(designer);
        entity.setShop(shop);
        entity.setActive(true);

        shopDesignerRepo.save(entity);
    }

    // 미용실 디자이너 삭제 시
    @Transactional
    public void deleteDesigner(Long shopDesignerId){

        ShopDesigner shopDesigner = shopDesignerRepo.findByIdAndIsActiveTrue(shopDesignerId);
        shopDesigner.setActive(false);

    }






}

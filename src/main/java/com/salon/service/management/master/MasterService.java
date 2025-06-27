package com.salon.service.management.master;

import com.salon.repository.management.LeaveRequestRepo;
import com.salon.repository.management.PaymentRepo;
import com.salon.repository.management.ShopDesignerRepo;
import com.salon.repository.management.master.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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





}

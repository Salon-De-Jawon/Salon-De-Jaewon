package com.salon.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manage")
public class ManageController {

    // 메인페이지
    @GetMapping("")
    public String getMain(){

        return "management/main";
    }

    // 근태 관리
    @GetMapping("/attendance")
    public String attendance(){

        return "management/attendance";
    }

    // 일일 통계
    @GetMapping("/statistics")
    public String statistics(){

        return "management/statistics";
    }

    // 매출 내역
    @GetMapping("/sales")
    public String sales(){

        return "management/sales";
    }

    // 결제 등록
    @GetMapping("/sales/new")
    public String newSales(){

        return "management/paymentForm";
    }

    // 결제 상세페이지
    @GetMapping("/sales/detail")
    public String salesDetail(){

        return "management/paymentDetail";
    }

    // 예약 내역
    @GetMapping("/reservations")
    public String reservation(){

        return "management/reservations";
    }

    // 회원관리카드
    @GetMapping("/member-card")
    public String memberCard(){

        return "management/memberCard";
    }





}

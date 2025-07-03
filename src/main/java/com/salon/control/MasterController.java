package com.salon.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/master")
public class MasterController {

    // 메인페이지
    @GetMapping("")
    public String getMain(){


        return "master/main";
    }

    // 매장 근태 관리
    @GetMapping("/attendance")
    public String attendance(){

        return "master/attendance";
    }

    // 디자이너 관리
    @GetMapping("/designer-list")
    public String designerList(){

        return "master/designerList";
    }

    // 예약 관리
    @GetMapping("/reservations")
    public String reservations(){

        return "master/reservations";
    }

    // 매장 관리
    @GetMapping("/shop-edit")
    public String shopEdit(){

        return "master/shopEdit";
    }

    // 쿠폰 관리
    @GetMapping("/coupons")
    public String coupon(){

        return "master/coupons";
    }

}

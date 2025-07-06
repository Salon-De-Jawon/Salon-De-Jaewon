package com.salon.control;

import com.salon.config.CustomUserDetails;
import com.salon.dto.shop.ShopListDto;
import com.salon.dto.user.ShopMapDto;
import com.salon.dto.user.ShopMarkerDto;
import com.salon.dto.user.SignUpDto;
import com.salon.dto.user.UserLocateDto;
import com.salon.service.shop.ShopService;
import com.salon.service.user.KakaoMapService;
import com.salon.service.user.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class MainController {
    @Value("${kakao.maps.api.key}")
    private String kakaoMapsKey;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestKey;

    private final MemberService memberService;
    private final KakaoMapService kakaoMapService;
    private final ShopService shopService;


    @GetMapping("/")
    public String mainpage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){

        if (userDetails != null) {
            String name = userDetails.getMember().getName();
            model.addAttribute("name", name);
        }

        model.addAttribute("kakaoMapsKey", kakaoMapsKey);

        return "/mainpage";
    }


    // 위도경도를 주소로 변환
    @GetMapping("/api/coord-to-address")
    public ResponseEntity<?> getAddressFormCoords (@RequestParam double x, @RequestParam double y) {
        try {
            UserLocateDto location = kakaoMapService.getUserAddress(x, y);
            return ResponseEntity.ok(location);
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 메인 지도에 표시할 샵 목록 불러오기

    @GetMapping("/api/shops")
    @ResponseBody
    public List<ShopMapDto> getShopsForMap(@RequestParam BigDecimal lat, @RequestParam BigDecimal lon) {
        return shopService.getAllShopsForMap(lat, lon);
    }

    @GetMapping("/shopList")
    public String shopListPage() {
        return "/user/shopList";
    }

    @GetMapping("/api/shop-list")
    @ResponseBody
    public List<ShopListDto> getShopListByRegion(
            @RequestParam String region,
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lon,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return shopService.getShopByRegion(region, lat, lon, page, size);
    }


    @GetMapping("/compare")
    public String comparePage() {
        return "/user/compare";
    }

    @GetMapping("/login")
    public  String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if(error != null) {
            model.addAttribute("loginErrorMsg", "옳지 않은 아이디나 비밀번호입니다.");
        }
        return "/user/login";
    }


    @GetMapping("/signUp")
    public String signUpPage(Model model) {
        model.addAttribute("signUpDto", new SignUpDto());
        return "/user/signUp";
    }

    @PostMapping("/signUp")
    public String signUp(@ModelAttribute SignUpDto dto, HttpSession session, BindingResult result, Model model) {

        Boolean verified = (Boolean) session.getAttribute("authSuccess");

        if (verified == null || !verified) {
            result.rejectValue("email", "NotVerified", "이메일 인증을 완료해주세요.");
            return "user/signUpForm";
        }

        // 인증이 완료되었으면 회원 가입 진행
        memberService.register(dto);

        session.removeAttribute("authSuccess"); // 인증 정보 제거 (1회용)
        return "redirect:/login";
    }


    // js에서 보낸 아이디 찾기 요청 처리

    @GetMapping("/check-id")
    @ResponseBody
    public Map<String, Boolean> checkLoginId(@RequestParam String loginId) {
       boolean exists = memberService.existsByLoginId(loginId);
       return Map.of("exists", exists);
    }



}

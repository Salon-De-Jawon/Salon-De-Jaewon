package com.salon.control;

import com.salon.config.CustomUserDetails;
import com.salon.dto.user.SignUpDto;
import com.salon.service.user.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@AllArgsConstructor
public class MainController {

    private final MemberService memberService;

    @GetMapping("/")
    public String mainpage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){

        if (userDetails != null) {
            String name = userDetails.getUser().getName();
            model.addAttribute("name", name);
        }

        return "/mainpage";
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

    @GetMapping("/shopList")
    public String shopListPage() {
        return "/user/shopList";
    }

    // js에서 보낸 아이디 찾기 요청 처리

    @GetMapping("/check-id")
    @ResponseBody
    public Map<String, Boolean> checkLoginId(@RequestParam String loginId) {
       boolean exists = memberService.existsByLoginId(loginId);
       return Map.of("exists", exists);
    }



}

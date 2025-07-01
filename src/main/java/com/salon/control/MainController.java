package com.salon.control;

import com.salon.config.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class MainController {

    @GetMapping("/")
    public String mainpage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){

        if (userDetails != null) {
            String name = userDetails.getUser().getName();
            model.addAttribute("name", name);
        }

        return "/mainpage";
    }

    @GetMapping("/login")
    public  String loginPage() {
        return "/user/login";
    }


}

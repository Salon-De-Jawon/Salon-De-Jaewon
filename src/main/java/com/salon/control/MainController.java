package com.salon.control;

import com.salon.config.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class MainController {

    @GetMapping("/")
    public String mainpage(){


        return "/mainpage";
    }

    @GetMapping("/loginpage")
    public  String loginPage() {
        return "/user/login";
    }


}

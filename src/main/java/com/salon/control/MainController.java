package com.salon.control;

import lombok.AllArgsConstructor;
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

    @GetMapping("/login")
    public  String loginPage() {
        return "/user/login";
    }


}

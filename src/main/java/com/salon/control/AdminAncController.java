package com.salon.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/anc")
public class AdminAncController {
    @GetMapping("/")
    public String main(){
        return "announcement";
    }
}

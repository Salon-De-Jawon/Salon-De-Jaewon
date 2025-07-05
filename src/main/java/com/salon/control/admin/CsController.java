package com.salon.control.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cs")
public class CsController {
    @GetMapping("/question")
    public String question(){
        return "admin/question";
    }
    @GetMapping("/problem")
    public String problem(){
        return "admin/problem";
    }
    @GetMapping("/apply")
    public String apply(){
        return "admin/shopApply";
    }
}

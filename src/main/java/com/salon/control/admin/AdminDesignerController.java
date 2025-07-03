package com.salon.control.admin;

import com.salon.dto.admin.ApplyDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/designer")
public class AdminDesignerController {

    @GetMapping("")
    public String requestForm(Model model){
        model.addAttribute("applyDto", new ApplyDto());
        return "admin/apply";
    }
}

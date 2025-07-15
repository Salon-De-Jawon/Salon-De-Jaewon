package com.salon.control;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/designerProfile")
public class DesignerController {


    @GetMapping("")
    public String getdesignerProfilePage(){


        return "shop/designerProfile";
    }



}

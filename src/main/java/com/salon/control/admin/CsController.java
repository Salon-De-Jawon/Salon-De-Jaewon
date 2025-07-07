package com.salon.control.admin;

import com.salon.config.CustomUserDetails;
import com.salon.dto.admin.CsCreateDto;
import com.salon.dto.admin.CsDetailDto;
import com.salon.dto.admin.CsListDto;
import com.salon.entity.Member;
import com.salon.service.admin.CsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cs")
public class CsController {
    private final CsService csService;
    @GetMapping("/question")
    public String question(Model model){
        model.addAttribute("csCreateDto", new CsCreateDto());
        return "admin/question";
    }
    @PostMapping("/question")
    public String questionSave(@AuthenticationPrincipal CustomUserDetails userDetails,
                               CsCreateDto csCreateDto,
                               @RequestParam("files") List<MultipartFile> files){
        Member member = userDetails.getMember();
        csService.questionSave(csCreateDto, member, files);
        return "redirect:/admin/cs/questionList";
    }
    @GetMapping("/questionList")
    public String questionList(Model model){
        List<CsListDto> csListDtoList = csService.List();
        model.addAttribute("csListDtoList", csListDtoList);
        return "admin/csList";
    }
    @GetMapping("/reply")
    public String reply(@AuthenticationPrincipal CustomUserDetails userDetails,
                        @RequestParam Long id,
                        Model model){
        CsCreateDto csCreateDto = csService.create(id);
        CsListDto csListDto = csService.list(id);
        boolean isAdmin = userDetails.getMember().getRole().name().equals("ADMIN");
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("csCreateDto", csCreateDto);
        model.addAttribute("csListDto", csListDto);
        model.addAttribute("csDetailDto", new CsDetailDto());
        model.addAttribute("isAdmin", isAdmin);
        return "admin/reply";
    }
    @PostMapping("/reply")
    public String replySave(){
        return "redirect:/admin/cs/questionList";
    }
    @GetMapping("/problem")
    public String problem(){
        return "admin/problem";
    }
    @GetMapping("/shopApply")
    public String shopApply(){
        return "admin/shopApply";
    }
    @GetMapping("bannerApply")
    public String bannerApply(){
        return "admin/bannerApply";
    }
}

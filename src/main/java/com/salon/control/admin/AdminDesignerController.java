package com.salon.control.admin;

import com.salon.config.CustomUserDetails;
import com.salon.dto.admin.ApplyDto;
import com.salon.entity.Member;
import com.salon.entity.admin.Apply;
import com.salon.service.admin.DesApplyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/admin/designer")
@RequiredArgsConstructor
public class AdminDesignerController {

    private final DesApplyService desApplyService;

    @GetMapping("/request")
    public String requestForm(Model model){
        model.addAttribute("applyDto", new ApplyDto());
        return "admin/apply";
    }
    @PostMapping("/request")
    public String request(@ModelAttribute ApplyDto applyDto,
                          @AuthenticationPrincipal CustomUserDetails userDetails,
                          @RequestParam(value="file", required = false) MultipartFile file,
                          HttpSession session,
                          Model model){
        Member member = userDetails.getMember();
        if(member == null){
            return "redirect:/";
        }
        try{
            desApplyService.Apply(applyDto, member, file);
            model.addAttribute("message", "디자이너 승인 요청이 완료되었습니다.");
            return "redirect:/";
        } catch (Exception e){
            model.addAttribute("error", "요청 처리 중 오류가 발생했습니다." + e.getMessage());
            return "admin/apply";
        }
    }

    @GetMapping("/list")
    public String list(Model model){
        List<Apply> list = desApplyService.list();
        model.addAttribute("applyList", list);
        return "admin/applyList";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id,
                          HttpSession session,
                          @AuthenticationPrincipal CustomUserDetails userDetails){
        Member member = userDetails.getMember();
        desApplyService.approve(id, member);
        return "redirect:/admin/designer/list";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id,
                         HttpSession session,
                         @AuthenticationPrincipal CustomUserDetails userDetails){
        System.out.println("reject 컨트롤러 진입 : id = " + id);
        Member member = userDetails.getMember();
        desApplyService.reject(id, member);
        return "redirect:/admin/designer/list";
    }

}

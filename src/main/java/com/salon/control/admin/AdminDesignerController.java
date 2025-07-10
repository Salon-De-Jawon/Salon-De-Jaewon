package com.salon.control.admin;

import com.salon.config.CustomUserDetails;
import com.salon.dto.admin.ApplyDto;
import com.salon.entity.Member;
import com.salon.entity.admin.Apply;
import com.salon.repository.admin.ApplyRepo;
import com.salon.service.admin.DesApplyService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${ocr.api}")
    private String ocrApiKey;

    private final DesApplyService desApplyService;
    private final ApplyRepo applyRepo;

    @GetMapping("/request")
    public String requestForm(Model model,
                              @AuthenticationPrincipal CustomUserDetails userDetails){

        Member member = userDetails.getMember();



        model.addAttribute("applyDto", new ApplyDto());
        model.addAttribute("ocrApiKey", ocrApiKey);
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
        } catch (IllegalStateException e){
            model.addAttribute("error", "이미 디자이너 신청을 하셨습니다.");
            return "admin/apply";
        } catch (Exception e){
            model.addAttribute("error", "요청 처리 중 오류가 발생했습니다." + e.getMessage());
            model.addAttribute("ocrApiKey", ocrApiKey);
            return "admin/apply";
        }
    }

    @GetMapping("/list")
    public String list(Model model){
        model.addAttribute("designerApplyList", desApplyService.listDesigner());
        return "admin/applyList";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id,
                          @Valid
                          HttpSession session,
                          @AuthenticationPrincipal CustomUserDetails userDetails){
        Member member = userDetails.getMember();
        desApplyService.approveDesigner(id, member);
        return "redirect:/admin/designer/list";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id,
                         HttpSession session,
                         @AuthenticationPrincipal CustomUserDetails userDetails){
        Member member = userDetails.getMember();
        desApplyService.rejectDesigner(id, member);
        return "redirect:/admin/designer/list";
    }

}

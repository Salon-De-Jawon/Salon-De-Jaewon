package com.salon.control.admin;

import com.salon.config.CustomUserDetails;
import com.salon.constant.WebTarget;
import com.salon.dto.admin.ApplyDto;
import com.salon.entity.Member;
import com.salon.entity.admin.Apply;
import com.salon.entity.admin.WebNotification;
import com.salon.repository.WebNotificationRepo;
import com.salon.service.WebNotificationService;
import com.salon.service.admin.DesApplyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/designer")
@RequiredArgsConstructor
public class AdminDesignerController {
    @Value("${ocr.api}")
    private String ocrApiKey;

    private final DesApplyService desApplyService;

    private final WebNotificationRepo webNotificationRepo;
    private final WebNotificationService webNotificationService;

    @GetMapping("/request")
    public String requestForm(Model model){
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
        System.out.println("✅ POST 요청 진입");
        System.out.println("applyNumber: " + applyDto.getApplyNumber());
        System.out.println("file: " + (file != null ? file.getOriginalFilename() : "없음"));
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
            model.addAttribute("ocrApiKey", ocrApiKey);
            return "admin/apply";
        }
    }

    @GetMapping("/list")
    public String list(Model model){
        List<Apply> list = desApplyService.list();
        System.out.println("조회된 리스트: " + list);
        System.out.println("리스트 크기: " + list.size());
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

//      desApplyService.reject(id, member);

        //알림 관련코드 (DesApplyService 수정했음. receiverId 가 나오도록 반환타입 변경.)

        Long receiverId = desApplyService.reject(id, member);

        // 알림 저장용 코드

        webNotificationService.notify(
                receiverId, // 알림을 받는 대상
                "디자이너 신청이 거절 되었습니다.",  // message 회원에게 갈 메세지
                WebTarget.DESAPPLY, // 알림 종류
                id  // targetId는 desApplyId 등 처리 대상 ID
        );

        return "redirect:/admin/designer/list";
    }

}

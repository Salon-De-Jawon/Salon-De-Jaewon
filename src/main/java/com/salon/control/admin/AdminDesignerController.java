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

//        WebNotification webNotification = new WebNotification();
//
//        webNotification.setMessage("디자이너 신청이 승인되었습니다.");
//        webNotification.setWebTarget(WebTarget.DESAPPLY);
//        webNotification.setTargetId(receiverId);
//        webNotification.setRead(false);
//        webNotification.setCreateAt(LocalDateTime.now());
//
//        webNotificationRepo.save(webNotification);
//
//        webNotificationService.sendWebSocketNotification(receiverId, webNotification);
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
        WebNotification notify = new WebNotification();

        notify.setMessage("디자이너 신청이 거절 되었습니다.");
        notify.setWebTarget(WebTarget.DESAPPLY);
        notify.setTargetId(receiverId);
        notify.setRead(false);
        notify.setCreateAt(LocalDateTime.now());

        // 알림 DB 저장
        webNotificationRepo.save(notify);

        // 웹소켓 전송

        webNotificationService.sendWebSocketNotification(receiverId, notify);


        return "redirect:/admin/designer/list";
    }

}

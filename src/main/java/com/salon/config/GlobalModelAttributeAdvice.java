package com.salon.config;

import com.salon.repository.WebNotificationRepo;
import com.salon.service.WebNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.salon.config.CustomUserDetails;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributeAdvice {

    private final WebNotificationService webNotificationService;

    @ModelAttribute("isAdmin")
    public boolean addIsAdmin(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            return userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    @ModelAttribute("userRole")
    public String populateUserRole(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getMember() == null) {
            return null;
        }
        return userDetails.getMember().getRole().name(); // Enum → String
    }


    // 이거 웹알림 전역

    @ModelAttribute
    public void addNotificationInfo(@AuthenticationPrincipal CustomUserDetails user,
                                    org.springframework.ui.Model model) {

        if (model.containsAttribute("unreadCnt")   // 개별 컨트롤러가 이미 넣었으면 건너뜀
                || model.containsAttribute("currentUserId")) {
            return;
        }

        if (user == null) {
            model.addAttribute("currentUserId", null);
            model.addAttribute("unreadCnt", 0L);
        } else {
            Long memberId  = user.getId();
            long unreadCnt = webNotificationService.countUnreadByMemberId(memberId);

            model.addAttribute("currentUserId", memberId);
            model.addAttribute("unreadCnt", unreadCnt);
        }
    }

}
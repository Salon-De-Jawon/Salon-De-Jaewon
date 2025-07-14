package com.salon.config;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.salon.config.CustomUserDetails;

@ControllerAdvice
public class GlobalModelAttributeAdvice {

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
}
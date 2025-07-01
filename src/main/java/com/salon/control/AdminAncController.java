package com.salon.control;

import com.salon.dto.admin.AncCreateDto;
import com.salon.dto.admin.AncListDto;
import com.salon.entity.Member;
import com.salon.repository.MemberRepo;
import com.salon.service.admin.AncService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/anc")
public class AdminAncController {
    private final AncService ancService;
    private final MemberRepo memberRepo;
    @GetMapping("/")
    public String list(Model model){
        List<AncListDto> ancListDtoList = new ArrayList<>();
        ancListDtoList = ancService.list();
        model.addAttribute("ancListDto", ancListDtoList);
        return "announcement";
    }
    @GetMapping("/detail")
    public String detail(){return "announcementDetail";}
    @GetMapping("/create")
    public String create(Model model){
        AncCreateDto ancCreateDto = new AncCreateDto();
        model.addAttribute("ancCreateDto", ancCreateDto);
        return "announcementCreate";}
    @PostMapping("/registration")
    public String registration(@AuthenticationPrincipal UserDetails userDetails, AncCreateDto ancCreateDto){
        Member member = new Member();

        member = memberRepo.findByLoginId(userDetails.getUsername());
        ancService.registration(ancCreateDto, member);
        return "redirect:/";
    }
    @GetMapping("/update")
    public String update(){return "announcementUpdate";}
}

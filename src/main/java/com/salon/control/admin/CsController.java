package com.salon.control.admin;

import com.salon.config.CustomUserDetails;
import com.salon.constant.Role;
import com.salon.dto.BizStatusDto;
import com.salon.dto.admin.*;
import com.salon.entity.Member;
import com.salon.entity.admin.Apply;
import com.salon.entity.management.Designer;
import com.salon.entity.management.ShopDesigner;
import com.salon.entity.management.master.Coupon;
import com.salon.entity.shop.Shop;
import com.salon.repository.MemberRepo;
import com.salon.repository.management.DesignerRepo;
import com.salon.repository.management.ShopDesignerRepo;
import com.salon.repository.management.master.CouponRepo;
import com.salon.repository.shop.ShopRepo;
import com.salon.service.admin.AncService;
import com.salon.service.admin.CsService;
import com.salon.service.admin.DesApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cs")
public class CsController {
    @Value("${api.encodedKey}")
    private String encodedKey;

    private final CouponRepo couponRepo;
    private final ShopDesignerRepo shopDesignerRepo;
    private final DesignerRepo designerRepo;
    private final ShopRepo shopRepo;
    private final AncService ancService;
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/api/bizCheck")
    public ResponseEntity<?> check(@RequestParam String bizNo) {
        System.out.println("bizNo: " + bizNo);
        // 요청 JSON 구성
        Map<String, Object> body = new HashMap<>();
        body.put("b_no", List.of(bizNo));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        String url = "https://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey=" + encodedKey;

        try {
            ResponseEntity<BizStatusDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    BizStatusDto.class
            );
            System.out.println("API 응답: " + response.getBody());
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("API 오류: " + e.getMessage());
        }

    }

    private final CsService csService;

    private final DesApplyService desApplyService;
    @GetMapping("")
    public String list(Model model){
        List<AncListDto> ancListDtoList = ancService.list();
        model.addAttribute("ancListDto", ancListDtoList);
        return "admin/announcement";
    }

    @GetMapping("/questionList")
    public String questionList(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){
        List<CsListDto> csListDtoList = csService.List();
        Member member = userDetails.getMember();
        if(member.getRole() == Role.ADMIN){
            csListDtoList = csService.findAll();
        } else {
            csListDtoList = csService.findByMember(member);
        }
        model.addAttribute("csListDtoList", csListDtoList);
        return "admin/csList";
    }
    @GetMapping("/reply")
    public String reply(@AuthenticationPrincipal CustomUserDetails userDetails,
                        @RequestParam Long id,
                        Model model){
        CsCreateDto csCreateDto = csService.create(id);
        CsListDto csListDto = csService.list(id);
        CsDetailDto csDetailDto = csService.detail(id);
        boolean isAdmin = userDetails.getMember().getRole().name().equals("ADMIN");
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("csCreateDto", csCreateDto);
        model.addAttribute("csListDto", csListDto);
        model.addAttribute("csDetailDto", csDetailDto);
        model.addAttribute("isAdmin", isAdmin);
        return "admin/reply";
    }
    @PostMapping("/reply")
    public String replySave(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @ModelAttribute CsDetailDto csDetailDto){
        Long id = csDetailDto.getId();
        CsCreateDto csCreateDto = csService.create(id);
        CsListDto csListDto = csService.list(id);
        csDetailDto.setLoginId(userDetails.getUsername());
        csService.replySave(csDetailDto, csCreateDto, csListDto);
        return "redirect:/admin/cs/questionList";
    }
    @GetMapping("/shopList")
    public String shopList(Model model){
        List<Apply> list = csService.listShop();
        System.out.println("샵 신청 목록 수: " + list.size());
        for (Apply a : list) {
            System.out.println("신청: " + a.getApplyNumber() + " / 타입: " + a.getApplyType() + " / 상태: " + a.getStatus());
        }
        model.addAttribute("shopApplyList", list);
        return "admin/shopList";
    }
    @PostMapping("/approve/{id}")
    public String approveShop(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails){
        csService.approveShop(id, userDetails.getMember());
        return "redirect:/admin/cs/shopList";
    }
    @PostMapping("/reject/{id}")
    public String rejectShop(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails userDetails){
        csService.rejectShop(id, userDetails.getMember());
        return "redirect:/admin/cs/shopList";
    }
    @GetMapping("/bannerList")
    public String bannerList(Model model){
        List<CouponBannerListDto> couponBannerListDtoList = csService.bannerList();
        model.addAttribute("couponBannerListDtoList", couponBannerListDtoList);
        return "admin/bannerList";
    }
    @GetMapping("/bannerDetail")
    public String bannerDetail(@RequestParam Long id, Model model){
        CouponBannerDetailDto couponBannerDetailDto = csService.bannerDetail(id);
        model.addAttribute("couponBannerDetailDto", couponBannerDetailDto);
        return "admin/bannerDetail";
    }
    @PostMapping("/couponBanner/approve")
    public String bannerApprove(@RequestParam Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        Member admin = userDetails.getMember();
        csService.bannerApprove(id, admin);
        return "redirect:/admin/cs/bannerList";
    }
    @PostMapping("/couponBanner/reject")
    public String bannerReject(@RequestParam Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        Member admin = userDetails.getMember();
        csService.bannerReject(id, admin);
        return "redirect:/admin/cs/bannerList";
    }
}

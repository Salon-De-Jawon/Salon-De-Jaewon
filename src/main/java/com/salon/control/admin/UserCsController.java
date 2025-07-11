package com.salon.control.admin;

import com.salon.config.CustomUserDetails;
import com.salon.dto.BizStatusDto;
import com.salon.dto.admin.AncDetailDto;
import com.salon.dto.admin.AncListDto;
import com.salon.dto.admin.ApplyDto;
import com.salon.dto.admin.CsCreateDto;
import com.salon.entity.Member;
import com.salon.entity.management.master.Coupon;
import com.salon.repository.management.master.CouponRepo;
import com.salon.service.admin.AncService;
import com.salon.service.admin.CsService;
import com.salon.service.admin.DesApplyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cs")
public class UserCsController {
    private final AncService ancService;
    private final DesApplyService desApplyService;
    @Value("${ocr.api}")
    private String ocrApiKey;
    @Value("${api.encodedKey}")
    private String encodedKey;
    private final RestTemplate restTemplate = new RestTemplate();
    private final CsService csService;
    private final CouponRepo couponRepo;
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
    // 공지 관련
    @GetMapping("")
    public String list(Model model){
        List<AncListDto> ancListDtoList = ancService.list();
        model.addAttribute("ancListDto", ancListDtoList);
        return "admin/announcement";
    }
    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long id, Model model){
        AncDetailDto ancDetailDto = ancService.detail(id);
        model.addAttribute("ancDetailDto", ancDetailDto);
        return "admin/announcementDetail";}
    // 디자이너 신청
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
    // 고객 문의 관련
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
    @GetMapping("/shopApply")
    public String shopApply(Model model){
        model.addAttribute("applyDto", new ApplyDto());
        return "admin/shopApply";
    }
    @PostMapping("/shopApply")
    public String shopRegistration(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   @ModelAttribute ApplyDto applyDto,
                                   Model model){

        System.out.println("폼 제출됨: " + applyDto.getApplyNumber());

        Member member = userDetails.getMember();
        try {
            csService.registration(applyDto, member);
        } catch(IllegalStateException e){
            model.addAttribute("applyDto", applyDto);
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/shopApply";
        }
        return "redirect:/admin/cs/shopList";
    }

}

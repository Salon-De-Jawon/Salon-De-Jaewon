package com.salon.control.admin;

import com.salon.config.CustomUserDetails;
import com.salon.dto.BizStatusDto;
import com.salon.dto.admin.CsCreateDto;
import com.salon.dto.admin.CsDetailDto;
import com.salon.dto.admin.CsListDto;
import com.salon.entity.Member;
import com.salon.service.admin.CsService;
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
@RequestMapping("/admin/cs")
public class CsController {
    @Value("${api.encodedKey}")
    private String encodedKey;

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
    @GetMapping("/shopApply")
    public String shopApply(){
        return "admin/shopApply";
    }
    @GetMapping("bannerApply")
    public String bannerApply(){
        return "admin/bannerApply";
    }
}

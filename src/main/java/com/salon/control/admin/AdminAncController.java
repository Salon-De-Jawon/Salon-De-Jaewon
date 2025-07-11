package com.salon.control.admin;

import com.salon.config.CustomUserDetails;
import com.salon.dto.admin.AncCreateDto;
import com.salon.dto.admin.AncDetailDto;
import com.salon.dto.admin.AncListDto;
import com.salon.entity.Member;
import com.salon.repository.MemberRepo;
import com.salon.service.admin.AncService;
import lombok.RequiredArgsConstructor;
import org.codehaus.groovy.control.io.InputStreamReaderSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/anc")
public class AdminAncController {
    private final AncService ancService;
    private final MemberRepo memberRepo;
    @Value("${api.encodedKey}")
    private String encodedKey;
    @Value("${file.anc-file-path}")
    private String ancPath;

        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException("파일이 없거나 디렉토리입니다.");
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8) + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(file.length())
                .body(resource);
    }

    @GetMapping("/create")
    public String create(Model model){
        model.addAttribute("ancCreateDto", new AncCreateDto());

        return "admin/announcementCreate";}

    @PostMapping("/registration")
    public String registration(@AuthenticationPrincipal CustomUserDetails userDetails,
                               AncCreateDto ancCreateDto,
                               @RequestParam("files") List<MultipartFile> files){

        Member member = userDetails.getMember();

        ancService.registration(ancCreateDto, member, files);
        return "redirect:/cs";
    }
    @GetMapping("/update")
    public String updateForm(@RequestParam("id") Long id, Model model){
        AncCreateDto ancCreateDto = ancService.updateForm(id);
        model.addAttribute("ancCreateDto", ancCreateDto);
        return "admin/announcementUpdate";
    }
    @PostMapping("/update")
    public String update(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @ModelAttribute AncCreateDto ancCreateDto){
        Member member = userDetails.getMember();
        ancService.update(ancCreateDto, member);
        return "redirect:/cs";
    }
    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id){
        ancService.delete(id);
        return "redirect:/cs";
    }
}

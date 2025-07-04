package com.salon.service.admin;

import com.salon.constant.ApplyStatus;
import com.salon.constant.ApplyType;
import com.salon.constant.Role;
import com.salon.dto.admin.ApplyDto;
import com.salon.entity.Member;
import com.salon.entity.admin.Apply;
import com.salon.repository.admin.ApplyRepo;
import com.salon.util.OcrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DesApplyService {

    private final ApplyRepo applyRepo;
    private final OcrUtil ocrUtil;

    public void Apply(ApplyDto applyDto, Member member, MultipartFile file) {

        Apply apply = new Apply();
        apply.setMember(member);
        apply.setApplyType(ApplyType.DESIGNER);

        String applyNumber = applyDto.getApplyNumber();
        if((applyNumber == null || applyNumber.trim().isEmpty()) && file != null && !file.isEmpty()){
            String extracted = ocrUtil.extractText(file);
            applyNumber = extractNumberFromText(extracted);
        }
        apply.setApplyNumber(applyDto.getApplyNumber());
        apply.setIssuedDate(LocalDate.now().toString());
        apply.setCreateAt(LocalDateTime.now());
        apply.setStatus(ApplyStatus.WAITING);

        if(file != null && !file.isEmpty()) {
            try{
            String uploadDir = "uploads/certificates/";
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String savedFilename = UUID.randomUUID().toString() + extension;
            File uploadPath = new File(uploadDir);

            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            File savedFile = new File(uploadPath, savedFilename);
            try (FileOutputStream fos = new FileOutputStream(savedFile)) {
                fos.write(file.getBytes());
            }
        }catch (IOException e){
                throw new RuntimeException("파일 저장 중 오류 발생", e);
            }
        }

        applyRepo.save(apply);
    }

    private String extractNumberFromText(String text) {
        if(text == null) return null;

        String[] lines = text.split("\\R");
        for(String line : lines){
            line = line.trim();
            if(line.matches(".*\\d{4}[- ]?\\d{5,8}.*")){
                return line.replaceAll("[^\\d-]", "");
            }
        }
        return null;
    }

    public List<Apply> list() {
        return applyRepo.findByStatus(ApplyStatus.WAITING);
    }

    @Transactional
    public void approve(Long id, Member member) {
        Apply apply = applyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("신청 정보를 찾을 수 없습니다."));
        apply.setStatus(ApplyStatus.APPROVED);
        apply.setApproveAt(LocalDateTime.now());
        apply.setAdmin(member);

        Member applicant = apply.getMember();
        applicant.setRole(Role.DESIGNER);
    }

    @Transactional
    public void reject(Long id, Member member) {
        System.out.println("reject()호출됨, id: " + id);
        Apply apply = applyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("신청 정보를 찾을 수 없습니다."));
        System.out.println("현재 상태 : " + apply.getStatus());
        apply.setStatus(ApplyStatus.REJECTED);
        apply.setApproveAt(LocalDateTime.now());
        apply.setAdmin(member);

        System.out.println("상태 변경 후 : " + apply.getStatus());
    }
}

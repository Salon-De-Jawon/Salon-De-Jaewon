package com.salon.service.admin;

import com.salon.constant.CsStatus;
import com.salon.dto.admin.CsCreateDto;
import com.salon.dto.admin.CsDetailDto;
import com.salon.dto.admin.CsListDto;
import com.salon.entity.Member;
import com.salon.entity.admin.CsCustomer;
import com.salon.entity.admin.CsFile;
import com.salon.repository.MemberRepo;
import com.salon.repository.admin.CsCustomerFileRepo;
import com.salon.repository.admin.CsCustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CsService {
    private final CsCustomerRepo csCustomerRepo;
    private final CsCustomerFileRepo csCustomerFileRepo;
    private final MemberRepo memberRepo;
    public void questionSave(CsCreateDto csCreateDto, Member member, List<MultipartFile> files) {
        CsCustomer csCustomer = CsCreateDto.to(csCreateDto, member);
        csCustomer = csCustomerRepo.save(csCustomer);
        if(files != null && !files.isEmpty()){
            for(MultipartFile file : files){
                if(!file.isEmpty()){
                    String originalName = file.getOriginalFilename();
                    String uuid = UUID.randomUUID().toString();
                    String fileName = uuid + "_" + originalName;
                    String filePath = "C:/upload/" + fileName;

                    try {
                        file.transferTo(new File(filePath));
                    } catch(IOException e){
                        throw new RuntimeException("파일 저장 실패", e);
                    }

                    CsFile csFile = new CsFile();
                    csFile.setOriginalName(originalName);
                    csFile.setFileName(fileName);
                    csFile.setFileUrl("/upload/"+fileName);
                    csFile.setCsCustomer(csCustomer);

                    csCustomerFileRepo.save(csFile);
                }
            }
        }
    }

    public List<CsListDto> List() {
        List<CsCustomer> csCustomerList = csCustomerRepo.findAll();
        List<CsListDto> csListDtoList = new ArrayList<>();
        for(CsCustomer csCustomer:csCustomerList){
            CsListDto csListDto = CsListDto.from(csCustomer);
            csListDtoList.add(csListDto);
        }
        return csListDtoList;
    }

    public CsCreateDto create(Long id) {
        CsCustomer csCustomer = csCustomerRepo.findById(id).get();
        CsCreateDto csCreateDto = CsCreateDto.from(csCustomer);
        return csCreateDto;
    }

    public CsListDto list(Long id) {
        CsCustomer csCustomer = csCustomerRepo.findById(id).get();
        CsListDto csListDto = CsListDto.from(csCustomer);
        return csListDto;
    }

    public void replySave(CsDetailDto csDetailDto, CsCreateDto csCreateDto, CsListDto csListDto) {
        Member admin = memberRepo.findByLoginId(csDetailDto.getLoginId());

        CsCustomer customer = csCustomerRepo.findById(csDetailDto.getId())
                        .orElseThrow(() -> new RuntimeException("문의 내역을 찾을 수 없습니다."));
        customer.setReplyAt(LocalDateTime.now());
        customer.setReplyText(csDetailDto.getReplyText());
        customer.setStatus(CsStatus.COMPLETED);
        customer.setAdmin(admin);
        csCustomerRepo.save(customer);
    }

    public CsDetailDto detail(Long id) {
        CsCustomer csCustomer = csCustomerRepo.findById(id).get();
        CsDetailDto csDetailDto = CsDetailDto.from(csCustomer);
        return csDetailDto;
    }
}

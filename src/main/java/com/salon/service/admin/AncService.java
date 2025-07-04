package com.salon.service.admin;

import com.salon.constant.Role;
import com.salon.dto.admin.AncCreateDto;
import com.salon.dto.admin.AncDetailDto;
import com.salon.dto.admin.AncListDto;
import com.salon.entity.Member;
import com.salon.entity.admin.Announcement;
import com.salon.entity.admin.AnnouncementFile;
import com.salon.repository.MemberRepo;
import com.salon.repository.admin.AnnouncementFileRepo;
import com.salon.repository.admin.AnnouncementRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AncService {
    private final MemberRepo memberRepo;
    private final AnnouncementRepo announcementRepo;
    private final AnnouncementFileRepo announcementFileRepo;
    public void registration(AncCreateDto ancCreateDto, Member member, List<MultipartFile> files) {

        Announcement announcement = AncCreateDto.to(ancCreateDto, member);
        announcement.setWriteAt(LocalDateTime.now());

        Announcement saved = announcementRepo.save(announcement);
        if(files != null && !files.isEmpty()) {
            for(MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String originalName = file.getOriginalFilename();
                    String uuid = UUID.randomUUID().toString();
                    String fileName = uuid + "_" + originalName;
                    String filePath = "C:/upload/" + fileName;

                    try {
                        file.transferTo(new File(filePath));
                    } catch (IOException e) {
                        throw new RuntimeException("파일 저장 실패", e);
                    }

                    AnnouncementFile announcementFile = new AnnouncementFile();
                    announcementFile.setOriginalName(originalName);
                    announcementFile.setFileName(fileName);
                    announcementFile.setFileUrl("/upload/" + fileName);
                    announcementFile.setAnnouncement(saved);

                    announcementFileRepo.save(announcementFile);
                }
            }
        }
        announcementRepo.save(announcement);
    }

    public List<AncListDto> list() {
        /*List<Announcement> announcementList = announcementRepo.findAll();*/
        List<Announcement> announcementList = announcementRepo.findAllByOrderByWriteAtDesc();
        List<AncListDto> ancListDtoList = new ArrayList<>();
        for(Announcement announcement : announcementList){
            /*ancListDtoList.add(AncListDto.from(announcement));*/
            AncListDto ancListDto = AncListDto.from(announcement);

            List<AnnouncementFile> files = announcementFileRepo.findByAnnouncement(announcement);
            if(!files.isEmpty()){
                AnnouncementFile file = files.get(0);
                ancListDto.setFileName(file.getOriginalName());
                ancListDto.setFileUrl(file.getFileUrl());
            }

            ancListDtoList.add(ancListDto);
        }

        return ancListDtoList;
    }

    public AncDetailDto detail(Long id) {
        Announcement announcement = announcementRepo.findById(id).get();
        AncDetailDto ancDetailDto = AncDetailDto.from(announcement);

        List<AnnouncementFile> files = announcementFileRepo.findByAnnouncement(announcement);
        if(files != null && !files.isEmpty()) {
            AnnouncementFile file = files.get(0);
            ancDetailDto.setOriginalName(file.getOriginalName());
            ancDetailDto.setFileName(file.getFileName());
            ancDetailDto.setFileUrl(file.getFileUrl());

            List<String> fileUrls = files.stream()
                    .map(AnnouncementFile::getFileUrl)
                    .toList();
            ancDetailDto.setFileUrls(fileUrls);
        }
        Role role = announcement.getRole();

        boolean hasPrev = announcementRepo.findTopByRoleAndIdLessThanOrderByIdDesc(role, id).isPresent();
        boolean hasNext = announcementRepo.findTopByRoleAndIdGreaterThanOrderByIdAsc(role, id).isPresent();

        ancDetailDto.setHasPrev(hasPrev);
        ancDetailDto.setHasNext(hasNext);

        announcementRepo.findTopByRoleAndIdLessThanOrderByIdDesc(role, id)
                .ifPresent(prev -> ancDetailDto.setPrevId(prev.getId()));
        announcementRepo.findTopByRoleAndIdGreaterThanOrderByIdAsc(role, id)
                .ifPresent(next -> ancDetailDto.setNextId(next.getId()));
        return ancDetailDto;
    }

    public AncCreateDto updateForm(Long id) {
        Announcement announcement = announcementRepo.findById(id).orElseThrow();

        return AncCreateDto.from(announcement);
    }

    public void update(AncCreateDto ancCreateDto, Member member) {
        Announcement announcement = announcementRepo.findById(ancCreateDto.getId())
                .orElseThrow();
        announcement.setTitle(ancCreateDto.getTitle());
        announcement.setContent(ancCreateDto.getContent());
        announcement.setRole(ancCreateDto.getRole());
        announcement.setAdmin(member);

        announcementRepo.save(announcement);
    }

    public void delete(Long id) {
        Announcement announcement = announcementRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지가 존재하지 않습니다."));
        List<AnnouncementFile> files = announcementFileRepo.findByAnnouncement(announcement);
        for(AnnouncementFile file : files) {
            announcementFileRepo.delete(file);
        }
        announcementRepo.delete(announcement);
    }
}

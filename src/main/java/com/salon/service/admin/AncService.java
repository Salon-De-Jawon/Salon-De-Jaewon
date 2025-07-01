package com.salon.service.admin;

import com.salon.dto.admin.AncCreateDto;
import com.salon.dto.admin.AncListDto;
import com.salon.entity.Member;
import com.salon.entity.admin.Announcement;
import com.salon.repository.MemberRepo;
import com.salon.repository.admin.AnnouncementRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AncService {
    private final MemberRepo memberRepo;
    private final AnnouncementRepo announcementRepo;
    public void registration(AncCreateDto ancCreateDto, Member member) {

        Announcement announcement = AncCreateDto.to(ancCreateDto, member);
        announcementRepo.save(announcement);
    }

    public List<AncListDto> list() {
        List<Announcement> announcementList = announcementRepo.findAll();
        List<AncListDto> ancListDtoList = new ArrayList<>();
        for(Announcement announcement : announcementList){
            ancListDtoList.add(AncListDto.from(announcement));
        }
        return ancListDtoList;
    }
}

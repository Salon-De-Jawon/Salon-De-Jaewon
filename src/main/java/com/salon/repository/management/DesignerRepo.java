package com.salon.repository.management;

import com.salon.entity.management.Designer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesignerRepo extends JpaRepository<Designer, Long> {

    // 디자이너 검색결과
    List<Designer> findByMember_NameAndMember_Tel(String name, String tel);

    // MemberId 로 디자이너 가져오기
    Designer findByMember_Id(Long memberId);

    // MemberId 로 디자이너 가져오기 222 옵셔널을 씀.
    Optional<Designer> findByMemberId(Long id);
}

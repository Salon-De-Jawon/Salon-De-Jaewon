package com.salon.repository.management;

import com.salon.entity.management.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepo extends JpaRepository<LeaveRequest, Long> {

    // 디자이너 휴가 목록
    List<LeaveRequest> findByDesignerIdOrderByRequestAtDesc(Long designerId);

}

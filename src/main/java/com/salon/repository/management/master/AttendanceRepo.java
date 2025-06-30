package com.salon.repository.management.master;

import com.salon.entity.management.master.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, Long> {

    // 해당 디자이너 근태목록
    List<Attendance> findByShopDesigner_IdAndClockInBetweenOrderByIdDesc(Long designerId, LocalDate start, LocalDate end);

}

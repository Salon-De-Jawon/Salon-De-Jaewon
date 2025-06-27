package com.salon.repository.management.master;

import com.salon.entity.management.master.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, Long> {

    // 해당 디자이너 근태목록
    List<Attendance> findByDesignerIdAndDateBetweenOrderByIdDesc(Long designerId, LocalDate start, LocalDate end);

    // 출근 여부
    boolean existsByDesignerIdAndClockInBetween(Long id, LocalDateTime clockIn, LocalDateTime clockOut);

    // 당일
    Optional<Attendance> findByDesignerIdAndClockInBetween(Long id, LocalDateTime dayStart, LocalDateTime dayEnd);
}

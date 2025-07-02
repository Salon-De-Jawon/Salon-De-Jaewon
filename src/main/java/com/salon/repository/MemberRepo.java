package com.salon.repository;

import com.salon.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MemberRepo extends JpaRepository<Member, Long> {
    Member findByLoginId(String loginId);

    boolean existsByEmail(String email);

    Member findByEmail(String email);
}

package com.salon.repository.admin;

import com.salon.entity.admin.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplyRepo extends JpaRepository<Apply, Long> {

}

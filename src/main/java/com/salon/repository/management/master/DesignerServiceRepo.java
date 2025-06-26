package com.salon.repository.management.master;

import com.salon.entity.management.master.DesignerService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignerServiceRepo extends JpaRepository<DesignerService, Long> {

}

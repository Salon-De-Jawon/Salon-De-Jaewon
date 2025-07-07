package com.salon.repository.designer;

import com.salon.entity.management.master.DesignerService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DesignerServiceRepo extends JpaRepository<DesignerService, Long> {

   Optional <DesignerService> findByShopDeisngerId(Long designerId);
}

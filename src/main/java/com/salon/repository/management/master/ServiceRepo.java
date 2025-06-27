package com.salon.repository.management.master;

import com.salon.constant.ServiceCategory;
import com.salon.entity.management.master.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepo extends JpaRepository<Service, Long> {

    // 미용실 시술 목록
    List<Service> findByShopId(Long shopId);

    // 미용사 담당 시술목록 (카테고리별로)
    List<Service> findByDesignerIdAndCategoryIn(Long id, List<ServiceCategory> categories);

}

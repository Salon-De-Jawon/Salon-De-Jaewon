package com.salon.repository.admin;

import com.salon.entity.admin.CsCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsCustomerRepo extends JpaRepository<CsCustomer, Long> {
}

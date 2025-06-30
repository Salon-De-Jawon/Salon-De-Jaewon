package com.salon.repository;

import com.salon.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewImageRepo extends JpaRepository<ReviewImage, Long> {


}

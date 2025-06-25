package com.salon.entity.management.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class DesignerService {
    @Id
    @Column(name = "designer_service_id")
    Long id;

}

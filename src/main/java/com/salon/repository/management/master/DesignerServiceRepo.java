package com.salon.repository.management.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class DesignerServiceRepo {
    @Id
    @Column(name = "designer_service_id")
    Long id;

}

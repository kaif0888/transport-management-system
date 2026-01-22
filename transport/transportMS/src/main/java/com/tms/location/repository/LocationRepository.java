package com.tms.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tms.location.entity.LocationEntity;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
}

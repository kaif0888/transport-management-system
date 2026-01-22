package com.tms.branch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tms.branch.entity.BranchEntity;
import java.util.List;

public interface BranchRepository extends JpaRepository<BranchEntity, Long> {
    List<BranchEntity> findByLocation_LocationId(Long locationId);
}

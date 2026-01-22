package com.tms.manifest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.manifest.entity.ManifestEntity;

public interface ManifestRepository extends JpaRepository<ManifestEntity, Long> {
   
}

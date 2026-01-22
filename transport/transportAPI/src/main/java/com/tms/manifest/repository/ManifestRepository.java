package com.tms.manifest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.manifest.entity.ManifestEntity;

public interface ManifestRepository extends JpaRepository<ManifestEntity, String> {
	List<ManifestEntity> findByManifestIdStartingWith(String prefix);
	Optional<ManifestEntity> findByDispatch_DispatchId(String dispatchId);



   
}

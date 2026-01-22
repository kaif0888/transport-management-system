package com.tms.boxes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.boxes.entity.HSNCodeEntity;

@Repository
public interface HSNCodeRepository extends JpaRepository<HSNCodeEntity, String> {
    List<HSNCodeEntity> findByIsActiveTrue();
    List<HSNCodeEntity> findByDescriptionContainingIgnoreCase(String description);
}

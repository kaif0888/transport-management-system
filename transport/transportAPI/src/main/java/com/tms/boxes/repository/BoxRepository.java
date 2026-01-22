package com.tms.boxes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.boxes.entity.BoxEntity;


@Repository
public interface BoxRepository extends JpaRepository<BoxEntity, String> {
    Optional<BoxEntity> findByBoxCode(String boxCode);
    List<BoxEntity> findByStatus(String status);
}

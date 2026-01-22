package com.tms.boxes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.boxes.entity.BoxProductEntity;

@Repository
public interface BoxProductRepository extends JpaRepository<BoxProductEntity, Long> {
    List<BoxProductEntity> findByBoxId(String boxId);
    void deleteByBoxIdAndProductId(String boxId, String productId);
}

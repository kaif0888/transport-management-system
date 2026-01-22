package com.tms.boxes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.boxes.entity.OrderBoxEntity;

@Repository
public interface OrderBoxRepository extends JpaRepository<OrderBoxEntity, Long> {
    List<OrderBoxEntity> findByOrderId(String orderId);
    void deleteByOrderIdAndBoxId(String orderId, String boxId);
    Optional<OrderBoxEntity> findByOrderIdAndBoxId(String orderId, String boxId);
//    List<OrderBoxEntity> findByOrderId(String orderId);
    List<OrderBoxEntity> findByBoxId(String boxId);

}

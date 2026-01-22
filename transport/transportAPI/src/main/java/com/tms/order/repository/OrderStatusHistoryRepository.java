package com.tms.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.order.entity.OrderStatusHistoryEntity;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistoryEntity, Long> {
    List<OrderStatusHistoryEntity> findByOrderIdOrderByChangedAtAsc(String orderId);

}

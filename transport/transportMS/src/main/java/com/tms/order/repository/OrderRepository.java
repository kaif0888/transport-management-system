package com.tms.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tms.order.entity.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

//	List<OrderEntity> findByCustomerId(Long customerId);
	
	@Query("SELECT o FROM OrderEntity o WHERE o.customer.customerId = :customerId")
    List<OrderEntity> findByCustomerId(@Param("customerId") Long customerId);

}

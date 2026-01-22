package com.tms.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tms.order.entity.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

//	List<OrderEntity> findByCustomerId(String customerId);
	
	@Query("SELECT o FROM OrderEntity o WHERE o.customer.customerId = :customerId")
    List<OrderEntity> findByCustomerId(@Param("customerId") String customerId);
	
	List<OrderEntity> findByOrderIdStartingWith(String prefix);
	
	Optional<OrderEntity> findByOrderId(String orderId);

	


}

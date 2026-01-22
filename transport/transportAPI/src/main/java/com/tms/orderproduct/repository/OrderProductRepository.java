package com.tms.orderproduct.repository;

import com.tms.orderproduct.entity.OrderProductEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProductEntity, String> {
	List<OrderProductEntity> findByOrderProductIdStartingWith(String prefix);

//	Optional<OrderProductEntity> findByOrderProductId(String orderId);
//
//	Optional<OrderProductEntity> findByOrder_OrderId(String orderId);
	Optional<OrderProductEntity> findByOrderProductId(String orderProductId);
	List<OrderProductEntity> findByOrder_OrderId(String orderId);

	List<OrderProductEntity> findByOrderOrderId(String orderId);
	

	 
	 

}

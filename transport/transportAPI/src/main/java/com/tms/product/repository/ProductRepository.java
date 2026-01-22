package com.tms.product.repository;

import com.tms.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
	List<ProductEntity> findByProductIdStartingWith(String prefix);
	
	@Query(value = "SELECT * FROM product WHERE order_id = :orderId", nativeQuery = true)
	List<ProductEntity> findByOrderId(@Param("orderId") String orderId);

}


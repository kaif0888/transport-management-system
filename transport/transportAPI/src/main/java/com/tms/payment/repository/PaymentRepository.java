package com.tms.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tms.payment.entity.PaymentEntity;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {

	List<PaymentEntity> findByPaymentIdStartingWith(String prefix);

    @Query("SELECT COALESCE(SUM(p.advancePayment), 0) FROM PaymentEntity p WHERE p.customerId = :customerId")
    Double getTotalAdvancePaidByCustomer(@Param("customerId") String customerId);
//    
//    @Query("SELECT COALESCE(SUM(p.advancePayment), 0) FROM PaymentEntity p WHERE p.orderId = :orderId")
//    Double getTotalPaidForOrder(@Param("orderId") String orderId);

	@Query("SELECT COALESCE(SUM(p.advancePayment), 0) FROM PaymentEntity p WHERE p.customerId = :customerId")
	Double getTotalPaidForCustomer(@Param("customerId") String customerId);

	@Query("SELECT COALESCE(SUM(p.advancePayment), 0) FROM PaymentEntity p WHERE p.orderId = :orderId")
	Double getTotalPaidForOrder(@Param("orderId") String orderId);
	
	List<PaymentEntity> findByOrderId(String orderId);
	
	@Query("SELECT COALESCE(SUM(p.advancePayment), 0) FROM PaymentEntity p WHERE p.orderId = :orderId")
	Double getTotalAdvancePaidForOrder(@Param("orderId") String orderId);
	
	@Query("SELECT COALESCE(MAX(p.totalPayment), 0) FROM PaymentEntity p WHERE p.orderId = :orderId")
	Double getTotalPaymentForOrder(@Param("orderId") String orderId);
	
	Optional<PaymentEntity> findByCustomerId(String customerId);
	
	Optional<PaymentEntity> findByOrder_OrderId(String orderId);
	


}

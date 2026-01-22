package com.tms.customer.repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tms.customer.entity.CustomerEntity;
import com.tms.invoice.entity.InvoiceEntity;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {
	List<CustomerEntity> findByCustomerIdStartingWith(String prefix);

//	Optional<InvoiceEntity> findByIdCustomer(Integer idCustomer);

//	OptionalInt findById(Integer idCustomer);
	
//	 Optional<CustomerEntity> findByIdCustomer(Integer idCustomer);
	
	 Optional<CustomerEntity> findByCustomerId(String customerId);
	 
	


}

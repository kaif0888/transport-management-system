package com.tms.bookingCost.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.bookingCost.entity.BookingCostEntity;

public interface BookingCostRepository extends JpaRepository<BookingCostEntity,Long>{
   
	
}

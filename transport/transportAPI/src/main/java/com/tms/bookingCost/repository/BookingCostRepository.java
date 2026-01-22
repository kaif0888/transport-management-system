package com.tms.bookingCost.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.bookingCost.entity.BookingCostEntity;

public interface BookingCostRepository extends JpaRepository<BookingCostEntity,String>{
	List<BookingCostEntity> findByBookingCostIdStartingWith(String prefix);
	
}

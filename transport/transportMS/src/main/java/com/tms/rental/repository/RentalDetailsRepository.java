package com.tms.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.rental.entity.RentalDetailsEntity;

public interface RentalDetailsRepository extends JpaRepository<RentalDetailsEntity, Long> {
	
}

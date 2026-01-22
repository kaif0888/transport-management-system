package com.tms.rental.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.rental.entity.RentalDetailsEntity;

public interface RentalDetailsRepository extends JpaRepository<RentalDetailsEntity, String> {
	List<RentalDetailsEntity> findByRentalDetailsIdStartingWith(String prefix);


	
}

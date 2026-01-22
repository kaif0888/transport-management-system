package com.tms.vehicletype.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.tms.vehicletype.entity.VehicleTypeEntity;

public interface VehicleTypeRepository  extends JpaRepository<VehicleTypeEntity, String>{
	List<VehicleTypeEntity> findByVehicleTypeIdStartingWith(String prefix);

	boolean existsByVehicleTypeName(String string);


}

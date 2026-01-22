package com.tms.vehicletype.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.tms.vehicletype.entity.VehicleTypeEntity;

public interface VehicleTypeRepository  extends JpaRepository<VehicleTypeEntity, Long>{

}

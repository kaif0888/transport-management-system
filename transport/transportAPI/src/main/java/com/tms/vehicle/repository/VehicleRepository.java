package com.tms.vehicle.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tms.vehicle.entity.VehicleEntity;

public interface VehicleRepository extends JpaRepository<VehicleEntity, String> {
	List<VehicleEntity> findByVehicleIdStartingWith(String prefix);

    Optional<VehicleEntity> findByRegistrationNumber(String registrationNumber);
    List<VehicleEntity> findByStatus(String status);
    
    @Query("SELECT v FROM VehicleEntity v WHERE v.vehicleId NOT IN (SELECT d.assignedVehicle.vehicleId FROM DriverEntity d WHERE d.assignedVehicle IS NOT NULL)")
    List<VehicleEntity> findVehiclesNotAssignedToAnyDriver();
    
    @Query("SELECT v FROM VehicleEntity v WHERE v.vehicleId NOT IN (SELECT r.vehicle.vehicleId FROM RentalDetailsEntity r)")
    List<VehicleEntity> findVehiclesNotRented();
    
    List<VehicleEntity> findByRegistrationNumberStartingWith(String registrationNumber);

    Optional<VehicleEntity> findByVehiclNumber(String vehiclNumber);

	Optional<VehicleEntity> findByRegistrationNumberOrVehiclNumber(String registrationNumber, String vehiclNumber);

	List<VehicleEntity> findByBranchIdsContaining(String branchIds);
	Optional<VehicleEntity> findByVehicleId(String vehicleId);


    

}
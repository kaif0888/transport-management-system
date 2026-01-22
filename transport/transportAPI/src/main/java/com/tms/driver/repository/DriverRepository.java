package com.tms.driver.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tms.driver.entity.DriverEntity;
import com.tms.vehicle.entity.VehicleEntity;

@Repository
public interface DriverRepository extends JpaRepository<DriverEntity, String> {

	List<DriverEntity> findByDriverIdStartingWith(String prefix);

	Optional<DriverEntity> findByLicenseNumber(String licenseNumber);
	Optional<DriverEntity> findByDriverId(String driverId);

	@Query("SELECT d.assignedVehicle FROM DriverEntity d WHERE d.assignedVehicle IS NOT NULL")
	List<VehicleEntity> findAllAssignedVehicles();

	// New method to find drivers with no assigned vehicle
	@Query("SELECT d FROM DriverEntity d WHERE d.assignedVehicle IS NULL")
	List<DriverEntity> findDriversWithNoVehicle();
	
	@Query("SELECT d FROM DriverEntity d LEFT JOIN FETCH d.documentIds WHERE d.driverId = :driverId")
	Optional<DriverEntity> findByIdWithDocuments(@Param("driverId") String driverId);


	Optional<DriverEntity> findByAssignedVehicle(VehicleEntity assignedVehicle);


}

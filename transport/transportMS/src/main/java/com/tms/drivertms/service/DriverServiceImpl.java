package com.tms.drivertms.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.drivertms.been.AvalaibleDriverBean;
import com.tms.drivertms.been.DriverBeen;
import com.tms.drivertms.entity.DriverEntity;

import com.tms.drivertms.repository.DriverRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.vehicletms.been.VehicleBean;
import com.tms.vehicletms.entity.VehicleEntity;
import com.tms.vehicletms.repository.VehicleRepository;

import io.swagger.annotations.Example;

@Service
public class DriverServiceImpl implements DriverService {

	@Autowired
	private DriverRepository driverRepository;
	@Autowired
	private VehicleRepository vehicleRepository;

	// Method-1 Add a new driver and assign vehicle if present
	@Override
	public DriverBeen addDriver(DriverBeen bean) {
		// Check if a driver with same email already exists
		Optional<DriverEntity> existingDriver = driverRepository.findByDriverEmail(bean.getDriverEmail());
		if (existingDriver.isPresent()) {
			throw new RuntimeException("Driver with email " + bean.getDriverEmail() + " already exists.");
		}

		DriverEntity entity = new DriverEntity();
		entity.setDriverId(bean.getDriverId()); // Setting driver basic info from bean
		entity.setName(bean.getName());
		entity.setLicenseNumber(bean.getLicenceNumber());
		entity.setLicenseExpiryDate(bean.getLicenceExpiryDate());
		entity.setContactNumber(bean.getContactNumber());
		entity.setDriverEmail(bean.getDriverEmail());

		if (bean.getAssignedVehicle() != null && bean.getAssignedVehicle().getVehicleId() != null) { // Check kiya ki
																										// driverBean me
																										// assignedVehicle
																										// diya gaya hai
																										// ya nahi
			Optional<VehicleEntity> vehicleOpt = vehicleRepository.findById(bean.getAssignedVehicle().getVehicleId()); // Vehicle
																														// ID
																														// ke
																														// base
																														// pe
																														// vehicle
																														// DB
																														// se
																														// nikala
			if (vehicleOpt.isPresent()) {
				entity.setAssignedVehicle(vehicleOpt.get()); // Agar vehicle mila toh entity me assign kiya
			} else {
				System.out.println("Vehicle ID not found: "); // Agar vehicle nahi mila toh log print kiya
			}
		} else {
			System.out.println("AssignedVehicle or VehicleId is null");
		}

		driverRepository.save(entity); // Save driver to repository
		BeanUtils.copyProperties(entity, bean); // Copy saved entity back to bean
		return bean;
	}

	public DriverBeen getDriverById(Integer driverId) {
		DriverEntity driver = driverRepository.findById(driverId)
				.orElseThrow(() -> new RuntimeException("Driver not found with ID: " + driverId));

		DriverBeen bean = new DriverBeen();
		bean.setDriverId(driver.getDriverId());
		bean.setName(driver.getName());
		bean.setLicenceNumber(driver.getLicenseNumber());
		bean.setLicenceExpiryDate(driver.getLicenseExpiryDate());
		bean.setContactNumber(driver.getContactNumber());
		bean.setDriverEmail(driver.getDriverEmail());
		bean.setAssignedVehicle(driver.getAssignedVehicle());

		return bean;
	}

	// Methid-2 List all drivers with their assigned vehicles
//                    @Override
//                    public List<DriverBeen> getDrivers() {
//                    return driverRepository.findAll()    //Repository se sabhi drivers uthaye (findAll)
//                    .stream()                      //Stream ke through har ek driver entity ko map kiya
//                    .map(driver -> {
//                    DriverBeen bean = new DriverBeen();
//                    bean.setDriverId(driver.getDriverId());  //Entity se bean me data copy kiya
//                    bean.setName(driver.getName());
//                    bean.setLicenceNumber(driver.getLicenseNumber());
//                    bean.setLicenceExpiryDate(driver.getLicenseExpiryDate());
//                    bean.setContactNumber(driver.getContactNumber());
//                    bean.setAssignedVehicle(driver.getAssignedVehicle()); // include vehicle
//                    bean.setDriverEmail(driver.getDriverEmail());
//                    return bean;
//                   })
//                    .collect(Collectors.toList());   //Sabhi beans ko ek list me collect kiya aur return kiya
//                    } 
	@Autowired
	private FilterCriteriaService<DriverEntity> filterCriteriaService;

	@Override
	public List<DriverBeen> getDriverbyfilterCriteria(List<FilterCriteriaBean> filters, int limit) {
		try {
			List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(DriverEntity.class, filters, limit);
			return (List<DriverBeen>) filteredEntities.stream().map(entity -> convertToBean((DriverEntity) entity))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Error filtering Driver: " + e.getMessage());
		}
	}

	private DriverBeen convertToBean(DriverEntity driverEntity) {
		DriverBeen driverBeen = new DriverBeen();
		driverBeen.setDriverId(driverEntity.getDriverId());
		driverBeen.setLicenceNumber(driverEntity.getLicenseNumber());
		driverBeen.setContactNumber(driverEntity.getContactNumber());
		driverBeen.setName(driverEntity.getName());
		return driverBeen;
	}

	// Mrthod-3 Update driver details (excluding assigned vehicle)
	@Override
	public DriverBeen updateDriver(Integer id, DriverBeen bean) {
		// Find the driver by ID
		DriverEntity driver = driverRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Driver not found with ID: " + id));

		// Check if the license number exists in the database or in the request
		boolean hasLicenseInDb = driver.getLicenseNumber() != null && !driver.getLicenseNumber().trim().isEmpty();
		boolean hasLicenseInRequest = bean.getLicenceNumber() != null && !bean.getLicenceNumber().trim().isEmpty();

		// If there's no license in DB and none in request, throw an error
		if (!hasLicenseInDb && !hasLicenseInRequest) {
			throw new IllegalArgumentException("License number is required and cannot be empty.");
		}

		// Update only the fields that are provided in the request (not null)
		if (bean.getName() != null) {
			driver.setName(bean.getName());
		}

		if (bean.getLicenceNumber() != null) {
			driver.setLicenseNumber(bean.getLicenceNumber());
		}

		if (bean.getLicenceExpiryDate() != null) {
			driver.setLicenseExpiryDate(bean.getLicenceExpiryDate());
		}

		if (bean.getContactNumber() != null) {
			driver.setContactNumber(bean.getContactNumber());
		}

		if (bean.getDriverEmail() != null) {
			driver.setDriverEmail(bean.getDriverEmail());
		}

		// Only update vehicle if it's specifically included in the request
		if (bean.getAssignedVehicle() != null) {
			// If vehicle ID is provided, verify it exists
			if (bean.getAssignedVehicle().getVehicleId() != null) {
				VehicleEntity vehicle = vehicleRepository.findById(bean.getAssignedVehicle().getVehicleId())
						.orElseThrow(() -> new RuntimeException(
								"Vehicle not found with ID: " + bean.getAssignedVehicle().getVehicleId()));
				driver.setAssignedVehicle(vehicle);
			} else {
				// If empty vehicle object is provided, remove vehicle assignment
				driver.setAssignedVehicle(null);
			}
		}

		// Save the updated driver
		driverRepository.save(driver);

		// Convert entity to bean
		DriverBeen updatedBean = new DriverBeen();
		BeanUtils.copyProperties(driver, updatedBean);
		return updatedBean;
	}

	// Method-4: Delete driver by ID
	@Override
	public String deleteDriver(Integer driverId) {
		DriverEntity driver = driverRepository.findById(driverId) // Driver ko uske ID se dhoondhte hain
				.orElseThrow(() -> new RuntimeException("Driver not found")); // Agar nahi mila toh exception throw
																				// karte hain
		driverRepository.delete(driver); // Driver entity ko database se delete kar dete hain (sirf driver delete hoga)
		return "Driver Deleted Successfully :"; // Delete ke baad success message return karte hain
	}

	// New method to get available drivers (without assigned vehicle)
	@Override
	public List<AvalaibleDriverBean> getAvailableDrivers() {
		return driverRepository.findDriversWithNoVehicle().stream().map(driver -> {
			AvalaibleDriverBean availableDriverBean = new AvalaibleDriverBean();
			availableDriverBean.setDriverId(driver.getDriverId());
			availableDriverBean.setName(driver.getName());
			availableDriverBean.setLicenceNumber(driver.getLicenseNumber());
			availableDriverBean.setLicenceExpiryDate(driver.getLicenseExpiryDate());
			availableDriverBean.setContactNumber(driver.getContactNumber());
			return availableDriverBean;
		}).collect(Collectors.toList());
	}

}

package com.tms.driver.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.driver.been.AvalaibleDriverBean;
import com.tms.driver.been.DriverBean;
import com.tms.driver.entity.DriverEntity;
import com.tms.driver.repository.DriverRepository;
import com.tms.vehicle.entity.VehicleEntity;
import com.tms.vehicle.repository.VehicleRepository;

import io.swagger.annotations.Example;

@Service
public class DriverServiceImpl implements DriverService {

	@Autowired
	private DriverRepository driverRepository;
	@Autowired
	private VehicleRepository vehicleRepository;

	@Override
	public DriverBean addDriver(DriverBean bean) {
		Optional<DriverEntity> existingDriver = driverRepository.findByLicenseNumber(bean.getLicenseNumber());
		if (existingDriver.isPresent()) {
			throw new RuntimeException("Driver with email " + bean.getDriverId() + " already exists.");
		}

		DriverEntity entity = new DriverEntity();
		entity.setDriverId(bean.getDriverId());
		entity.setName(bean.getName());
		entity.setLicenseNumber(bean.getLicenseNumber());
		entity.setContactNumber(bean.getContactNumber());
		entity.setDriverId(bean.getDriverId());

		if (bean.getAssignedVehicleId() != null && bean.getAssignedVehicleId() != null) {
			Optional<VehicleEntity> vehicleOpt = vehicleRepository.findById(bean.getAssignedVehicleId());
			if (vehicleOpt.isPresent()) {
				entity.setAssignedVehicle(vehicleOpt.get());
			} else {
				System.out.println("Vehicle ID not found: ");
			}
		} else {
			System.out.println("AssignedVehicle or VehicleId is null");
		}

		driverRepository.save(entity);
		BeanUtils.copyProperties(entity, bean);
		return bean;
	}

	public DriverBean getDriverById(Long driverId) {
		DriverEntity driver = driverRepository.findById(driverId)
				.orElseThrow(() -> new RuntimeException("Driver not found with ID: " + driverId));

		DriverBean bean = new DriverBean();
		bean.setDriverId(driver.getDriverId());
		bean.setName(driver.getName());
		bean.setLicenseNumber(driver.getLicenseNumber());
		bean.setContactNumber(driver.getContactNumber());
		bean.setDriverId(driver.getDriverId());

		return bean;
	}

	@Override
	public List<DriverBean> getDrivers() {
		return driverRepository.findAll().stream().map(driver -> {
			DriverBean bean = new DriverBean();
			bean.setDriverId(driver.getDriverId());
			bean.setName(driver.getName());
			bean.setLicenseNumber(driver.getLicenseNumber());
			bean.setContactNumber(driver.getContactNumber());
			bean.setDriverId(driver.getDriverId());
			return bean;
		}).collect(Collectors.toList());
	}

	@Override
	public DriverBean updateDriver(Long id, DriverBean bean) {
		DriverEntity driver = driverRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Driver not found with ID: " + id));

		boolean hasLicenseInDb = driver.getLicenseNumber() != null && !driver.getLicenseNumber().trim().isEmpty();
		boolean hasLicenseInRequest = bean.getLicenseNumber() != null && !bean.getLicenseNumber().trim().isEmpty();

		if (!hasLicenseInDb && !hasLicenseInRequest) {
			throw new IllegalArgumentException("License number is required and cannot be empty.");
		}

		if (bean.getName() != null) {
			driver.setName(bean.getName());
		}

		if (bean.getLicenseNumber() != null) {
			driver.setLicenseNumber(bean.getLicenseNumber());
		}

		if (bean.getContactNumber() != null) {
			driver.setContactNumber(bean.getContactNumber());
		}

		if (bean.getDriverId() != null) {
			driver.setDriverId(bean.getDriverId());
		}

//		if (bean.getAssignedVehicleId() != null) {
//			if (bean.getAssignedVehicleId() != null) {
//				VehicleEntity vehicle = vehicleRepository.findById(bean.getAssignedVehicleId());
//				driver.setAssignedVehicle(vehicle);
//			} else {
//				driver.setAssignedVehicle(null);
//			}
//		}

		driverRepository.save(driver);

		DriverBean updatedBean = new DriverBean();
		BeanUtils.copyProperties(driver, updatedBean);
		return updatedBean;
	}

	@Override
	public String deleteDriver(Long driverId) {
		DriverEntity driver = driverRepository.findById(driverId)
				.orElseThrow(() -> new RuntimeException("Driver not found"));
		driverRepository.delete(driver);
		return "Driver Deleted Successfully :";
	}

	@Override
	public List<AvalaibleDriverBean> getAvailableDrivers() {
		return driverRepository.findDriversWithNoVehicle().stream().map(driver -> {
			AvalaibleDriverBean availableDriverBean = new AvalaibleDriverBean();
			availableDriverBean.setDriverId(driver.getDriverId());
			availableDriverBean.setName(driver.getName());
			availableDriverBean.setLicenceNumber(driver.getLicenseNumber());
			availableDriverBean.setContactNumber(driver.getContactNumber());
			return availableDriverBean;
		}).collect(Collectors.toList());
	}
}

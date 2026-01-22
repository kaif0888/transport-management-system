package com.tms.vehicletms.service;

import com.tms.drivertms.repository.DriverRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.location.bean.LocationBean;
import com.tms.location.entity.LocationEntity;
import com.tms.vehicletms.been.VehicleAvalaibleDropdown;
import com.tms.vehicletms.been.VehicleBean;
import com.tms.vehicletms.entity.VehicleEntity;
import com.tms.vehicletms.repository.VehicleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Service
public class VehicleServiceImpl<T> implements VehicleService {

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private DriverRepository driverRepository;

	// Method-1 for create Vehicele
	@Override
	public VehicleBean createVehicle(VehicleBean bean) {
		if (bean.getRegistrationNumber() == null || bean.getRegistrationNumber().isEmpty()) { // Condition 1: Check agar
																								// registration number
																								// zero ho
			throw new RuntimeException("Registration number cannot be empty.");
		}
		if (bean.getCapacity() <= 0) { // Condition 2: Check agar capacity valid hon
			throw new RuntimeException("Capacity must be greater than 0.");
		}
		Optional<VehicleEntity> existingVehicle = vehicleRepository
				.findByRegistrationNumber(bean.getRegistrationNumber());
		if (existingVehicle.isPresent()) {
			throw new RuntimeException(
					"Vehicle with registration number " + bean.getRegistrationNumber() + " already exists.");
		}
		VehicleEntity entity = new VehicleEntity();
		BeanUtils.copyProperties(bean, entity);
		vehicleRepository.save(entity);
		BeanUtils.copyProperties(entity, bean);
		return bean;
	}

	// Method-2 Ye method sabhi vehicles ki list deta hai in form of VehicleBean.
	@Override
	public List<VehicleBean> getAllVehicles() {
		return vehicleRepository.findAll().stream().map(entity -> { // Sabhi entities nikali
			VehicleBean bean = new VehicleBean(); // Har ek entity ke liye bean banaya
			BeanUtils.copyProperties(entity, bean); // Copy kiya
			return bean;
		}).collect(Collectors.toList()); // Beans ki list return ki
	}

	@Override
	public List<VehicleBean> getVehiclesByRegistrationNumber(String registrationNumber) {
		// Get vehicles with registration numbers starting with the provided parameter
		List<VehicleEntity> vehicles = vehicleRepository.findByRegistrationNumberStartingWith(registrationNumber);

		// Convert entities to beans and return
		return vehicles.stream().map(entity -> {
			VehicleBean bean = new VehicleBean();
			BeanUtils.copyProperties(entity, bean);
			return bean;
		}).collect(Collectors.toList());
	}

	// Method-3 Ye method sabhi vehicles ko update krne ke lie.
	@Override
	public VehicleBean updateVehicle(Integer vehicleId, VehicleBean bean) {
		// Validate vehicleId
		if (vehicleId == null) {
			throw new IllegalArgumentException("Vehicle ID must not be null for update.");
		}

		// Find the vehicle by ID
		VehicleEntity entity = vehicleRepository.findById(vehicleId)
				.orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));

		// Check registration number validity only if it's being updated
		if (bean.getRegistrationNumber() != null) {
			if (bean.getRegistrationNumber().isEmpty()) {
				throw new RuntimeException("Registration number cannot be empty");
			}

			// Check if the new registration number already exists (but belongs to a
			// different vehicle)
			Optional<VehicleEntity> existingVehicle = vehicleRepository
					.findByRegistrationNumber(bean.getRegistrationNumber());
			if (existingVehicle.isPresent() && !existingVehicle.get().getVehicleId().equals(vehicleId)) {
				throw new RuntimeException(
						"Vehicle with registration number " + bean.getRegistrationNumber() + " already exists.");
			}
		}

		// Check capacity only if it's being updated
		if (bean.getCapacity() != null && bean.getCapacity() <= 0) {
			throw new RuntimeException("Capacity must be greater than 0.");
		}

		// Update only the fields that are provided in the request (not null)
		if (bean.getRegistrationNumber() != null) {
			entity.setRegistrationNumber(bean.getRegistrationNumber());
		}

		if (bean.getModel() != null) {
			entity.setModel(bean.getModel());
		}

		if (bean.getCapacity() != null) {
			entity.setCapacity(bean.getCapacity());
		}

		if (bean.getStatus() != null) {
			entity.setStatus(bean.getStatus());
		}

		// Add any other fields that might be in your VehicleEntity
		// For example:
		// if (bean.getFuelType() != null) {
		// entity.setFuelType(bean.getFuelType());
		// }

		// Save the updated entity
		vehicleRepository.save(entity);

		// Convert back to bean
		VehicleBean updatedBean = new VehicleBean();
		BeanUtils.copyProperties(entity, updatedBean);

		return updatedBean;
	}

	@Override
	public Map<String, List<VehicleBean>> getVehiclesGroupedByModel() {
		List<VehicleEntity> allVehicles = vehicleRepository.findAll();
		return allVehicles.stream().collect(Collectors.groupingBy(entity -> {
			// Check for null and provide a default value if null
			String model = entity.getModel();
			return (model != null) ? model : "Unknown Model"; // Default value if model is null
		}, Collectors.mapping(entity -> {
			VehicleBean bean = new VehicleBean();
			BeanUtils.copyProperties(entity, bean);
			return bean;
		}, Collectors.toList())));
	}

	// Method-5 Ye method vehicle ko ID ke hisaab se fetch karta hai
	@Override
	public VehicleBean getVehicleById(Integer vehicleId) {
		if (vehicleId == null) {
			throw new IllegalArgumentException("Vehicle ID cannot be null.");
		}

		// Di gayi ID se vehicle entity ko database se fetch karta hai
		VehicleEntity entity = vehicleRepository.findById(vehicleId)
				.orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));

		// Entity se data ko bean mein copy karta hai
		VehicleBean bean = new VehicleBean();
		BeanUtils.copyProperties(entity, bean);

		return bean;
	}

	@Override
	public List<VehicleAvalaibleDropdown> getAvailableVehicles() {
		// Fetch vehicles that are not assigned to any driver
		List<VehicleEntity> unassignedVehicles = vehicleRepository.findVehiclesNotAssignedToAnyDriver();

		// Convert to dropdown bean
		return unassignedVehicles.stream().map(entity -> {
			VehicleAvalaibleDropdown bean = new VehicleAvalaibleDropdown();
			bean.setVehicleId(entity.getVehicleId());
			bean.setRegistrationNumber(entity.getRegistrationNumber());
			bean.setModel(entity.getModel());
			return bean;
		}).collect(Collectors.toList());
	}

	@Override
	public List<VehicleAvalaibleDropdown> getUnrentedVehicles() {
		List<VehicleEntity> unrentedVehicles = vehicleRepository.findVehiclesNotRented();

		return unrentedVehicles.stream().map(vehicle -> {
			VehicleAvalaibleDropdown bean = new VehicleAvalaibleDropdown();
			bean.setVehicleId(vehicle.getVehicleId());
			bean.setRegistrationNumber(vehicle.getRegistrationNumber());
			bean.setModel(vehicle.getModel());
			return bean;
		}).collect(Collectors.toList());
	}

	@Override
	public List<String> getDistinctVehicleModels() {
		List<VehicleEntity> allVehicles = vehicleRepository.findAll();

		// Using TreeSet with custom comparator for case-insensitive comparison
		Set<String> distinctModels = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		// Add all non-null models to the set
		allVehicles.stream().map(VehicleEntity::getModel).filter(model -> model != null && !model.isEmpty())
				.forEach(distinctModels::add);

		// Convert set to list
		return distinctModels.stream().collect(Collectors.toList());
	}

	@Override
	public List<VehicleBean> getFilteredVehicles(String registrationNumber, String model, String company) {
		List<VehicleEntity> vehicles = vehicleRepository.findWithFilters(registrationNumber, model, company);

		// Convert entities to beans and return
		return vehicles.stream().map(entity -> {
			VehicleBean bean = new VehicleBean();
			BeanUtils.copyProperties(entity, bean);
			return bean;
		}).collect(Collectors.toList());
	}

	@Autowired
	private FilterCriteriaService<VehicleEntity> filterCriteriaService;

	@Override
	public List<VehicleBean> getvehiclebyfilterCriteria(List<FilterCriteriaBean> filters, int limit) {
		try {
			List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(VehicleEntity.class, filters, limit);
			return (List<VehicleBean>) filteredEntities.stream().map(entity -> convertToBean((VehicleEntity) entity))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Error filtering Vehicle: " + e.getMessage());
		}
	}

	private VehicleBean convertToBean(VehicleEntity vehicleEntity) {
		VehicleBean vehicleBean = new VehicleBean();
//		vehicleBean.setLicenseNumber(vehicleEntity.getLicenseNumber());
//		vehicleBean.setVehicleId(vehicleEntity.getVehicleId());
//		vehicleBean.setRegistrationNumber(vehicleEntity.getRegistrationNumber());
//		vehicleBean.setModel(vehicleEntity.getModel());
//		vehicleBean.setCompanyName(vehicleEntity.getCompanyName());
//		vehicleBean.setVehicleOwnerName(vehicleEntity.getVehicleOwnerName());
		BeanUtils.copyProperties(vehicleEntity, vehicleBean);
		
		return vehicleBean;
	}

	@Override
	public List<String> getDistinctVehicleCompany() {

		List<VehicleEntity> allVehicle = vehicleRepository.findAll();
		Set<String> distinctCompany = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		allVehicle.stream().map(VehicleEntity::getCompanyName).filter(company -> company != null && !company.isEmpty())
				.forEach(distinctCompany::add);
		return distinctCompany.stream().collect(Collectors.toList());
	}

}

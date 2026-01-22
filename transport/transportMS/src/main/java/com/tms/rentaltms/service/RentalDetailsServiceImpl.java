package com.tms.rentaltms.service;

//RentalDetailsServiceImpl.java

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.order.bean.OrderBean;
import com.tms.order.entity.OrderEntity;
import com.tms.rentaltms.bean.RentalDetailsBean;
import com.tms.rentaltms.entity.RentalDetailsEntity;
import com.tms.rentaltms.repository.RentalDetailsRepository;
import com.tms.vehicletms.been.VehicleBean;
import com.tms.vehicletms.entity.VehicleEntity;
import com.tms.vehicletms.repository.VehicleRepository;

@Service
public class RentalDetailsServiceImpl implements RentalDetailsService {

	@Autowired
	private RentalDetailsRepository rentalRepository; // Spring ka Autowiring use karke hum automatically
														// RentalDetailsRepository ka object inject karwa rahe hain.
	@Autowired
	private VehicleRepository vehicleRepository;

	// Add new driver to system.
	@Override
	public RentalDetailsBean addRental(RentalDetailsBean bean) {
		RentalDetailsEntity entity = new RentalDetailsEntity();
		BeanUtils.copyProperties(bean, entity);
		if (bean.getVehicle() != null && bean.getVehicle().getVehicleId() != null) { // Agar rental ke saath koi vehicle
																						// bhi diya gaya hai, to us
																						// vehicle ko database se
																						// nikalte hain.
			vehicleRepository.findById(bean.getVehicle().getVehicleId()) // Agar vehicle mil gaya, to usko entity ke
																			// andar assign kar diya.
					.ifPresent(entity::setVehicle);
		}
		rentalRepository.save(entity);
		BeanUtils.copyProperties(entity, bean);
		return bean;
	}

	// List all drivers in the system.
	@Override
	public List<RentalDetailsBean> listRentals() { // Ye method database se sabhi rental records nikal kar unhe
													// RentalDetailsBean me convert karta hai
		return rentalRepository.findAll().stream().map(entity -> { // rentalRepository.findAll() → database se sabhi
																	// rental entities nikalta hai.
			RentalDetailsBean bean = new RentalDetailsBean();
			BeanUtils.copyProperties(entity, bean);
			bean.setVehicle(entity.getVehicle()); // Entity ke saath jo vehicle linked hai, usko bhi manually bean me
													// set kiya.
			return bean;
		}).collect(Collectors.toList()); // sabhi beans ko ek list me collect kar diya aur return kar diya.
	}

	// Update rental record details.
	@Override
	public RentalDetailsBean updateRental(Integer id, RentalDetailsBean bean) {
		RentalDetailsEntity entity = rentalRepository.findById(id).orElseThrow();
		entity.setProviderName(bean.getProviderName());
		entity.setRentalStartDate(bean.getRentalStartDate());
		entity.setRentalEndDate(bean.getRentalEndDate());
		entity.setRentalCost(bean.getRentalCost());
		rentalRepository.save(entity);
		BeanUtils.copyProperties(entity, bean);
		return bean;
	}

	// Track the status of rented vehicles.
	@Override
	public String trackRentalStatus(Integer id) {
		return rentalRepository.findById(id).isPresent() ? "Rental active or record found" : "Rental record not found";
	}

	// Delete rental by ID
	@Override
	public String deleteRental(Integer id) {
		Optional<RentalDetailsEntity> optionalEntity = rentalRepository.findById(id); //// Pehle check karte hain ki
																						//// diya gaya rental ID
																						//// database me exist karta hai
																						//// ya nahi.
		if (optionalEntity.isPresent()) {
			rentalRepository.deleteById(id);
			return "Rental deleted successfully";
		} else {
			return "Rental not found";
		}
	}

	// Method-5 Ye method vehicle ko ID ke hisaab se fetch karta hai
	@Override
	public RentalDetailsBean getRentalById(Integer rentalDetailsId) {
		if (rentalDetailsId == null) {
			throw new IllegalArgumentException("Rental ID cannot be null.");
		}

		// Di gayi ID se vehicle entity ko database se fetch karta hai
		RentalDetailsEntity entity = rentalRepository.findById(rentalDetailsId)
				.orElseThrow(() -> new RuntimeException("Rental not found with ID: " + rentalDetailsId));

		// Entity se data ko bean mein copy karta hai
		RentalDetailsBean bean = new RentalDetailsBean();
		BeanUtils.copyProperties(entity, bean);

		return bean;
	}

	@Autowired
	private FilterCriteriaService<RentalDetailsEntity> filterCriteriaService;

	@Override
	public List<RentalDetailsBean> listOfRentalDetailByFilter(List<FilterCriteriaBean> filters, int limit) {
		try {
			List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(RentalDetailsEntity.class, filters,
					limit);
			return (List<RentalDetailsBean>) filteredEntities.stream()
					.map(entity -> convertToBean((RentalDetailsEntity) entity)).collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Error filtering Driver: " + e.getMessage());
		}
	}

	private RentalDetailsBean convertToBean(RentalDetailsEntity rentalDetailsEntity) {
		RentalDetailsBean rentalDetailsBean = new RentalDetailsBean();
		rentalDetailsBean.setRentalDetailsId(rentalDetailsEntity.getRentalDetailsId());
		rentalDetailsBean.setProviderName(rentalDetailsEntity.getProviderName());
		rentalDetailsBean.setRentalCost(rentalDetailsEntity.getRentalCost());
		rentalDetailsBean.setRentalStartDate(rentalDetailsEntity.getRentalStartDate());
		rentalDetailsBean.setRentalEndDate(rentalDetailsEntity.getRentalEndDate());	
		rentalDetailsBean.setVehicle(rentalDetailsEntity.getVehicle());
		return rentalDetailsBean;
	}

}

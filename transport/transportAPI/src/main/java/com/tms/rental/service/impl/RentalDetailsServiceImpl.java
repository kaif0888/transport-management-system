package com.tms.rental.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.rental.bean.RentalDetailsBean;
import com.tms.rental.entity.RentalDetailsEntity;
import com.tms.rental.repository.RentalDetailsRepository;
import com.tms.rental.service.RentalDetailsService;
import com.tms.vehicle.entity.VehicleEntity;
import com.tms.vehicle.repository.VehicleRepository;

@Service
public class RentalDetailsServiceImpl implements RentalDetailsService {

    @Autowired
    private RentalDetailsRepository rentalRepository;

    @Autowired
    private VehicleRepository vehicleRepository;
    
	@Autowired
	UserRepository  userRepository;

    private String generateUniqueRentalDetailsId() {
        String prefix = "VL-RENT-";
        String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";

        List<RentalDetailsEntity> rentals = rentalRepository.findByRentalDetailsIdStartingWith(fullPrefix);

        int maxSeq = rentals.stream()
            .map(r -> r.getRentalDetailsId().substring(fullPrefix.length()))
            .mapToInt(seq -> {
                try {
                    return Integer.parseInt(seq);
                } catch (NumberFormatException e) {
                    return 0;
                }
            })
            .max()
            .orElse(0);

        return fullPrefix + String.format("%03d", maxSeq + 1);
    }


    // Add new rental to system
    @Override
    public RentalDetailsBean addRental(RentalDetailsBean bean) {
        RentalDetailsEntity entity = new RentalDetailsEntity();
        BeanUtils.copyProperties(bean, entity);

        // Set the generated unique rentalDetailsId
        entity.setRentalDetailsId(generateUniqueRentalDetailsId());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    entity.setCreatedBy(authentication.getName());
	    User currentUser = userRepository.findByEmail(authentication.getName())
	            .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
	    entity.setBranchIds(currentUser.getBranchIds());

        // Assign vehicle if vehicleId is present
        if (bean.getVehicleId() != null) {
            VehicleEntity vehicle = vehicleRepository.findById(bean.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + bean.getVehicleId()));
            entity.setVehicle(vehicle);
        }

        rentalRepository.save(entity);

        BeanUtils.copyProperties(entity, bean);
        if (entity.getVehicle() != null) {
            bean.setVehicleId(entity.getVehicle().getVehicleId());
        }

        return bean;
    }


    // Get list of all rentals
    @Override
    public List<RentalDetailsBean> listRentals() {
        return rentalRepository.findAll().stream().map(entity -> {
            RentalDetailsBean bean = new RentalDetailsBean();
            BeanUtils.copyProperties(entity, bean);
            if (entity.getVehicle() != null) {
                bean.setVehicleId(entity.getVehicle().getVehicleId());
            }
            return bean;
        }).collect(Collectors.toList());
    }

    // Update rental record details
    @Override
    public RentalDetailsBean updateRental(String id, RentalDetailsBean bean) {
        RentalDetailsEntity entity = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found with ID: " + id));

        entity.setProviderName(bean.getProviderName());
        entity.setRentalStartDate(bean.getRentalStartDate());
        entity.setRentalEndDate(bean.getRentalEndDate());
        entity.setRentalCost(bean.getRentalCost());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    entity.setLastModifiedBy(authentication.getName());

        rentalRepository.save(entity);
        BeanUtils.copyProperties(entity, bean);
        if (entity.getVehicle() != null) {
            bean.setVehicleId(entity.getVehicle().getVehicleId());
        }
        return bean;
    }

    // Track the status of a rental
    @Override
    public String trackRentalStatus(String id) {
        return rentalRepository.existsById(id)
                ? "Rental active or record found"
                : "Rental record not found";
    }

    // Delete rental by ID
    @Override
    public String deleteRental(String id) {
        if (rentalRepository.existsById(id)) {
            rentalRepository.deleteById(id);
            return "Rental deleted successfully";
        } else {
            return "Rental not found";
        }
    }

    // Get rental by ID
    @Override
    public RentalDetailsBean getRentalById(String rentalDetailsId) {
        RentalDetailsEntity entity = rentalRepository.findById(rentalDetailsId)
                .orElseThrow(() -> new RuntimeException("Rental not found with ID: " + rentalDetailsId));

        RentalDetailsBean bean = new RentalDetailsBean();
        BeanUtils.copyProperties(entity, bean);

        if (entity.getVehicle() != null) {
            bean.setVehicleId(entity.getVehicle().getVehicleId());
        }

        return bean;
    }
    
    @Autowired
	private FilterCriteriaService<RentalDetailsEntity> filterCriteriaService;

	@Override
	public List<RentalDetailsBean> listOfRentalDetailByFilter(List<FilterCriteriaBean> filters, int limit) {
		try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
        	if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().name())) {
        	    // Remove any pre-existing branch filter (if present)
        	    filters.removeIf(f -> f.getAttribute().equalsIgnoreCase("branchIds"));

        	    // Convert comma-separated branchIds string to comma-separated string for value
        	    String branchIds = currentUser.getBranchIds(); // e.g., "BR001,BR002"

        	    FilterCriteriaBean branchFilter = new FilterCriteriaBean();
        	    branchFilter.setAttribute("branchIds");
        	    branchFilter.setOperation(FilterOperation.AMONG);
        	    branchFilter.setValue(branchIds);  // Still a comma-separated string
        	    branchFilter.setValueType(String.class); // Optional

        	    filters.add(branchFilter);
        	}
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
//		rentalDetailsBean.setVehicle(rentalDetailsEntity.getVehicle());
		return rentalDetailsBean;
	}
}

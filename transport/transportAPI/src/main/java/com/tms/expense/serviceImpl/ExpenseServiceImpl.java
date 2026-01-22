package com.tms.expense.serviceImpl;

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
import com.tms.expense.bean.ExpenseBean;
import com.tms.expense.entity.ExpenseEntity;
import com.tms.expense.repository.ExpenseRepository;
import com.tms.expense.service.ExpenseService;
import com.tms.expenseType.entity.ExpenseTypeEntity;
import com.tms.expenseType.repository.ExpenseTypeRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.product.bean.ProductBean;
import com.tms.vehicle.entity.VehicleEntity;
import com.tms.vehicle.repository.VehicleRepository;

@Service
public class ExpenseServiceImpl implements ExpenseService {

	@Autowired
	private ExpenseRepository expenseRepository;

	@Autowired
	private VehicleRepository vehicleRepository;
	
	@Autowired
	private ExpenseTypeRepository expenseTypeRepo;
	
	@Autowired
	UserRepository  userRepository;

	private String generateExpenseId() {
		String prefix = "EXP-";
		String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
		String fullPrefix = prefix + dateStr + "-";

		List<ExpenseEntity> todayExpenses = expenseRepository.findByExpenseIdStartingWith(fullPrefix);

		int maxSeq = todayExpenses.stream().map(e -> e.getExpenseId().substring(fullPrefix.length())).mapToInt(seq -> {
			try {
				return Integer.parseInt(seq);
			} catch (NumberFormatException e) {
				return 0;
			}
		}).max().orElse(0);

		int nextSeq = maxSeq + 1;
		String formattedSeq = String.format("%03d", nextSeq);

		return fullPrefix + formattedSeq;
	}

	@Override
	public ExpenseBean addExpense(ExpenseBean dto) {
		ExpenseEntity entity = new ExpenseEntity();
		BeanUtils.copyProperties(dto, entity);

		// Generate unique ID
		entity.setExpenseId(generateExpenseId());

		// Set vehicle reference
		VehicleEntity vehicle = vehicleRepository.findById(dto.getVehicleId())
				.orElseThrow(() -> new RuntimeException("Vehicle not found"));
		entity.setVehicle(vehicle);
		
		//set ExpenseType reference
		ExpenseTypeEntity expenseType = expenseTypeRepo.findById(dto.getExpenseTypeId())
				.orElseThrow(() -> new RuntimeException("ExpenseType not found"));
		entity.setExpenseType(expenseType);
//	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//	    entity.setCreatedBy(authentication.getName());
//	 // Get branchId from current authenticated user
//	    User currentUser = userRepository.findByEmail(authentication.getName())
//	            .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
//	    entity.setBranchIds(currentUser.getBranchIds());

		// Save and map response
		ExpenseEntity savedEntity = expenseRepository.save(entity);

		ExpenseBean response = new ExpenseBean();
		BeanUtils.copyProperties(savedEntity, response);
		response.setVehicleId(vehicle.getVehicleId());
		response.setExpenseTypeId(expenseType.getExpenseTypeId());   

		return response;
	}

	@Override
	public List<ExpenseBean> listAllExpenses() {
		return expenseRepository.findAll().stream().map(entity -> {
			ExpenseBean dto = new ExpenseBean();
			BeanUtils.copyProperties(entity, dto);
			
			   if (entity.getVehicle() != null) {
		            dto.setVehicleId(entity.getVehicle().getVehicleId());
		        }

		        if (entity.getExpenseType() != null) {
		            dto.setExpenseTypeId(entity.getExpenseType().getExpenseTypeId());
		        }
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public ExpenseBean updateExpense(String expenseId, ExpenseBean dto) {
		ExpenseEntity entity = expenseRepository.findById(expenseId)
				.orElseThrow(() -> new RuntimeException("Expense not found with ID: " + expenseId));
		
		


		BeanUtils.copyProperties(dto, entity);
		
		// Set vehicle reference
				VehicleEntity vehicle = vehicleRepository.findById(dto.getVehicleId())
						.orElseThrow(() -> new RuntimeException("Vehicle not found"));
				entity.setVehicle(vehicle);
				
				//set ExpenseType reference
				ExpenseTypeEntity expenseType = expenseTypeRepo.findById(dto.getExpenseTypeId())
						.orElseThrow(() -> new RuntimeException("ExpenseType not found"));
				entity.setExpenseType(expenseType);
				
		ExpenseEntity updatedEntity = expenseRepository.save(entity);

		ExpenseBean response = new ExpenseBean();
		BeanUtils.copyProperties(updatedEntity, response);
		
		response.setVehicleId(vehicle.getVehicleId());
		response.setExpenseTypeId(expenseType.getExpenseTypeId());  
		return response;
	}

	@Override
	public void deleteExpense(String expenseId) {
		if (!expenseRepository.existsById(expenseId)) {
			throw new RuntimeException("Expense not found with ID: " + expenseId);
		}
		expenseRepository.deleteById(expenseId);
	}

	@Autowired
	private FilterCriteriaService<ExpenseEntity> filterCriteriaService;

	@Override
	public List<ExpenseBean> getExpensebyfilterCriteria(List<FilterCriteriaBean> filters, int limit) {
		  try {
//	            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//	            String username = authentication.getName();
//
//	            User currentUser = userRepository.findByEmail(username)
//	                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
//	        	if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().name())) {
//	        	    // Remove any pre-existing branch filter (if present)
//	        	    filters.removeIf(f -> f.getAttribute().equalsIgnoreCase("branchIds"));
//
//	        	    // Convert comma-separated branchIds string to comma-separated string for value
//	        	    String branchIds = currentUser.getBranchIds(); // e.g., "BR001,BR002"
//
//	        	    FilterCriteriaBean branchFilter = new FilterCriteriaBean();
//	        	    branchFilter.setAttribute("branchIds");
//	        	    branchFilter.setOperation(FilterOperation.AMONG);
//	        	    branchFilter.setValue(branchIds);  // Still a comma-separated string
//	        	    branchFilter.setValueType(String.class); // Optional
//
//	        	    filters.add(branchFilter);
//	        	}
	        	
		        List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(ExpenseEntity.class, filters, limit);
		        
		        return filteredEntities.stream()
		            .filter(entity -> entity instanceof ExpenseEntity) // Ensure proper type
		            .map(entity -> convertToBean((ExpenseEntity) entity))
		            .collect(Collectors.toList());

		    } catch (Exception e) {
		        throw new RuntimeException("Error filtering Expense: " + e.getMessage(), e); // Improved exception message
		    }
	}

	private ExpenseBean convertToBean(ExpenseEntity expenseEntity) {
	    ExpenseBean expenseBean = new ExpenseBean();

	    expenseBean.setExpenseId(expenseEntity.getExpenseId());
	    expenseBean.setDescription(expenseEntity.getDescription());
	    expenseBean.setAmount(expenseEntity.getAmount());
	    expenseBean.setCreatedBy(expenseEntity.getCreatedBy());
	    expenseBean.setCreatedDate(expenseEntity.getCreatedDate());
	    expenseBean.setLastModifiedBy(expenseEntity.getLastModifiedBy());
	    expenseBean.setLastModifiedDate(expenseEntity.getLastModifiedDate());
	    expenseBean.setDate(expenseEntity.getDate());

	    // Handle possible nulls to avoid NullPointerExceptions
	    if (expenseEntity.getExpenseType() != null) {
	        expenseBean.setExpenseTypeId(expenseEntity.getExpenseType().getExpenseTypeId());
	        expenseBean.setExpenseTypeName(expenseEntity.getExpenseType().getExpenseTypeName());
	    }

	    if (expenseEntity.getVehicle() != null) {
	        expenseBean.setVehicleId(expenseEntity.getVehicle().getVehicleId());
	        expenseBean.setVehiclNumber(expenseEntity.getVehicle().getVehiclNumber());
	    }
	    
	    return expenseBean;

	}

	@Override
	public ExpenseBean getExpenseById(String id) {
	    ExpenseEntity entity = expenseRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + id));

	    return convertToBean(entity);
	}




}

package com.tms.employee.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
//import java.util.Collection;
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
import com.tms.employee.bean.EmployeeBean;
import com.tms.employee.entity.EmployeeEntity;
import com.tms.employee.repository.EmployeeRepository;
import com.tms.employee.service.EmployeeService;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	UserRepository  userRepository;

	private String generateEmployeeId() {
		String prefix = "EMP-";
		String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
		String fullPrefix = prefix + dateStr + "-";

		List<EmployeeEntity> todayEmployees = employeeRepository.findByEmployeeIdStartingWith(fullPrefix);

		int maxSeq = todayEmployees.stream().map(e -> e.getEmployeeId().substring(fullPrefix.length()))
				.mapToInt(seq -> {
					try {
						return Integer.parseInt(seq);
					} catch (NumberFormatException ex) {
						return 0;
					}
				}).max().orElse(0);

		int nextSeq = maxSeq + 1;
		String formattedSeq = String.format("%03d", nextSeq);

		return fullPrefix + formattedSeq;
	}

	@Override
	public EmployeeBean createEmployee(EmployeeBean employee) {
		EmployeeEntity employeeEntity = new EmployeeEntity();
		BeanUtils.copyProperties(employee, employeeEntity);

		// Set generated unique ID
		employeeEntity.setEmployeeId(generateEmployeeId());
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    employeeEntity.setCreatedBy(authentication.getName());
	 // Get branchId from current authenticated user
	    User currentUser = userRepository.findByEmail(authentication.getName())
	            .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
	    employeeEntity.setBranchIds(currentUser.getBranchIds());
		// Set timestamps
		employeeEntity.setLastModifiedDate(LocalDateTime.now());

		EmployeeEntity savedEntity = employeeRepository.save(employeeEntity);

		EmployeeBean saveBean = new EmployeeBean();
		BeanUtils.copyProperties(savedEntity, saveBean);
		return saveBean;
	}

	@Override
	public List<EmployeeBean> getListOfAllEmployees() {
		List<EmployeeEntity> employeeEntities = employeeRepository.findAll();
		List<EmployeeBean> employeeBean = new ArrayList<>();
		for (EmployeeEntity employeEntity : employeeEntities) {
			EmployeeBean bean = new EmployeeBean();
			BeanUtils.copyProperties(employeEntity, bean);
			employeeBean.add(bean);
			System.out.println("Employee Bean  " + employeeBean);
		}
		return employeeBean;
	}

	@Override
	public EmployeeBean getEmployeeById(String employeeId) {
		Optional<EmployeeEntity> optional = employeeRepository.findById(employeeId);
		EmployeeBean employeeBean = null;
		if (optional.isPresent()) {
			EmployeeEntity employeeEntity = optional.get();
			employeeBean = new EmployeeBean();
			BeanUtils.copyProperties(employeeEntity, employeeBean);
		}
		return employeeBean;
	}

	@Override
	public EmployeeBean updateEmployee(EmployeeBean employee) {
		Optional<EmployeeEntity> optional = employeeRepository.findById(employee.getEmployeeId());
		if (optional.isPresent()) {
			EmployeeEntity employeeEntity = optional.get();
			employeeEntity.setEmployeeName(employee.getEmployeeName());
			employeeEntity.setEmployeeEmail(employee.getEmployeeEmail());
			employeeEntity.setEmployeeDepartment(employee.getEmployeeDepartment());
			// employeeEntity.setCreatedDate(LocalDate.now());
			employeeEntity.setLastModifiedDate(LocalDateTime.now());
			EmployeeEntity saveEntity = employeeRepository.save(employeeEntity);
			EmployeeBean saveBean = new EmployeeBean();
			BeanUtils.copyProperties(saveEntity, saveBean);
			return saveBean;
		}
		return null;

	}

	@Override
	public String deleteEmployeeById(String employeeId) {
		if (employeeId != null) {
			Optional<EmployeeEntity> optional = employeeRepository.findById(employeeId);
			if (!optional.isEmpty()) {
				EmployeeEntity employeeEntity = optional.get();
				employeeRepository.deleteById(employeeEntity.getEmployeeId());
				return "The Employee Has been Deleted";

			}
		}
		return null;
	}

	@Override
	public List<EmployeeBean> getEmployeeAccordingToUserInput(String employeeId) {
		List<EmployeeBean> listOfEmployeeBean = new ArrayList<>();
		EmployeeBean employeeBean = null;

		if (employeeId != null) {
			Optional<EmployeeEntity> opt = employeeRepository.findById(employeeId);
			if (opt.isPresent()) {
				EmployeeEntity employeeEntity = opt.get();
				employeeBean = new EmployeeBean();
				BeanUtils.copyProperties(employeeEntity, employeeBean);
				listOfEmployeeBean.add(employeeBean);
				return listOfEmployeeBean;
			}
		} else {
			List<EmployeeEntity> employeeEntities = employeeRepository.findAll();
			for (EmployeeEntity employeEntity : employeeEntities) {
				employeeBean = new EmployeeBean();
				BeanUtils.copyProperties(employeEntity, employeeBean);
				listOfEmployeeBean.add(employeeBean);
			}
		}

		return listOfEmployeeBean;
	}

	@Autowired
	private FilterCriteriaService<EmployeeEntity> filterCriteriaService;

	@Override
	public List<EmployeeBean> getEmployeebyfilterCriteria(List<FilterCriteriaBean> filters, int limit) {
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
			List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(EmployeeEntity.class, filters,
					limit);
			return (List<EmployeeBean>) filteredEntities.stream().map(entity -> convertToBean((EmployeeEntity) entity))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Error filtering Driver: " + e.getMessage());
		}

	}

	private EmployeeBean convertToBean(EmployeeEntity employeeEntity) {
		EmployeeBean employeeBean = new EmployeeBean();
		employeeBean.setEmployeeId(employeeEntity.getEmployeeId());
		employeeBean.setEmployeeName(employeeEntity.getEmployeeName());
		employeeBean.setEmployeeEmail(employeeEntity.getEmployeeEmail());
		employeeBean.setEmployeeDepartment(employeeEntity.getEmployeeDepartment());
		employeeBean.setCreatedBy(employeeEntity.getCreatedBy());
		employeeBean.setCreatedDate(employeeEntity.getCreatedDate());
		employeeBean.setLastModifiedBy(employeeEntity.getLastModifiedBy());
		employeeBean.setLastModifiedDate(employeeEntity.getLastModifiedDate());
		
		return employeeBean;
	}

}

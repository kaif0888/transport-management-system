package com.tms.employee.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
//import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.employee.bean.EmployeeBean;
import com.tms.employee.entity.EmployeeEntity;
import com.tms.employee.repository.EmployeeRepository;
import com.tms.employee.service.EmployeeService;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.order.bean.OrderBean;
import com.tms.order.entity.OrderEntity;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Override
	public EmployeeBean createEmployee(EmployeeBean employee) {

		EmployeeEntity employeeEntity = new EmployeeEntity();
		BeanUtils.copyProperties(employee, employeeEntity);
		employeeEntity.setCreatedDate(LocalDate.now());
		employeeEntity.setLastModifiedDate(LocalDate.now());
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
			BeanUtils.copyProperties(employeEntity, bean );
			employeeBean.add(bean);
           System.out.println("Employee Bean  " +employeeBean);
		}
		return employeeBean;
	}

	@Override
	public EmployeeBean getEmployeeById(Long employeeId) {
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
			//employeeEntity.setCreatedDate(LocalDate.now());
			employeeEntity.setLastModifiedDate(LocalDate.now());
			EmployeeEntity saveEntity = employeeRepository.save(employeeEntity);
			EmployeeBean saveBean = new EmployeeBean();
			BeanUtils.copyProperties(saveEntity, saveBean);
			return saveBean;
		}
		return null;

	}

	@Override
	public String deleteEmployeeById(Long employeeId) {
		if (employeeId != null && employeeId != 0) {
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
	public List<EmployeeBean> getEmployeeAccordingToUserInput(Long employeeId) {
		List<EmployeeBean> listOfEmployeeBean = new ArrayList<>();
		EmployeeBean employeeBean = null;

		if (employeeId != null && employeeId != 0) {
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
		// TODO Auto-generated method stub
		try {
			List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(EmployeeEntity.class, filters, limit);
			return (List<EmployeeBean>) filteredEntities.stream().map(entity -> convertToBean((EmployeeEntity) entity))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Error filtering Driver: " + e.getMessage());
		}
		
	}
	
	

	private EmployeeBean convertToBean (EmployeeEntity employeeEntity) {
		EmployeeBean employeeBean = new EmployeeBean();
		employeeBean.setEmployeeId(employeeEntity.getEmployeeId());
		employeeBean.setEmployeeName(employeeEntity.getEmployeeName());
		employeeBean.setEmployeeEmail(employeeEntity.getEmployeeEmail());
		employeeBean.setEmployeeDepartment(employeeEntity.getEmployeeDepartment());
		return employeeBean;
	}

}

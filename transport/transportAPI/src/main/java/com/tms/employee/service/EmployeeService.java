package com.tms.employee.service;

import java.util.List;

import com.tms.employee.bean.EmployeeBean;
import com.tms.filter.criteria.bean.FilterCriteriaBean;

public interface EmployeeService {

	EmployeeBean createEmployee(EmployeeBean employee);

	List<EmployeeBean> getListOfAllEmployees();

	EmployeeBean getEmployeeById(String employeeId);

	EmployeeBean updateEmployee(EmployeeBean employee);

	String deleteEmployeeById(String employeeId);

	public List<EmployeeBean> getEmployeeAccordingToUserInput(String employeeId);

	List<EmployeeBean> getEmployeebyfilterCriteria(List<FilterCriteriaBean> filters, int limit);

}

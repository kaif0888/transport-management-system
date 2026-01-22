package com.tms.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tms.employee.bean.EmployeeBean;
import com.tms.employee.service.EmployeeService;
import com.tms.employee.service.impl.EmployeeServiceImpl;
import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.order.bean.OrderBean;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@GetMapping("/msg")
	public String name() {
		return "Hello....";
	}

	@PostMapping(value = "/createEmployee")
	public EmployeeBean createUser(@RequestBody EmployeeBean employee) {
		return employeeService.createEmployee(employee);
	}

	@GetMapping(value = "/getListOfEmployees")
	public List<EmployeeBean> getAllUser() {
		return employeeService.getListOfAllEmployees();
	}

	@GetMapping(value = "/getEmployeeById/{employeeId}")
	public EmployeeBean getById(@PathVariable Long employeeId) {
		return employeeService.getEmployeeById(employeeId);
	}

	@PutMapping(value = "/updateEmployee")
	public EmployeeBean updateUser(@RequestBody EmployeeBean employeeBean) {
		return employeeService.updateEmployee(employeeBean);
	}

	@DeleteMapping(value = "/deleteEmployee/{employeeId}")
	public String deleteUser(@PathVariable Long employeeId) {
		return employeeService.deleteEmployeeById(employeeId);
	}

	@GetMapping(value = "/getEmployeeAccordingToUserInput")
	public List<EmployeeBean> getEmployeeAccordingToUserInput(@RequestParam(required = false) Long employeeId) {

		return employeeService.getEmployeeAccordingToUserInput(employeeId);
	}
	
	@PostMapping("/listOfOrderByFilter")
	public ResponseEntity<List<EmployeeBean>> getEmployeebyfilterCriteria(@RequestBody FilterRequest request) {
		try {
			int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
			List<EmployeeBean> getEmployeebyfilterCriteria = employeeService.getEmployeebyfilterCriteria(request.getFilters(),
					limit);
			return ResponseEntity.ok(getEmployeebyfilterCriteria);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}

}

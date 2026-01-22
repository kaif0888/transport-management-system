package com.tms.customer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.customer.bean.CustomerBean;
import com.tms.customer.service.CustomerService;
import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.product.bean.ProductBean;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/customer")
@Api(value = "Customer Management System", tags = "Customer Management")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

    @PostMapping("/filterCustomers")
    public ResponseEntity<List<CustomerBean>> filterCustomers(@RequestBody FilterRequest request) {
        try {
            int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
            List<CustomerBean> filterCustomers = customerService.filterCustomers(request.getFilters(), limit);
            return ResponseEntity.ok(filterCustomers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
 
	
	@ApiOperation(value = "Create a new customer", response = CustomerBean.class, notes = "This API is used to create a new customer by providing necessary customer details.")
	@PostMapping("/createCustomer")
	public CustomerBean createCustomer(@RequestBody CustomerBean customerBean) {
		return customerService.createCustomer(customerBean);
	}

	@ApiOperation(value = "Get customer by ID", response = CustomerBean.class, notes = "This API retrieves a customer by their unique ID and returns all associated details.")
	@GetMapping("/getCustomerById/{customerId}")
	public ResponseEntity<CustomerBean> getCustomerById(@PathVariable Long customerId) {
		try {
			CustomerBean customerBean = customerService.getCustomerById(customerId);
			return new ResponseEntity<>(customerBean, HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "Update customer by customerId", response = CustomerBean.class, notes = "Update an existing customer by customerId.")
	@PutMapping("/updateByCustomerId/{id}")
	public ResponseEntity<CustomerBean> updateByCustomerId(@PathVariable("id") Long id,
			@RequestBody CustomerBean customerBean) {
		try {
			customerBean.setCustomerId(id); // assuming there's a setCustomerId method
			CustomerBean updatedCustomer = customerService.updateCustomer(customerBean);
			return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "Delete a customer by ID", response = Void.class, notes = "This API deletes a customer from the system based on their unique customer ID.")
	@DeleteMapping("/deleteCustomer/{customerId}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable("customerId") Long customerId) {
		try {
			customerService.deleteCustomer(customerId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}

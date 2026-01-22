package com.tms.expenseType.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.expense.bean.ExpenseBean;
import com.tms.expenseType.bean.ExpenseTypeBean;
import com.tms.expenseType.service.ExpenseTypeService;
import com.tms.expenseType.serviceImpl.ExpenseTypeServiceImpl;
import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.productcategory.bean.ProductCategoryBean;



@RestController
@RequestMapping("/expenseType")
public class ExpenseTypeController {
     
	@Autowired
	private ExpenseTypeServiceImpl expenseTypeServiceImpl;
	
	@Autowired
	ExpenseTypeService expenseTypeService;
	
	
	@PostMapping("/createExpenseType")
	public ExpenseTypeBean createExpenseType(@RequestBody ExpenseTypeBean expenseTypeBean)
	{
 	  return expenseTypeService.createExpenseType(expenseTypeBean);
	}
	
	 @GetMapping("/getExpenseTypeById/{id}")
	    public ExpenseTypeBean getExpenseTypeById(@PathVariable String id) {
	        return expenseTypeService.getExpenseTypeById(id);
	    }
	
	@GetMapping("/listAllExpenseType")
	public List<ExpenseTypeBean> listAllExpenseType()
	{
		return expenseTypeServiceImpl.listAllExpenseType();
	}
	
	@DeleteMapping("/deleteById/{expenseTypeId}")
	public String deleteExpenseById(@PathVariable String expenseTypeId)
	{
		return expenseTypeServiceImpl.deleteExpenseType(expenseTypeId);
	}
	
	@PostMapping("/getExpenseTypebyfilterCriteria")
	public ResponseEntity<List<ExpenseTypeBean>> getExpenseTypebyfilterCriteria(@RequestBody FilterRequest request) {
		try {
			int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
			List<ExpenseTypeBean> getExpenseTypebyfilterCriteria = expenseTypeService.getExpenseTypebyfilterCriteria(request.getFilters(), limit);
			return ResponseEntity.ok(getExpenseTypebyfilterCriteria);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}
	
}

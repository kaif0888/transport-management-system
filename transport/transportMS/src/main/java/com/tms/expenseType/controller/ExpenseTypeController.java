package com.tms.expenseType.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.expenseType.bean.ExpenseTypeBean;
import com.tms.expenseType.serviceImpl.ExpenseTypeServiceImpl;



@RestController
@RequestMapping("/expenseType")
public class ExpenseTypeController {
     
	@Autowired
	private ExpenseTypeServiceImpl expenseTypeServiceImpl;
	
	@PostMapping("/createExpenseType")
	public ExpenseTypeBean createExpenseType(@RequestBody ExpenseTypeBean expenseTypeBean)
	{
 	  return expenseTypeServiceImpl.createExpenseType(expenseTypeBean);
	}
	
	@GetMapping("/listAllExpenseType")
	public List<ExpenseTypeBean> listAllExpenseType()
	{
		return expenseTypeServiceImpl.listAllExpenseType();
	}
	
	@DeleteMapping("/deleteById/{expenseTypeId}")
	public String deleteExpenseById(@PathVariable Long expenseTypeId)
	{
		return expenseTypeServiceImpl.deleteExpenseType(expenseTypeId);
	}
	
}

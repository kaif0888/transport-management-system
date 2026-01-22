package com.tms.expense.controller;

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

import com.tms.expense.bean.ExpenseBean;
import com.tms.expense.service.ExpenseService;
import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.product.bean.ProductBean;

@RestController
@RequestMapping("/expense")

public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/addExpense")
    public ExpenseBean addExpense(@RequestBody ExpenseBean dto) {
        return expenseService.addExpense(dto);
    }
    
    @GetMapping("/getExpenseById/{id}")
    public ExpenseBean getExpenseById(@PathVariable String id) {
        return expenseService.getExpenseById(id);
    }

    @GetMapping("/listExpense")
    public List<ExpenseBean> listAllExpenses() {
        return expenseService.listAllExpenses();
    }

    @PutMapping("/updateExpenseBy/{id}")
    public ExpenseBean updateExpense(@PathVariable("id") String id, @RequestBody ExpenseBean dto) {
        return expenseService.updateExpense(id, dto);
    }

    @DeleteMapping("/deleteExpenseBy/{id}")
    public String deleteExpense(@PathVariable("id") String id) {
        expenseService.deleteExpense(id);
        return "Expense with ID " + id + " deleted successfully.";
    }
    
	@PostMapping("/getExpensebyfilterCriteria")
	public ResponseEntity<List<ExpenseBean>> getExpensebyfilterCriteria(@RequestBody FilterRequest request) {
		try {
			int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
			List<ExpenseBean> getExpensebyfilterCriteria = expenseService.getExpensebyfilterCriteria(request.getFilters(), limit);
			return ResponseEntity.ok(getExpensebyfilterCriteria);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}
}

package com.tms.expense.service;

import java.util.List;

import com.tms.expense.bean.ExpenseBean;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.product.bean.ProductBean;



public interface ExpenseService {
    ExpenseBean addExpense(ExpenseBean expenseDTO);
    List<ExpenseBean> listAllExpenses();
    ExpenseBean updateExpense(String expenseId, ExpenseBean expenseDTO);
    void deleteExpense(String expenseId);
	List<ExpenseBean> getExpensebyfilterCriteria(List<FilterCriteriaBean> filters, int limit);
	ExpenseBean getExpenseById(String id);
}
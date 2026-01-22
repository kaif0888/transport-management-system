package com.tms.expense.service;

import java.util.List;

import com.tms.expense.bean.ExpenseBean;
import com.tms.filter.criteria.bean.FilterCriteriaBean;



public interface ExpenseService {
    ExpenseBean addExpense(ExpenseBean expenseDTO);
    List<ExpenseBean> listAllExpenses();
    ExpenseBean updateExpense(Long expenseId, ExpenseBean expenseDTO);
    void deleteExpense(Long expenseId);
}
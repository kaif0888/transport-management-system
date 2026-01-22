package com.tms.expenseType.service;

import java.util.List;

import com.tms.expenseType.bean.ExpenseTypeBean;


public interface ExpenseTypeService {
   public ExpenseTypeBean createExpenseType(ExpenseTypeBean expenseTypeBean);
   public List<ExpenseTypeBean> listAllExpenseType();
   public String deleteExpenseType(Long expenseTypeId);
}

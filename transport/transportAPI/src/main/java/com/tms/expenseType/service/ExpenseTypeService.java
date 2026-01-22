package com.tms.expenseType.service;

import java.util.List;

import com.tms.expenseType.bean.ExpenseTypeBean;
import com.tms.filter.criteria.bean.FilterCriteriaBean;


public interface ExpenseTypeService {
   public ExpenseTypeBean createExpenseType(ExpenseTypeBean expenseTypeBean);
   public List<ExpenseTypeBean> listAllExpenseType();
   ExpenseTypeBean getExpenseTypeById(String id);
   public String deleteExpenseType(String expenseTypeId);
   public List<ExpenseTypeBean> getExpenseTypebyfilterCriteria(List<FilterCriteriaBean> filters, int limit);
}

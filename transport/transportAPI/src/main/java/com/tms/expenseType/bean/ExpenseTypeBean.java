package com.tms.expenseType.bean;

import com.tms.generic.bean.GenericBean;

public class ExpenseTypeBean extends GenericBean {
   
     private String expenseTypeId;
     private String expenseTypeName;
     private String description;
     
	public String getExpenseTypeId() {
		return expenseTypeId;
	}
	public void setExpenseTypeId(String expenseTypeId) {
		this.expenseTypeId = expenseTypeId;
	}
	public String getExpenseTypeName() {
		return expenseTypeName;
	}
	public void setExpenseTypeName(String expenseTypeName) {
		this.expenseTypeName = expenseTypeName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	   
	   
	
}

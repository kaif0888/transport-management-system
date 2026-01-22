package com.tms.expenseType.entity;

import com.tms.generic.entity.GenericEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="expenseType")
public class ExpenseTypeEntity extends GenericEntity{

    @Id
    @Column(name = "expenseTypeId")
    private String expenseTypeId;

    @Column(name = "expenseTypeName")
    private String expenseTypeName;
    
    @Column(name = "description")
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

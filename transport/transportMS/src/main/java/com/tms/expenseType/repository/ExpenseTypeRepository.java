package com.tms.expenseType.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.expenseType.entity.ExpenseTypeEntity;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseTypeEntity,Long>{

}

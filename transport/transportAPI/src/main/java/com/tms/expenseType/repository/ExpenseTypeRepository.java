package com.tms.expenseType.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.expenseType.entity.ExpenseTypeEntity;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseTypeEntity,String>{
	List<ExpenseTypeEntity> findByExpenseTypeIdStartingWith(String prefix);

	boolean existsByExpenseTypeName(String string);

}


package com.tms.expense.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tms.expense.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, String> {
	List<ExpenseEntity> findByExpenseIdStartingWith(String prefix);

}

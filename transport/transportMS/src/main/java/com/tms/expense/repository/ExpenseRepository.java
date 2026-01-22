
package com.tms.expense.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tms.expense.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
}

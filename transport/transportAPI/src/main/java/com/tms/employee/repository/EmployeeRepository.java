package com.tms.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.employee.entity.EmployeeEntity;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String>{
	List<EmployeeEntity> findByEmployeeIdStartingWith(String prefix);


}

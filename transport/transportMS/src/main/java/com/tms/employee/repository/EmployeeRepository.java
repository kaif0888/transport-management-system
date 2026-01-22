package com.tms.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.employee.entity.EmployeeEntity;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long>{

}

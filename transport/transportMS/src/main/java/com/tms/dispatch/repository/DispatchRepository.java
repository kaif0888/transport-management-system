package com.tms.dispatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.dispatch.entity.DispatchEntity;



public interface DispatchRepository extends JpaRepository <DispatchEntity,Long> {

}

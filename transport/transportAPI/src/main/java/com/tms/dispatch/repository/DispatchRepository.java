package com.tms.dispatch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.dispatch.entity.DispatchEntity;



public interface DispatchRepository extends JpaRepository <DispatchEntity,String> {
	List<DispatchEntity> findByDispatchIdStartingWith(String prefix);


}

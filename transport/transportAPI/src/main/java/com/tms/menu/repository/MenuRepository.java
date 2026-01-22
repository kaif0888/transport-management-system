package com.tms.menu.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tms.menu.entity.MenuEntity;


public interface MenuRepository extends CrudRepository<MenuEntity, String> {

	public List<MenuEntity> findByParentIsNull();
}

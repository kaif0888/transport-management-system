package com.tms.productcategory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.productcategory.entity.ProductCategoryEntity;


public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long> {
}

package com.tms.productcategory.entity;

import com.tms.generic.entity.GenericEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_category")
public class ProductCategoryEntity extends GenericEntity{

    @Id
    @Column(name = "categoryId")
    private String categoryId;

    @Column(name = "categoryName")
    private String categoryName;
    
    @Column(name = "description")
    private String description;

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    

}

package com.tms.product.entity;

import java.math.BigDecimal;

import com.tms.productcategory.entity.ProductCategoryEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productId")
    private Long productId;
    
    @Column(name = "productCode")
    private String productCode;
    
    @Column(name = "productName")
    private String productName;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private ProductCategoryEntity category;

    @Column(name = "weight")
    private BigDecimal weight;

    @Column(name = "price")
    private BigDecimal price;

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public ProductCategoryEntity getCategory() {
		return category;
	}

	public void setCategory(ProductCategoryEntity category) {
		this.category = category;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	
}

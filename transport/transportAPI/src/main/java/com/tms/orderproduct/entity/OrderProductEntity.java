package com.tms.orderproduct.entity;

import java.math.BigDecimal;

import com.tms.generic.entity.GenericEntity;
import com.tms.order.entity.OrderEntity;
import com.tms.product.entity.ProductEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_product")
public class OrderProductEntity extends GenericEntity {

    @Id
    @Column(name = "orderProductId")
    private String orderProductId;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "productId")
    private ProductEntity product;
    
    @JoinColumn(name = "productName")
    private String productName;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "pricePerUnit")
    private BigDecimal pricePerUnit;

    @Column(name = "totalWeight")
    private BigDecimal totalWeight;

	public String getOrderProductId() {
		return orderProductId;
	}

	public void setOrderProductId(String orderProductId) {
		this.orderProductId = orderProductId;
	}

	public OrderEntity getOrder() {
		return order;
	}

	public void setOrder(OrderEntity order) {
		this.order = order;
	}

	public ProductEntity getProduct() {
		return product;
	}

	public void setProduct(ProductEntity product) {
		this.product = product;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public BigDecimal getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(BigDecimal totalWeight) {
		this.totalWeight = totalWeight;
	}

	
}

package com.tms.orderproduct.entity;

import java.math.BigDecimal;

import com.tms.order.entity.OrderEntity;
import com.tms.product.entity.ProductEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_product")
public class OrderProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderProductId")
    private Long orderProductId;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "productId")
    private ProductEntity product;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "pricePerUnit")
    private BigDecimal pricePerUnit;

    @Column(name = "totalWeight")
    private BigDecimal totalWeight;

	public Long getOrderProductId() {
		return orderProductId;
	}

	public void setOrderProductId(Long orderProductId) {
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

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
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

package com.tms.manifest.entity;

import java.time.LocalDate;
import java.util.List;

import com.tms.dispatch.entity.DispatchEntity;
import com.tms.generic.entity.GenericEntity;
import com.tms.location.entity.LocationEntity;
import com.tms.order.entity.OrderEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "manifest")
public class ManifestEntity extends GenericEntity {

	@Id
	private String manifestId;

	@OneToOne
	@JoinColumn(name = "dispatchId", referencedColumnName = "dispatchId")
	private DispatchEntity dispatch;

	// Repeating products per manifest – valid with ManyToMany
//	@ManyToMany
//	@JoinTable(name = "manifest_products", joinColumns = @JoinColumn(name = "manifestId"), inverseJoinColumns = @JoinColumn(name = "productId"))
//	private List<ProductEntity> products;

	// Reused start location – ManyToOne is appropriate
	@ManyToOne
	@JoinColumn(name = "startLocationId", referencedColumnName = "locationId")
	private LocationEntity startLocation;

	// Reused end location – ManyToOne is appropriate
	@ManyToOne
	@JoinColumn(name = "endLocationId", referencedColumnName = "locationId")
	private LocationEntity endLocation;

//	@ManyToOne
//	@JoinColumn(name="orderId",referencedColumnName = "orderId")
//	private List<OrderEntity> orders; 
	@ManyToMany
	@JoinTable(name = "manifest_orders", joinColumns = @JoinColumn(name = "manifestId"), inverseJoinColumns = @JoinColumn(name = "orderId"))
	private List<OrderEntity> orders;
   
	private int totalOrders;
	private int pendingOrders;
	private int deliveredOrders;
	@Column
	private LocalDate deliveryDate;

	public List<OrderEntity> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderEntity> orders) {
		this.orders = orders;
	}

	// Getters and Setters
	public String getManifestId() {
		return manifestId;
	}

	public void setManifestId(String manifestId) {
		this.manifestId = manifestId;
	}

	public DispatchEntity getDispatch() {
		return dispatch;
	}

	public void setDispatch(DispatchEntity dispatch) {
		this.dispatch = dispatch;
	}

	public LocationEntity getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(LocationEntity startLocation) {
		this.startLocation = startLocation;
	}

	public LocationEntity getEndLocation() {
		return endLocation;
	}

	public void setEndLocation(LocationEntity endLocation) {
		this.endLocation = endLocation;
	}

	public LocalDate getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDate deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public int getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(int totalOrders) {
		this.totalOrders = totalOrders;
	}

	public int getPendingOrders() {
		return pendingOrders;
	}

	public void setPendingOrders(int pendingOrders) {
		this.pendingOrders = pendingOrders;
	}

	public int getDeliveredOrders() {
		return deliveredOrders;
	}

	public void setDeliveredOrders(int deliveredOrders) {
		this.deliveredOrders = deliveredOrders;
	}



}



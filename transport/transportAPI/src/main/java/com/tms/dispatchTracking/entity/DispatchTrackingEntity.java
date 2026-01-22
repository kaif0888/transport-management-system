package com.tms.dispatchTracking.entity;

import com.tms.dispatch.entity.DispatchEntity;
import com.tms.generic.entity.GenericEntity;
import com.tms.location.entity.LocationEntity;
import com.tms.order.entity.OrderEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispatch_tracking")
public class DispatchTrackingEntity extends GenericEntity {

	@Id
	@Column(name = "tracking_id", unique = true, nullable = false)
	private String trackingId;


	@ManyToOne
    @JoinColumn(name = "dispatch_id", referencedColumnName = "dispatchId")
    private DispatchEntity dispatch;

  
//    @Column(name = "activeLocation")
//    private String activeLocation;
    
    @ManyToOne
    @JoinColumn(name = "active_location_id", referencedColumnName = "locationId")
    private LocationEntity activeLocation;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Column(name = "timestamp")
    private LocalDateTime timeStamp;

    @Column(name = "status")
    private String status;
    
    private long totalOrders;
    private long deliveredOrders;
    private long pendingOrders;

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public DispatchEntity getDispatch() {
		return dispatch;
	}

	public void setDispatch(DispatchEntity dispatch) {
		this.dispatch = dispatch;
	}



	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocationEntity getActiveLocation() {
		return activeLocation;
	}

	public void setActiveLocation(LocationEntity activeLocation) {
		this.activeLocation = activeLocation;
	}

	public long getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(long totalOrders) {
		this.totalOrders = totalOrders;
	}

	public long getDeliveredOrders() {
		return deliveredOrders;
	}

	public void setDeliveredOrders(long deliveredOrders) {
		this.deliveredOrders = deliveredOrders;
	}

	public long getPendingOrders() {
		return pendingOrders;
	}

	public void setPendingOrders(long pendingOrders) {
		this.pendingOrders = pendingOrders;
	}

	public OrderEntity getOrder() {
		return order;
	}

	public void setOrder(OrderEntity order) {
		this.order = order;
	}

	
	

}

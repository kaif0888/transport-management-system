

	package com.tms.manifest.bean;

	import java.time.LocalDate;
import java.util.List;

import com.tms.generic.bean.GenericBean;
import com.tms.order.bean.OrderBean;

	public class ManifestBean extends GenericBean{



		    private String manifestId;
		    private String dispatchId;
		  //  private List<String> productIds;  // Changed from String productId to List<String>
		    private String startLocationId;
		    private String endLocationId;
		    private String startLocationName;
		    private String endLocationName;
		    private LocalDate deliveryDate;
		    private List<String> orderIds;
		    private List<OrderBean> orders;
		    private int totalOrders;
		    private int pendingOrders;
		    private int deliveredOrders;
		    private String vehiclNumber;

		    

		    public List<OrderBean> getOrders() {
				return orders;
			}

			public void setOrders(List<OrderBean> orders) {
				this.orders = orders;
			}

			public String getManifestId() {
		        return manifestId;
		    }

		    public void setManifestId(String manifestId) {
		        this.manifestId = manifestId;
		    }

		    public String getDispatchId() {
		        return dispatchId;
		    }

		    public void setDispatchId(String dispatchId) {
		        this.dispatchId = dispatchId;
		    }

		  

		    public String getStartLocationId() {
		        return startLocationId;
		    }

		    public void setStartLocationId(String startLocationId) {
		        this.startLocationId = startLocationId;
		    }

		    public String getEndLocationId() {
		        return endLocationId;
		    }

		    public void setEndLocationId(String endLocationId) {
		        this.endLocationId = endLocationId;
		    }

		    public LocalDate getDeliveryDate() {
		        return deliveryDate;
		    }

		    public void setDeliveryDate(LocalDate deliveryDate) {
		        this.deliveryDate = deliveryDate;
		    }

			public List<String> getOrderIds() {
				return orderIds;
			}

			public void setOrderIds(List<String> orderIds) {
				this.orderIds = orderIds;
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

			public String getStartLocationName() {
				return startLocationName;
			}

			public void setStartLocationName(String startLocationName) {
				this.startLocationName = startLocationName;
			}

			public String getEndLocationName() {
				return endLocationName;
			}

			public void setEndLocationName(String endLocationName) {
				this.endLocationName = endLocationName;
			}

			public String getVehiclNumber() {
				return vehiclNumber;
			}

			public void setVehiclNumber(String vehiclNumber) {
				this.vehiclNumber = vehiclNumber;
			}

			
		    
		}

	



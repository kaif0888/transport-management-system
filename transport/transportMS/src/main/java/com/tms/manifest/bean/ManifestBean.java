

	package com.tms.manifest.bean;

	import java.time.LocalDate;
import java.util.List;

	public class ManifestBean {



		    private Long manifestId;
		    private Long dispatchId;
		    private List<Long> productIds;  // Changed from Long productId to List<Long>
		    private Long startLocationId;
		    private Long endLocationId;
		    private LocalDate deliveryDate;

		    public Long getManifestId() {
		        return manifestId;
		    }

		    public void setManifestId(Long manifestId) {
		        this.manifestId = manifestId;
		    }

		    public Long getDispatchId() {
		        return dispatchId;
		    }

		    public void setDispatchId(Long dispatchId) {
		        this.dispatchId = dispatchId;
		    }

		    public List<Long> getProductIds() {
		        return productIds;
		    }

		    public void setProductIds(List<Long> productIds) {
		        this.productIds = productIds;
		    }

		    public Long getStartLocationId() {
		        return startLocationId;
		    }

		    public void setStartLocationId(Long startLocationId) {
		        this.startLocationId = startLocationId;
		    }

		    public Long getEndLocationId() {
		        return endLocationId;
		    }

		    public void setEndLocationId(Long endLocationId) {
		        this.endLocationId = endLocationId;
		    }

		    public LocalDate getDeliveryDate() {
		        return deliveryDate;
		    }

		    public void setDeliveryDate(LocalDate deliveryDate) {
		        this.deliveryDate = deliveryDate;
		    }
		}

	



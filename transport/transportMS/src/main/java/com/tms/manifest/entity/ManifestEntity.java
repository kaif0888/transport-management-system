package com.tms.manifest.entity;

import java.time.LocalDate;
import java.util.List;

import com.tms.dispatch.entity.DispatchEntity;
import com.tms.location.entity.LocationEntity;
import com.tms.product.entity.ProductEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="manifest") 
public class ManifestEntity {
    
	


	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long manifestId;

	    @OneToOne
	    @JoinColumn(name = "dispatchId", referencedColumnName = "dispatchId")
	    private DispatchEntity dispatch;

	    // Repeating products per manifest – valid with ManyToMany
	    @ManyToMany
	    @JoinTable(
	        name = "manifest_products",
	        joinColumns = @JoinColumn(name = "manifestId"),
	        inverseJoinColumns = @JoinColumn(name = "productId")
	    )
	    private List<ProductEntity> products;

	    // Reused start location – ManyToOne is appropriate
	    @ManyToOne
	    @JoinColumn(name = "startLocationId", referencedColumnName = "locationId")
	    private LocationEntity startLocation;

	    // Reused end location – ManyToOne is appropriate
	    @ManyToOne
	    @JoinColumn(name = "endLocationId", referencedColumnName = "locationId")
	    private LocationEntity endLocation;

	    private LocalDate deliveryDate;

	    // Getters and Setters
	    public Long getManifestId() {
	        return manifestId;
	    }

	    public void setManifestId(Long manifestId) {
	        this.manifestId = manifestId;
	    }

	    public DispatchEntity getDispatch() {
	        return dispatch;
	    }

	    public void setDispatch(DispatchEntity dispatch) {
	        this.dispatch = dispatch;
	    }

	    public List<ProductEntity> getProducts() {
	        return products;
	    }

	    public void setProducts(List<ProductEntity> products) {
	        this.products = products;
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
	}

	    

	    // Getters and Setters
	    
	    
	    
	


	


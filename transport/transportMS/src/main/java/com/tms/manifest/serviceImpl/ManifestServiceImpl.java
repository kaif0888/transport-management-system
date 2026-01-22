package com.tms.manifest.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.dispatch.entity.DispatchEntity;
import com.tms.dispatch.repository.DispatchRepository;
import com.tms.location.entity.LocationEntity;
import com.tms.location.repository.LocationRepository;
import com.tms.manifest.bean.ManifestBean;
import com.tms.manifest.entity.ManifestEntity;
import com.tms.manifest.repository.ManifestRepository;
import com.tms.manifest.service.ManifestService;
import com.tms.product.entity.ProductEntity;
import com.tms.product.repository.ProductRepository;

@Service
public class ManifestServiceImpl implements ManifestService{
    @Autowired
	private ManifestRepository manifestRepo;
    
    @Autowired
    private DispatchRepository dispatchRepo;
	
    @Autowired
    private ProductRepository productRepo;
    
    @Autowired
    private LocationRepository locationRepo;
    
    
    @Override
    public ManifestBean createManifest(ManifestBean manifestBean) {
        Long dispatchId = manifestBean.getDispatchId();
        if (dispatchId == null) {
            throw new IllegalArgumentException("Dispatch Id must not be null");
        }

        DispatchEntity dispatch = dispatchRepo.findById(dispatchId)
            .orElseThrow(() -> new RuntimeException("Dispatch not found: " + dispatchId));

        List<Long> productIds = manifestBean.getProductIds();
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product Ids must not be null or empty");
        }

        List<ProductEntity> products = productRepo.findAllById(productIds);

        LocationEntity startLocation = locationRepo.findById(manifestBean.getStartLocationId())
            .orElseThrow(() -> new RuntimeException("Start location not found: " + manifestBean.getStartLocationId()));

        LocationEntity endLocation = locationRepo.findById(manifestBean.getEndLocationId())
            .orElseThrow(() -> new RuntimeException("End location not found: " + manifestBean.getEndLocationId()));

        ManifestEntity entity = new ManifestEntity();
        entity.setDispatch(dispatch);
        entity.setProducts(products);
        entity.setStartLocation(startLocation);
        entity.setEndLocation(endLocation);
        entity.setDeliveryDate(manifestBean.getDeliveryDate());

        ManifestEntity saved = manifestRepo.save(entity);

        ManifestBean savedBean = new ManifestBean();
        savedBean.setManifestId(saved.getManifestId());
        savedBean.setDispatchId(dispatchId);
        savedBean.setProductIds(productIds);
        savedBean.setStartLocationId(startLocation.getLocationId());
        savedBean.setEndLocationId(endLocation.getLocationId());
        savedBean.setDeliveryDate(saved.getDeliveryDate());

        return savedBean;
    }


		@Override
		public List<ManifestBean> listManifestBean() {
		    List<ManifestEntity> entities = manifestRepo.findAll();
		    List<ManifestBean> beans = new ArrayList<>();

		    for (ManifestEntity entity : entities) {
		        ManifestBean bean = new ManifestBean();
		        bean.setManifestId(entity.getManifestId());

		        if (entity.getDispatch() != null) {
		            bean.setDispatchId(entity.getDispatch().getDispatchId());
		        }

		        if (entity.getProducts() != null) {
		        	List<Long> productIds = entity.getProducts().stream()
		                    .map(ProductEntity::getProductId)
		                    .collect(Collectors.toList()); 

		            bean.setProductIds(productIds);
		        }

		        if (entity.getStartLocation() != null) {
		            bean.setStartLocationId(entity.getStartLocation().getLocationId());
		        }

		        if (entity.getEndLocation() != null) {
		            bean.setEndLocationId(entity.getEndLocation().getLocationId());
		        }

		        bean.setDeliveryDate(entity.getDeliveryDate());

		        beans.add(bean);
		    }

		    return beans;
		}
		

		@Override
		public ManifestBean updateManifestBean(ManifestBean manifestBean) {
		    ManifestEntity entity = manifestRepo.findById(manifestBean.getManifestId())
		        .orElseThrow(() -> new RuntimeException("Manifest not found with ID: " + manifestBean.getManifestId()));

		    if (manifestBean.getStartLocationId() != null) {
		        LocationEntity start = locationRepo.findById(manifestBean.getStartLocationId())
		            .orElseThrow(() -> new RuntimeException("Start Location not found"));
		        entity.setStartLocation(start);
		    }

		    if (manifestBean.getEndLocationId() != null) {
		        LocationEntity end = locationRepo.findById(manifestBean.getEndLocationId())
		            .orElseThrow(() -> new RuntimeException("End Location not found"));
		        entity.setEndLocation(end);
		    }

		    if (manifestBean.getProductIds() != null && !manifestBean.getProductIds().isEmpty()) {
		        List<ProductEntity> products = productRepo.findAllById(manifestBean.getProductIds());
		        entity.setProducts(products);
		    }

		    entity.setDeliveryDate(manifestBean.getDeliveryDate());

		    ManifestEntity updated = manifestRepo.save(entity);

		    ManifestBean updatedBean = new ManifestBean();
		    updatedBean.setManifestId(updated.getManifestId());
		    updatedBean.setDispatchId(updated.getDispatch().getDispatchId());

		    updatedBean.setProductIds(
		        updated.getProducts().stream()
		            .map(ProductEntity::getProductId)
		            .collect(Collectors.toList())
		    );

		    updatedBean.setStartLocationId(updated.getStartLocation().getLocationId());
		    updatedBean.setEndLocationId(updated.getEndLocation().getLocationId());
		    updatedBean.setDeliveryDate(updated.getDeliveryDate());

		    return updatedBean;
		}

    




}

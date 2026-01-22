package com.tms.driver.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.document.bean.DocumentBean;
import com.tms.document.entity.DocumentEntity;
import com.tms.document.repository.DocumentRepository;
import com.tms.driver.been.AvalaibleDriverBean;
import com.tms.driver.been.DriverBean;
import com.tms.driver.been.DriverDocumentRequest;
import com.tms.driver.been.DriverDocumentResponseBean;
import com.tms.driver.entity.DriverEntity;
import com.tms.driver.repository.DriverRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.vehicle.entity.VehicleEntity;
import com.tms.vehicle.repository.VehicleRepository;


@Service
public class DriverServiceImpl implements DriverService {

	@Autowired
	private DriverRepository driverRepository;
	@Autowired
	private VehicleRepository vehicleRepository;
	
	@Autowired
	UserRepository  userRepository;

	@Autowired
	private DocumentRepository documentRepository;
	
	private String generateUniqueDriverId() {
        String prefix = "DRI-";
        String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";

        List<DriverEntity> todayDrivers = driverRepository.findByDriverIdStartingWith(fullPrefix);

        int maxSeq = todayDrivers.stream()
            .map(d -> d.getDriverId().substring(fullPrefix.length()))
            .mapToInt(seq -> {
                try {
                    return Integer.parseInt(seq);
                } catch (NumberFormatException e) {
                    return 0;
                }
            })
            .max()
            .orElse(0);

        int nextSeq = maxSeq + 1;
        String formattedSeq = String.format("%03d", nextSeq);

        return fullPrefix + formattedSeq;
    }

	@Override
	public DriverBean addDriver(DriverBean bean) {
	    Optional<DriverEntity> existingDriver = driverRepository.findByLicenseNumber(bean.getLicenseNumber());
	    if (existingDriver.isPresent()) {
	        throw new RuntimeException("Driver with license number " + bean.getLicenseNumber() + " already exists.");
	    }

	    DriverEntity entity = new DriverEntity();

	    String uniqueDriverId = generateUniqueDriverId();
	    entity.setDriverId(uniqueDriverId);
	    entity.setLastModifiedDate(LocalDateTime.now());
	    bean.setDriverId(uniqueDriverId);

	    entity.setName(bean.getName());
	    entity.setLicenseNumber(bean.getLicenseNumber());
	    entity.setLicenseExpiry(bean.getLicenseExpiry());
	    entity.setContactNumber(bean.getContactNumber());

	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    entity.setCreatedBy(authentication.getName());

	    User currentUser = userRepository.findByEmail(authentication.getName())
	            .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
	    entity.setBranchIds(currentUser.getBranchIds());

	    // 🔥 Associate documents with the driver
	    if (bean.getDocumentIds() != null && !bean.getDocumentIds().isEmpty()) {
	        List<DocumentEntity> associatedDocuments = new ArrayList<>();
	        for (String docId : bean.getDocumentIds()) {
	            DocumentEntity document = documentRepository.findByDocumentId(docId)
	                    .orElseThrow(() -> new RuntimeException("Document with ID " + docId + " not found"));
	            document.setDriver(entity); // link to driver
	            associatedDocuments.add(document);
	        }
	        entity.setDocuments(associatedDocuments);
	    }

	    // 🔄 Associate vehicle if present
	    if (bean.getAssignedVehicleId() != null) {
	        Optional<VehicleEntity> vehicleOpt = vehicleRepository.findById(bean.getAssignedVehicleId());
	        vehicleOpt.ifPresent(entity::setAssignedVehicle);
	    }

	    driverRepository.save(entity);
	    BeanUtils.copyProperties(entity, bean);

	    // 🔄 Send back the linked documentIds
	    if (entity.getDocuments() != null && !entity.getDocuments().isEmpty()) {
	        List<String> documentIdList = entity.getDocuments().stream()
	            .map(DocumentEntity::getDocumentId)
	            .collect(Collectors.toList());
	        bean.setDocumentIds(documentIdList);
	    }

	    return bean;
	}

	
	public DriverDocumentResponseBean  getDriverById(String driverId) {
	    DriverEntity driver = driverRepository.findByDriverId(driverId)
	        .orElseThrow(() -> new RuntimeException("Driver not found with ID: " + driverId));

	    DriverDocumentResponseBean bean = new DriverDocumentResponseBean();
	    bean.setDriverId(driver.getDriverId());
	    bean.setName(driver.getName());
	    bean.setLicenseNumber(driver.getLicenseNumber());
	    bean.setContactNumber(driver.getContactNumber());
	    bean.setLicenseExpiry(driver.getLicenseExpiry());

	    // Convert and set document beans
	    if (driver.getDocuments() != null && !driver.getDocuments().isEmpty()) {
	        List<DocumentBean> documentBeans = driver.getDocuments().stream().map(doc -> {
	            DocumentBean docBean = new DocumentBean();
	            docBean.setDocumentId(doc.getDocumentId());
	            docBean.setName(doc.getName());
	            docBean.setType(doc.getType());
	            docBean.setFileUrl(doc.getFileUrl());
	            docBean.setDocumentName(doc.getDocumentName());
	            docBean.setDocumentStatus(doc.getDocumentStatus());
	            return docBean;
	        }).collect(Collectors.toList());

	        bean.setDocumentIds(documentBeans);
	    }

	    if (driver.getAssignedVehicle() != null) {
	        bean.setAssignedVehicleId(driver.getAssignedVehicle().getVehicleId());
	    }

	    return bean;
	}


	@Override
	public List<DriverBean> getDrivers() {
		return driverRepository.findAll().stream().map(driver -> {
			DriverBean bean = new DriverBean();
			bean.setDriverId(driver.getDriverId());
			bean.setName(driver.getName());
			bean.setLicenseNumber(driver.getLicenseNumber());
			bean.setContactNumber(driver.getContactNumber());
			bean.setDriverId(driver.getDriverId());
			return bean;
		}).collect(Collectors.toList());
	}

	@Override
	public DriverBean updateDriver(String id, DriverBean bean) {
	    // Fetch driver by ID
	    DriverEntity driver = driverRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Driver not found with ID: " + id));

	    // ✅ Update basic fields
	    if (bean.getName() != null) {
	        driver.setName(bean.getName());
	    }
	    if (bean.getLicenseNumber() != null) {
	        driver.setLicenseNumber(bean.getLicenseNumber());
	    }
	    if (bean.getContactNumber() != null) {
	        driver.setContactNumber(bean.getContactNumber());
	    }
	    if (bean.getLicenseExpiry() != null) {
	        driver.setLicenseExpiry(bean.getLicenseExpiry());
	    }

	    // ✅ Re-associate documents
	    if (bean.getDocumentIds() != null) {
	        // 1. Unlink all old documents
	        if (driver.getDocuments() != null && !driver.getDocuments().isEmpty()) {
	            for (DocumentEntity oldDoc : driver.getDocuments()) {
	                oldDoc.setDriver(null);
	                documentRepository.save(oldDoc);
	            }
	        }

	        // 2. Link new documents
	        List<DocumentEntity> newDocuments = new ArrayList<>();
	        for (String docId : bean.getDocumentIds()) {
	            DocumentEntity document = documentRepository.findByDocumentId(docId)
	                    .orElseThrow(() -> new RuntimeException("Document with ID " + docId + " not found"));
	            document.setDriver(driver); // Link to driver
	            documentRepository.save(document);
	            newDocuments.add(document);
	        }
	        driver.setDocuments(newDocuments);
	    }

	    // ✅ Re-associate vehicle (if needed)
	    if (bean.getAssignedVehicleId() != null) {
	        Optional<VehicleEntity> vehicleOpt = vehicleRepository.findById(bean.getAssignedVehicleId());
	        vehicleOpt.ifPresent(driver::setAssignedVehicle);
	    }

	    // ✅ Set audit fields
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    driver.setLastModifiedBy(authentication.getName());
	    driver.setLastModifiedDate(LocalDateTime.now());

	    // ✅ Save updated driver
	    driverRepository.save(driver);

	    // ✅ Prepare response bean
	    DriverBean updatedBean = new DriverBean();
	    BeanUtils.copyProperties(driver, updatedBean);

	    // Set updated document IDs
	    if (driver.getDocuments() != null && !driver.getDocuments().isEmpty()) {
	        List<String> docIds = driver.getDocuments().stream()
	                .map(DocumentEntity::getDocumentId)
	                .collect(Collectors.toList());
	        updatedBean.setDocumentIds(docIds);
	    }

	    // Set assigned vehicle ID
	    if (driver.getAssignedVehicle() != null) {
	        updatedBean.setAssignedVehicleId(driver.getAssignedVehicle().getVehicleId());
	    }

	    return updatedBean;
	}



	@Override
	public String deleteDriver(String driverId) {
		DriverEntity driver = driverRepository.findById(driverId)
				.orElseThrow(() -> new RuntimeException("Driver not found"));
		driverRepository.delete(driver);
		return "Driver Deleted Successfully :";
	}

	@Override
	public List<AvalaibleDriverBean> getAvailableDrivers() {
		return driverRepository.findDriversWithNoVehicle().stream().map(driver -> {
			AvalaibleDriverBean availableDriverBean = new AvalaibleDriverBean();
			availableDriverBean.setDriverId(driver.getDriverId());
			availableDriverBean.setName(driver.getName());
			availableDriverBean.setLicenceNumber(driver.getLicenseNumber());
			availableDriverBean.setContactNumber(driver.getContactNumber());
			return availableDriverBean;
		}).collect(Collectors.toList());
	}
	@Autowired
	private FilterCriteriaService<DriverEntity> filterCriteriaService;
	
	@Override
	public List<DriverBean> getDriverbyfilterCriteria(List<FilterCriteriaBean> filters, int limit) {
		try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
        	if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().name())) {
        	    // Remove any pre-existing branch filter (if present)
        	    filters.removeIf(f -> f.getAttribute().equalsIgnoreCase("branchIds"));

        	    // Convert comma-separated branchIds string to comma-separated string for value
        	    String branchIds = currentUser.getBranchIds(); // e.g., "BR001,BR002"

        	    FilterCriteriaBean branchFilter = new FilterCriteriaBean();
        	    branchFilter.setAttribute("branchIds");
        	    branchFilter.setOperation(FilterOperation.AMONG);
        	    branchFilter.setValue(branchIds);  // Still a comma-separated string
        	    branchFilter.setValueType(String.class); // Optional

        	    filters.add(branchFilter);
        	}
			List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(DriverEntity.class, filters, limit);
			return (List<DriverBean>) filteredEntities.stream().map(entity -> convertToBean((DriverEntity) entity))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Error filtering Driver: " + e.getMessage());
		}
	}
	
	
	
	 @Override
	    public List<Map<String, Object>> getDriverExpiryList() {

	        List<DriverEntity> driver = driverRepository.findAll();
	        List<Map<String, Object>> expiryList = new ArrayList<>();

	        LocalDate today = LocalDate.now();

	        for (DriverEntity v : driver) {

	            Map<String, Object> data = new HashMap<>();

	            // license Expiry
	            if (v.getLicenseExpiry() != null) {
	            	Object date = v.getLicenseExpiry();
	                long daysLeft = ChronoUnit.DAYS.between(today, v.getLicenseExpiry());
	                if (daysLeft <= 5) {
	                    data.put("driverLicencseExpiry", v.getLicenseExpiry());
	                }
	            }
	            
	         // Add only if at least one document is expired or expiring soon
	            if (!data.isEmpty()) {
	                data.put("License_Number", v.getLicenseNumber());
	                expiryList.add(data);
	            }
	        }

	        return expiryList;
	    }
	
	
	
	private DriverBean convertToBean(DriverEntity driverEntity) {
	    DriverBean driverBean = new DriverBean();
	    driverBean.setDriverId(driverEntity.getDriverId());
	    driverBean.setLicenseNumber(driverEntity.getLicenseNumber());
	    driverBean.setContactNumber(driverEntity.getContactNumber());
	    driverBean.setLicenseExpiry(driverEntity.getLicenseExpiry());
	    driverBean.setName(driverEntity.getName());
	    driverBean.setLastModifiedBy(driverEntity.getLastModifiedBy());
	    driverBean.setLastModifiedDate(driverEntity.getLastModifiedDate());
	    if (driverBean.getDocumentIds() != null && !driverBean.getDocumentIds().isEmpty()) {
	        List<DocumentEntity> associatedDocuments = new ArrayList<>();
	        for (String docId : driverBean.getDocumentIds()) {
	            DocumentEntity document = documentRepository.findByDocumentId(docId)
	                    .orElseThrow(() -> new RuntimeException("Document with ID " + docId + " not found"));
	            document.setDriver(driverEntity); // link to driver
	            associatedDocuments.add(document);
	        }
	        driverEntity.setDocuments(associatedDocuments);
	    }
//	    if (driverEntity.getDocuments() != null && !driverEntity.getDocuments().isEmpty()) {
//	        List<DocumentBean> docBeans = driverEntity.getDocuments().stream().map(doc -> {
//	            DocumentBean b = new DocumentBean();
//	            b.setDocumentId(doc.getDocumentId());
//	            b.setName(doc.getName());
//	            b.setType(doc.getType());
//	            b.setFileUrl(doc.getFileUrl());
//	            b.setDocumentName(doc.getDocumentName());
//	            b.setDocumentStatus(doc.getDocumentStatus());
//	            return b;
//	        }).collect(Collectors.toList());
////	        driverBean.setDocumentIds(docBeans);
//	    }
	    
	    if (driverEntity.getAssignedVehicle() != null) {
	        driverBean.setAssignedVehicleId(driverEntity.getAssignedVehicle().getVehicleId());
	        driverBean.setAssignedVehicleNumber(driverEntity.getAssignedVehicle().getRegistrationNumber());
	    }

	    return driverBean;
	}


	@Override
	public DriverBean assignVechileDriver(String driverId, String vehicleId) {
	    // Fetch driver
	    DriverEntity driver = driverRepository.findById(driverId)
	        .orElseThrow(() -> new RuntimeException("Driver not found with ID: " + driverId));

	    // Check if the driver already has an assigned vehicle
	    if (driver.getAssignedVehicle() != null) {
	        throw new RuntimeException("Driver is already assigned to another vehicle.");
	    }

	    // Fetch vehicle
	    VehicleEntity vehicle = vehicleRepository.findById(vehicleId)
	        .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));

	    // Check if vehicle is already assigned to another driver
	    Optional<DriverEntity> vehicleAssignedTo = driverRepository.findByAssignedVehicle(vehicle);
	    if (vehicleAssignedTo.isPresent()) {
	        throw new RuntimeException("Vehicle is already assigned to driver ID: " + vehicleAssignedTo.get().getDriverId());
	    }

	    // Assign vehicle to driver
	    driver.setAssignedVehicle(vehicle);
	    driverRepository.save(driver);

	    // Prepare and return response
	    DriverBean bean = new DriverBean();
	    bean.setDriverId(driver.getDriverId());
	    bean.setName(driver.getName());
	    bean.setLicenseNumber(driver.getLicenseNumber());
	    bean.setContactNumber(driver.getContactNumber());
	    bean.setAssignedVehicleId(vehicleId);

	    return bean;
	}

	
	@Override
	public DriverBean unAssignVechileDriver(String driverId, String vehicleId) {
	    // Fetch driver
	    DriverEntity driver = driverRepository.findById(driverId)
	        .orElseThrow(() -> new RuntimeException("Driver not found with ID: " + driverId));

	    // Fetch vehicle
	    VehicleEntity vehicle = vehicleRepository.findById(vehicleId)
	        .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));

	    // Check if vehicle is already assigned to another driver
	    Optional<DriverEntity> vehicleAssignedTo = driverRepository.findByAssignedVehicle(vehicle);
	    if (vehicleAssignedTo.isPresent()) {
		    // unAssign vehicle to driver
		    driver.setAssignedVehicle(null);
		    driverRepository.save(driver);
	    }

	    // Prepare and return response
	    DriverBean bean = new DriverBean();
	    bean.setDriverId(driver.getDriverId());
	    bean.setName(driver.getName());
	    bean.setLicenseNumber(driver.getLicenseNumber());
	    bean.setContactNumber(driver.getContactNumber());
	    bean.setAssignedVehicleId(vehicleId);

	    return bean;
	}

	@Override
	public Object addDriverWithDocument(DriverBean driver, DocumentBean document) {
		// TODO Auto-generated method stub
		return null;
	}





}

package com.tms.vehicle.service;

import java.util.List;
import java.util.Map;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.vehicle.bean.VehicleWithDocumentsRequest;
import com.tms.vehicle.bean.VehicleAvalaibleDropdown;

public interface VehicleService {


	
	VehicleWithDocumentsRequest createVehicleWithDocuments(VehicleWithDocumentsRequest request);

	List<VehicleWithDocumentsRequest> getAllVehicles();

	List<VehicleWithDocumentsRequest> getVehiclesByRegistrationNumber(String registrationNumber);

	VehicleWithDocumentsRequest updateVehicle(String vehicleId, VehicleWithDocumentsRequest request);

	Map<String, List<VehicleWithDocumentsRequest>> getVehiclesGroupedByModel();

	VehicleWithDocumentsRequest getVehicleById(String vehicleId);

	List<VehicleAvalaibleDropdown> getAvailableVehicles();

	List<VehicleAvalaibleDropdown> getUnrentedVehicles();

	List<String> getDistinctVehicleModels();

	List<VehicleWithDocumentsRequest> getvehiclebyfilterCriteria(List<FilterCriteriaBean> filters, int limit);
	
	List<Map<String, Object>> getVehicleExpiryList();

}
package com.tms.vehicle.service;

import java.util.List;
import java.util.Map;

import com.tms.vehicle.been.VehicleAvalaibleDropdown;
import com.tms.vehicle.been.VehicleBean;

public interface VehicleService {

	VehicleBean createVehicle(VehicleBean vehicleBean);

	List<VehicleBean> getAllVehicles();

	List<VehicleBean> getVehiclesByRegistrationNumber(String registrationNumber);

	VehicleBean updateVehicle(Long vehicleId, VehicleBean bean);

	Map<String, List<VehicleBean>> getVehiclesGroupedByModel();

	VehicleBean getVehicleById(Long vehicleId);

	List<VehicleAvalaibleDropdown> getAvailableVehicles();

	List<VehicleAvalaibleDropdown> getUnrentedVehicles();

	List<String> getDistinctVehicleModels();

//	List<String> getDistinctVehicleCompany();

//	List<VehicleBean> getFilteredVehicles(String registrationNumber, String model, String company);

}

package com.tms.vehicletms.service;


import java.util.List;
import java.util.Map;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.vehicletms.been.VehicleAvalaibleDropdown;
import com.tms.vehicletms.been.VehicleBean;

public interface VehicleService {
	
  //	This method will create a new vehicle. It takes a VehicleBean object as input and returns the created
  //	VehicleBean object (likely with some added information like the generated vehicleId).
	VehicleBean createVehicle(VehicleBean vehicleBean);
	
//	This method will fetch a list of all vehicles. It returns a List<VehicleBean>, 
//	containing all the vehicle data in the system.
    List<VehicleBean> getAllVehicles();
    List<VehicleBean> getVehiclesByRegistrationNumber(String registrationNumber);
    
//    This method will fetch a list of all vehicles. It returns
//    a List<VehicleBean>, containing all the vehicle data in the system.

    VehicleBean updateVehicle(Integer vehicleId, VehicleBean bean);
 
// This method will group vehicles by their model. It returns a 
// Map where the key is the model name (String), and the value is a List<VehicleBean> 
// containing all vehicles of that model.
    Map<String, List<VehicleBean>> getVehiclesGroupedByModel(); 
    VehicleBean getVehicleById(Integer vehicleId);
    List<VehicleAvalaibleDropdown> getAvailableVehicles();
    List<VehicleAvalaibleDropdown> getUnrentedVehicles();
    List<String> getDistinctVehicleModels();
    List<String> getDistinctVehicleCompany();
    List<VehicleBean> getFilteredVehicles(String registrationNumber, String model, String company);

	List<VehicleBean> getvehiclebyfilterCriteria(List<FilterCriteriaBean> filters, int limit);


    
    
    }

package com.tms.vehicletype.service;

import java.util.List;


import com.tms.vehicletype.Bean.VehicleTypeBean;

public interface VehicleTypeService {
    public VehicleTypeBean createVehicleType(VehicleTypeBean vehicleType);
    
    public List<VehicleTypeBean> listVehicleType();
    
    public VehicleTypeBean updateVehicleType(VehicleTypeBean vehicleType);
    
    public String deleteVehicleType(String vehicleTypeId);
    
}

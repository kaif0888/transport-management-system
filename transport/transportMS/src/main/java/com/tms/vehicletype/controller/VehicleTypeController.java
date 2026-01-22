package com.tms.vehicletype.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.vehicletype.Bean.VehicleTypeBean;
import com.tms.vehicletype.service.VehicleTypeService;

@RestController
@RequestMapping("/vehicleType")
public class VehicleTypeController {

	@Autowired
	private VehicleTypeService vehicleTypeService;
	
	@PostMapping("/createVehicleType")
	public VehicleTypeBean createVehicleType(@RequestBody VehicleTypeBean vehicleTypeBean)
	{
		return vehicleTypeService.createVehicleType(vehicleTypeBean);
	}
	
	@GetMapping("/listVehicleType")
	public List<VehicleTypeBean> listVehicleType()
	{
		return vehicleTypeService.listVehicleType();
	}
	
	@PutMapping("/updateVehicleType")
	public VehicleTypeBean updateVehicleType(@RequestBody VehicleTypeBean vehicleTypeBean)
	{
		return vehicleTypeService.updateVehicleType(vehicleTypeBean);
	}
	
	@DeleteMapping("/deleteVehicleType/{vehicleTypeId}")
	public String deleteVehicleType(@PathVariable Long vehicleTypeId)
	{
		return vehicleTypeService.deleteVehicleType(vehicleTypeId);
	}
	
	
}

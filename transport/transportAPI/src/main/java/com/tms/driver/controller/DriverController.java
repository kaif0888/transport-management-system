package com.tms.driver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tms.driver.been.AvalaibleDriverBean;
import com.tms.driver.been.DriverBean;
import com.tms.driver.been.DriverDocumentResponseBean;
import com.tms.driver.service.DriverService;
import com.tms.filter.criteria.bean.FilterRequest;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/drivers")
public class DriverController {

	@Autowired
	private DriverService driverService;

	@PostMapping("/addDriverDetails")
	public ResponseEntity<DriverBean> addDriver(@RequestBody DriverBean bean) {
		System.out.println("driver"+bean);
		return ResponseEntity.ok(driverService.addDriver(bean));
	}
	
	

	@GetMapping("/getDriverById/{driverId}")
	public ResponseEntity<DriverDocumentResponseBean > getDriverById(@PathVariable String driverId) {
		try {
			DriverDocumentResponseBean  driver = driverService.getDriverById(driverId);
			return ResponseEntity.ok(driver);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@GetMapping("/listDrivers")
	public ResponseEntity<List<DriverBean>> listDrivers() {
		return ResponseEntity.ok(driverService.getDrivers());

	}

	@PutMapping("/Updateby/{id}")
	public ResponseEntity<?> updateDriver(@PathVariable String id, @RequestBody DriverBean bean) {
		try {
			DriverBean updatedDriver = driverService.updateDriver(id, bean);
			return ResponseEntity.ok(updatedDriver);
		} catch (IllegalArgumentException e) {
			// For validation errors like missing license
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (RuntimeException e) {
			// For not found errors
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
	
	@PutMapping("/assignVechileDriver/{driverId}/{vechicleId}")
	public ResponseEntity<?> assignVechileDriver(@PathVariable String driverId,@PathVariable String vechicleId) {
		try {
			DriverBean updatedDriver = driverService.assignVechileDriver(driverId, vechicleId);
			return ResponseEntity.ok(updatedDriver);
		} catch (IllegalArgumentException e) {
			// For validation errors like missing license
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (RuntimeException e) {
			// For not found errors
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
	
	@PutMapping("/unAssignVechileDriver/{driverId}/{vechicleId}")
	public ResponseEntity<?> unAssignVechileDriver(@PathVariable String driverId,@PathVariable String vechicleId) {
		try {
			DriverBean updatedDriver = driverService.unAssignVechileDriver(driverId, vechicleId);
			return ResponseEntity.ok(updatedDriver);
		} catch (IllegalArgumentException e) {
			// For validation errors like missing license
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (RuntimeException e) {
			// For not found errors
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@DeleteMapping("/deleteDriverBy/{driverId}")
	public ResponseEntity<String> deleteDriver(@PathVariable String driverId) {
		driverService.deleteDriver(driverId);
		return ResponseEntity.ok("Driver deleted successfully");
	}

	@GetMapping("/availableDrivers")
	public ResponseEntity<List<AvalaibleDriverBean>> getAvailableDrivers() {
		List<AvalaibleDriverBean> availableDrivers = driverService.getAvailableDrivers();
		return ResponseEntity.ok(availableDrivers);
	}
	
	
   @GetMapping("/driverLicenseExpiries")
    public List<Map<String, Object>> getVehicleExpiries() {
        return driverService.getDriverExpiryList();
    }
	
	
	
	@PostMapping("/getlistOfDriversByFilterCriteria")
	public ResponseEntity<List<DriverBean>> getDriverbyfilterCriteria(@RequestBody FilterRequest request) {
		try {
			int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
			List<DriverBean> getDriverbyfilterCriteria = driverService.getDriverbyfilterCriteria(request.getFilters(), limit);
			return ResponseEntity.ok(getDriverbyfilterCriteria);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}
}

package com.tms.drivertms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tms.drivertms.been.AvalaibleDriverBean;
import com.tms.drivertms.been.DriverBeen;
import com.tms.drivertms.service.DriverService;
import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.vehicletms.been.VehicleBean;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/drivers")
public class DriverController {

	@Autowired
	private DriverService driverService;

	@PostMapping("/addDriverDetails")
	public ResponseEntity<DriverBeen> addDriver(@RequestBody DriverBeen bean) {
		return ResponseEntity.ok(driverService.addDriver(bean));
	}

	@GetMapping("/getDriverById/{driverId}")
	public ResponseEntity<DriverBeen> getDriverById(@PathVariable Integer driverId) {
		try {
			DriverBeen driver = driverService.getDriverById(driverId);
			return ResponseEntity.ok(driver);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

//	@GetMapping("/listDrivers")
//	public ResponseEntity<List<DriverBeen>> listDrivers() {
//		return ResponseEntity.ok(driverService.getDrivers());
//
//	}
	@PostMapping("/listDrivers")
	public ResponseEntity<List<DriverBeen>> getDriverbyfilterCriteria(@RequestBody FilterRequest request) {
		try {
			int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
			List<DriverBeen> getDriverbyfilterCriteria = driverService.getDriverbyfilterCriteria(request.getFilters(), limit);
			return ResponseEntity.ok(getDriverbyfilterCriteria);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}


	@PutMapping("/Updateby/{id}")
	public ResponseEntity<?> updateDriver(@PathVariable Integer id, @RequestBody DriverBeen bean) {
		try {
			DriverBeen updatedDriver = driverService.updateDriver(id, bean);
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
	public ResponseEntity<String> deleteDriver(@PathVariable Integer driverId) {
		driverService.deleteDriver(driverId);
		return ResponseEntity.ok("Driver deleted successfully");
	}

	@GetMapping("/availableDrivers")
	public ResponseEntity<List<AvalaibleDriverBean>> getAvailableDrivers() {
		List<AvalaibleDriverBean> availableDrivers = driverService.getAvailableDrivers();
		return ResponseEntity.ok(availableDrivers);
	}
}

package com.tms.driver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tms.driver.been.AvalaibleDriverBean;
import com.tms.driver.been.DriverBean;
import com.tms.driver.service.DriverService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/drivers")
public class DriverController {

	@Autowired
	private DriverService driverService;

	@PostMapping("/addDriverDetails")
	public ResponseEntity<DriverBean> addDriver(@RequestBody DriverBean bean) {
		return ResponseEntity.ok(driverService.addDriver(bean));
	}

	@GetMapping("/getDriverById/{driverId}")
	public ResponseEntity<DriverBean> getDriverById(@PathVariable Long driverId) {
		try {
			DriverBean driver = driverService.getDriverById(driverId);
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
	public ResponseEntity<?> updateDriver(@PathVariable Long id, @RequestBody DriverBean bean) {
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

	@DeleteMapping("/deleteDriverBy/{driverId}")
	public ResponseEntity<String> deleteDriver(@PathVariable Long driverId) {
		driverService.deleteDriver(driverId);
		return ResponseEntity.ok("Driver deleted successfully");
	}

	@GetMapping("/availableDrivers")
	public ResponseEntity<List<AvalaibleDriverBean>> getAvailableDrivers() {
		List<AvalaibleDriverBean> availableDrivers = driverService.getAvailableDrivers();
		return ResponseEntity.ok(availableDrivers);
	}
}

package com.tms.vehicle.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tms.vehicle.been.VehicleAvalaibleDropdown;
import com.tms.vehicle.been.VehicleBean;
import com.tms.vehicle.service.VehicleService;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins="*")
@RestController                                                
@RequestMapping("/vehicles")                                 
public class VehicleController {


 @Autowired                                                      
 private VehicleService vehicleService;	

 
 @PostMapping("/createVehicle")
 public VehicleBean createVehicle(@RequestBody VehicleBean vehicleBean) {
     return vehicleService.createVehicle(vehicleBean);          
 }

// @GetMapping("/listVehicles")
// public List<VehicleBean> listVehicles(
//     @RequestParam(required = false) String registrationNumber,
//     @RequestParam(required = false) String model,
//     @RequestParam(required = false) String company
// ) {
//     // Check for filter parameters
//     if (registrationNumber != null || model != null || company != null) {
//         return vehicleService.getFilteredVehicles(registrationNumber, model, company);
//     } else {
//         return vehicleService.getAllVehicles();
//     }
// }

 
 @PutMapping("/updateVehicle/{id}")
 public ResponseEntity<?> updateVehicle(@PathVariable("id") Long vehicleId, @RequestBody VehicleBean vehicleBean) {
     try {
         VehicleBean updatedVehicle = vehicleService.updateVehicle(vehicleId, vehicleBean);
         return ResponseEntity.ok(updatedVehicle);
     } catch (IllegalArgumentException e) {
         return ResponseEntity.badRequest().body(e.getMessage());
     } catch (RuntimeException e) {
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
     } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
     }
 }
 
 
 @GetMapping("/vehicles/grouped-by-model")
 public ResponseEntity<Map<String, List<VehicleBean>>> getVehiclesGroupedByModel() {
     return ResponseEntity.ok(vehicleService.getVehiclesGroupedByModel());
 
 }
 
 @GetMapping("/vehicles/{id}")
 public ResponseEntity<VehicleBean> getVehicleById(@PathVariable("id") Long vehicleId) {
     try {
         VehicleBean vehicle = vehicleService.getVehicleById(vehicleId);
         return ResponseEntity.ok(vehicle);
     } catch (RuntimeException e) {
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
     } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
     }
 }
 
 @GetMapping("/available")
 public ResponseEntity<List<VehicleAvalaibleDropdown>> getAvailableVehicles() {
     List<VehicleAvalaibleDropdown> availableVehicles = vehicleService.getAvailableVehicles();
     return new ResponseEntity<>(availableVehicles, HttpStatus.OK);
 }
 
 @GetMapping("/unrented")
 public ResponseEntity<List<VehicleAvalaibleDropdown>> getUnrentedVehicles() {
     List<VehicleAvalaibleDropdown> unrentedVehicles = vehicleService.getUnrentedVehicles();
     return new ResponseEntity<>(unrentedVehicles, HttpStatus.OK);
 }
 
 @GetMapping("/getAllVehicleModels")
 public ResponseEntity<List<String>> getDistinctVehicleModels() {
     List<String> models = vehicleService.getDistinctVehicleModels();
     return new ResponseEntity<>(models, HttpStatus.OK);
 }

// @GetMapping("/getAllVehicleCompany")
// public ResponseEntity<List<String>> getDistinctVehicleCompany(){
//	 List <String> company =  vehicleService.getDistinctVehicleCompany();
//	 
//	 return new ResponseEntity<>(company, HttpStatus.OK);
// }
 
 }



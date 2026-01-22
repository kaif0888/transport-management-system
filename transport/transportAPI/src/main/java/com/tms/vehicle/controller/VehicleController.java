package com.tms.vehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.service.CSVService;
import com.tms.vehicle.bean.VehicleWithDocumentsRequest;
import com.tms.vehicle.bean.VehicleAvalaibleDropdown;
import com.tms.vehicle.service.VehicleService;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private CSVService csvService;


    /**
     * Create vehicle with all documents in a single request
     */
    @PostMapping("/createVehicleWithDocuments")
    public ResponseEntity<?> createVehicleWithDocuments(@RequestBody VehicleWithDocumentsRequest request) {
        try {
            VehicleWithDocumentsRequest createdVehicle = vehicleService.createVehicleWithDocuments(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create vehicle: " + e.getMessage()));
        }
    }

    /**
     * Update an existing vehicle
     */
    @PutMapping("/updateVehicle/{id}")
    public ResponseEntity<?> updateVehicle(
            @PathVariable("id") String vehicleId,
            @RequestBody VehicleWithDocumentsRequest request) {
        try {
            VehicleWithDocumentsRequest updatedVehicle = vehicleService.updateVehicle(vehicleId, request);
            return ResponseEntity.ok(updatedVehicle);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Get all vehicles grouped by model
     */
    @GetMapping("/vehicles/grouped-by-model")
    public ResponseEntity<Map<String, List<VehicleWithDocumentsRequest>>> getVehiclesGroupedByModel() {
        return ResponseEntity.ok(vehicleService.getVehiclesGroupedByModel());
    }

    /**
     * Get vehicle by ID
     */
    @GetMapping("/getVehicleById/{id}")
    public ResponseEntity<?> getVehicleById(@PathVariable("id") String vehicleId) {
        try {
            VehicleWithDocumentsRequest vehicle = vehicleService.getVehicleById(vehicleId);
            return ResponseEntity.ok(vehicle);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Get all available vehicles (not assigned to any driver)
     */
    @GetMapping("/available")
    public ResponseEntity<List<VehicleAvalaibleDropdown>> getAvailableVehicles() {
        List<VehicleAvalaibleDropdown> availableVehicles = vehicleService.getAvailableVehicles();
        return new ResponseEntity<>(availableVehicles, HttpStatus.OK);
    }

    /**
     * Get all unrented vehicles
     */
    @GetMapping("/unrented")
    public ResponseEntity<List<VehicleAvalaibleDropdown>> getUnrentedVehicles() {
        List<VehicleAvalaibleDropdown> unrentedVehicles = vehicleService.getUnrentedVehicles();
        return new ResponseEntity<>(unrentedVehicles, HttpStatus.OK);
    }

    /**
     * Get all distinct vehicle models
     */
    @GetMapping("/getAllVehicleModels")
    public ResponseEntity<List<String>> getDistinctVehicleModels() {
        List<String> models = vehicleService.getDistinctVehicleModels();
        return new ResponseEntity<>(models, HttpStatus.OK);
    }

    /**
     * Get list of vehicles by filter criteria
     */
    @PostMapping("/getListOfVehiclesByFilterCriteria")
    public ResponseEntity<?> getVehicleByFilterCriteria(@RequestBody FilterRequest request) {
        try {
            int limit = request.getLimit() != null ? request.getLimit() : 100;
            List<VehicleWithDocumentsRequest> vehicles = vehicleService.getvehiclebyfilterCriteria(
                    request.getFilters(), limit);
            return ResponseEntity.ok(vehicles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to filter vehicles: " + e.getMessage()));
        }
    }
    
    /** get list of details of vehicle expiries notifications
     * 
     */
    @GetMapping("/expiries")
    public List<Map<String, Object>> getVehicleExpiries() {
        return vehicleService.getVehicleExpiryList();
    }

    /**
     * Upload vehicles from CSV file
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Please upload a CSV file!"));
        }

        try {
            csvService.saveVehicles(file);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Uploaded the file successfully: " + file.getOriginalFilename()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(Map.of("error", "Could not upload the file: " + file.getOriginalFilename() + "! " + e.getMessage()));
        }
    }
}
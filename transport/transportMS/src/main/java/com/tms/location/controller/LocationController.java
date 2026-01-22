package com.tms.location.controller;
import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.location.bean.LocationBean;
import com.tms.location.service.LocationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/location")
@Api(value = "Location Management System", tags = "Location Management")
public class LocationController {

    @Autowired
    private LocationService locationService;
    
    @PostMapping("/filteredLocations")
    public ResponseEntity<List<LocationBean>> filterLocations(@RequestBody FilterRequest request) {
        try {
            int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
            List<LocationBean> filteredLocations = locationService.filterLocations(request.getFilters(), limit);
            return ResponseEntity.ok(filteredLocations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @ApiOperation(value = "Create location", response = LocationBean.class, notes = "Create a new location.")
    @PostMapping("/createLocation")
    public ResponseEntity<LocationBean> createLocation(@RequestBody LocationBean locationBean) {
        try {
            LocationBean created = locationService.createLocation(locationBean);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get location by ID", response = LocationBean.class, notes = "Fetch location by ID.")
    @GetMapping("/getLocationById/{id}")
    public ResponseEntity<LocationBean> getLocation(@PathVariable("id") Long id) {
        try {
            LocationBean location = locationService.getLocationById(id);
            return ResponseEntity.ok(location);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Get all locations", response = List.class, notes = "Fetch all locations.")
    @GetMapping("/getAllLocations")
    public ResponseEntity<List<LocationBean>> getAllLocations() {
        try {
            List<LocationBean> locations = locationService.getAllLocations();
            return ResponseEntity.ok(locations);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Update location by locationId", response = LocationBean.class, notes = "Update an existing location by locationId.")
    @PutMapping("/updateByLocationId/{id}")
    public ResponseEntity<LocationBean> updateByLocationId(@PathVariable("id") Long id, @RequestBody LocationBean locationBean) {
        try {
            // Set the id from path variable to the bean
            locationBean.setLocationId(id);
            
            LocationBean updated = locationService.updateLocation(locationBean);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @ApiOperation(value = "Delete location by ID", response = Void.class, notes = "Delete a location using its ID.")
    @DeleteMapping("/deleteLocation/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable("id") Long id) {
        try {
            locationService.deleteLocation(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

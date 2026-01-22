package com.tms.branch.controller;

import com.tms.branch.bean.BranchBean;
import com.tms.branch.bean.BranchLocationResponse;
import com.tms.branch.service.BranchService;
import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.location.bean.LocationBean;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/branch")
@Api(value = "Branch Management System", tags = "Branch Management")
public class BranchController {

    @Autowired
    private BranchService branchService;
    
    @PostMapping("/filteredBranchs")
    public ResponseEntity<List<BranchBean>> filterBranchs(@RequestBody FilterRequest request) {
        try {
            int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
            List<BranchBean> filteredBranchs = branchService.filterBranchs(request.getFilters(), limit);
            return ResponseEntity.ok(filteredBranchs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @ApiOperation(value = "Create branch", response = BranchBean.class, notes = "Create a new branch.")
    @PostMapping("/createBranch")
    public ResponseEntity<BranchBean> createBranch(@RequestBody BranchBean branchBean) {
        try {
            BranchBean created = branchService.createBranch(branchBean);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get branch by ID", response = BranchBean.class, notes = "Fetch branch by ID.")
    @GetMapping("/getBranchById/{id}")
    public ResponseEntity<BranchBean> getBranchById(@PathVariable("id") String id) {
        try {
            BranchBean branch = branchService.getBranchById(id);
            return ResponseEntity.ok(branch);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Get all branches", response = List.class, notes = "Fetch all branches.")
    @GetMapping("/getAllBranches")
    public ResponseEntity<List<BranchBean>> getAllBranches() {
        try {
            List<BranchBean> branches = branchService.getAllBranches();
            return ResponseEntity.ok(branches);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @ApiOperation(value = "Get all branches and locations", response = BranchLocationResponse.class, notes = "Fetch all branches aString with locations")
    @GetMapping("/getAllBranchesAndLocations")
    public ResponseEntity<BranchLocationResponse> getAllBranchesAndLocations() {
        try {
            BranchLocationResponse response = branchService.getAllBranchesAndLocations();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @ApiOperation(value = "Update branch by branchId", response = BranchBean.class, notes = "Update an existing branch by branchId.")
    @PutMapping("/updateByBranchId/{id}")
    public ResponseEntity<BranchBean> updateByBranchId(@PathVariable("id") String id, @RequestBody BranchBean branchBean) {
        try {
            // Make sure the branchBean has the correct id
            branchBean.setBranchId(id);
            
            BranchBean updated = branchService.updateBranch(branchBean);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @ApiOperation(value = "Delete branch by ID", response = Void.class, notes = "Delete a branch using its ID.")
    @DeleteMapping("deleteBranch/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable("id") String id) {
        try {
            branchService.deleteBranch(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

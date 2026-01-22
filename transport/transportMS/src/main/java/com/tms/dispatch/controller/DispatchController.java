package com.tms.dispatch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tms.dispatch.bean.DispatchBean;
import com.tms.dispatch.entity.DispatchEntity;
import com.tms.dispatch.service.DispatchService;
import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.order.bean.OrderBean;

import java.util.List;
import java.util.List;
import com.tms.dispatch.bean.DispatchBean;

@RestController
@RequestMapping("/dispatch")
@CrossOrigin(origins = "*")

public class DispatchController {

	@Autowired
	private DispatchService dispatchService;

	@PostMapping("/AddDispatchDetail")
	public DispatchBean createDispatch(@RequestBody DispatchBean dispatchBean) {
		return dispatchService.createDispatch(dispatchBean);

//        {
//        	  "dispatchType": "Urgent",
//        	  "status": "yes",
//        	  "vehicle": {
//        	    "vehicleId": 5
//        	  },
//        	  "driver": {
//        	    "driverId": 29
//        	  }
//        	}
    
 }
     
     
     
     
    
    @GetMapping("/listDispatchDetail")
    public List<DispatchBean> getAllDispatches() {
        return dispatchService.getAllDispatches();
    }
    
    
    
    
    
    
    @PutMapping("/DispatchUpdateBy/{id}")
    public ResponseEntity<DispatchBean> updateDispatchById(
            @PathVariable("id") Long dispatchId,
            @RequestBody DispatchBean dispatchBean) {

	}

	@GetMapping("/listDispatchDetail")
	public List<DispatchBean> getAllDispatches() {
		return dispatchService.getAllDispatches();
	}

	@PutMapping("/DispatchUpdateBy/{id}")
	public ResponseEntity<DispatchBean> updateDispatchById(@PathVariable("id") Integer dispatchId,
			@RequestBody DispatchBean dispatchBean) {

		DispatchBean updatedDispatch = dispatchService.updateDispatchById(dispatchId, dispatchBean);

		if (updatedDispatch != null) {
			return ResponseEntity.ok(updatedDispatch);
		} else {
			return ResponseEntity.notFound().build();
		}

	}
	
	@PostMapping("/listOfDispatchByFilter")
	public ResponseEntity<List<DispatchBean>> listOfDispatchByFilter(@RequestBody FilterRequest request) {
		try {
			int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
			List<DispatchBean> listOfDispatchByFilter = dispatchService.listOfDispatchByFilter(request.getFilters(),
					limit);
			return ResponseEntity.ok(listOfDispatchByFilter);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}
}

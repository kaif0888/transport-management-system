package com.tms.dispatchTracking.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.tms.dispatchTracking.bean.DispatchTrackingBean;
import com.tms.dispatchTracking.dto.OrderTrackingResponse;
import com.tms.dispatchTracking.service.DispatchTrackingService;
//import com.tms.dispatchTracking.serviceImpl.DispatchTrackingServiceImpl;
import com.tms.filter.criteria.bean.FilterRequest;

@RestController
@RequestMapping("/dispatchTracking")
public class DispatchTrackingController {
//	@Autowired
//	private DispatchTrackingServiceImpl dispatchTrackingServiceImpl;

	@Autowired
	DispatchTrackingService dispatchTrackingService;    

	@PostMapping("/createDispatchTracking")
	public List<DispatchTrackingBean> createDispatchTracking(@RequestBody DispatchTrackingBean bean)
	{
		return dispatchTrackingService.createTracking(bean);
	}
	
	@GetMapping("/getDispatchTrackingById/{trackingId}")
	public DispatchTrackingBean getDispatchTrackingById(@PathVariable String trackingId) {
	    DispatchTrackingBean dispatchTrackingBean = new DispatchTrackingBean();
	    dispatchTrackingBean.setTrackingId(trackingId);
	    return dispatchTrackingService.getDispatchTrackingById(dispatchTrackingBean);
	}


	@PutMapping("/updateDispatchTrackingById/{trackingId}")
	public DispatchTrackingBean updateDispatchTrackingById (@PathVariable String trackingId,@RequestBody DispatchTrackingBean dispatchTrackingBean) {
		dispatchTrackingBean.setTrackingId(trackingId);
		return dispatchTrackingService.updateTracking(dispatchTrackingBean);
	}
	
	@PutMapping("/updateTrackingStatusById/{trackingId}")
	public DispatchTrackingBean updateTrackingStatusById(@PathVariable String trackingId)
	{
		return dispatchTrackingService.updateStatus(trackingId);
	}

	@DeleteMapping("/deleteDispatchTrackingById/{trackingId}")
	public String deleteDispatchTrackingBean(@PathVariable String trackingId) {
		return dispatchTrackingService.deleteTracking(trackingId);
	}

	@PostMapping("/listOfDispatchTrackingByFilter")
	public ResponseEntity<List<DispatchTrackingBean>> listOfDispatchTrackingByFilter(
			@RequestBody FilterRequest request) {
		try {
			int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
			List<DispatchTrackingBean> listOfDispatchTrackingByFilter = dispatchTrackingService
					.listOfDispatchTrackingByFilter(request.getFilters(), limit);
			return ResponseEntity.ok(listOfDispatchTrackingByFilter);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}
	
	@GetMapping("/tracking/orders-by-dispatch/{dispatchId}")
	public ResponseEntity<List<DispatchTrackingBean>> getTrackingForOrdersInDispatch(@PathVariable String dispatchId) {
	    List<DispatchTrackingBean> response = dispatchTrackingService.getOrderTrackingByDispatchId(dispatchId);
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/orderStatus/{orderId}")
	public ResponseEntity<List<OrderTrackingResponse>> getOrderTracking(@PathVariable String orderId) {
	    try {
	        List<OrderTrackingResponse> response = dispatchTrackingService.getTrackingByOrderId(orderId);
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
	    }
	}


}

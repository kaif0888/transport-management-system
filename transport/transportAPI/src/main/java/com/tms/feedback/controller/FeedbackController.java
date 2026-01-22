package com.tms.feedback.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.feedback.bean.FeedbackBean;
import com.tms.feedback.service.FeedbackService;
import com.tms.filter.criteria.bean.FilterRequest;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

	@Autowired
	private FeedbackService feedbackserive;

		@PostMapping("/addFeedback")
		public ResponseEntity<Void> registerNewFeedback(@RequestBody FeedbackBean feedbackBean) {
			try {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		        String username = authentication.getName(); 
		         feedbackserive.addCustomerFeedback(feedbackBean,username);
	           return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
	
		}

	@PostMapping("/getListOfFilterFeedback")
	public ResponseEntity<List<FeedbackBean>> getListOfFeedback(@RequestBody FilterRequest request) {
		try {
		return ResponseEntity.ok(feedbackserive.getListOfFilterFeedback(request)); 
		}catch(RuntimeException e) {
			return  ResponseEntity.badRequest().body(null);
		}
		}

	@DeleteMapping("/deleteFeedback/{feedbackId}")
	public ResponseEntity<Void> removeFeedbackById(@PathVariable Integer feedbackId) {
		try { 
		feedbackserive.removeFeedbackById(feedbackId);
	     return new ResponseEntity<>(HttpStatus.OK);
		}catch(RuntimeException e) {
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	   }
		}

}

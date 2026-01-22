package com.tms.payment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.order.bean.OrderBean;
import com.tms.payment.bean.PaymentBean;
import com.tms.payment.service.PaymentService;
import com.tms.payment.service.impl.PaymentServiceImpl;

;

@RestController
@RequestMapping(value = "/payment")
public class PaymentController {

//	@Autowired
//	private PaymentServiceImpl paymentService;

	@Autowired
	PaymentService paymentService;

//	@GetMapping(value = "/getPaymentById/{paymentId}")
//	public PaymentBean getPaymentById(@PathVariable String paymentId) {
//		PaymentBean paymentById = paymentService.getPaymentById(paymentId);
//		return paymentById;
//	}


	@PostMapping(value = "/createPayment")
	public PaymentBean createPayment(@RequestBody PaymentBean paymentBean) {
		PaymentBean payment = paymentService.createPayment(paymentBean);
		return payment;
	}
	
	@PostMapping(value = "/createbyOrderandCustomerPayment")
	public PaymentBean createbyOrderandCustomerPayment(@RequestBody PaymentBean paymentBean) {
		PaymentBean payment = paymentService.createbyOrderandCustomerPayment(paymentBean);
		return payment;
	}
	
	@PostMapping("/listOfPaymentByFilter")
	public ResponseEntity<List<PaymentBean>> listOfPaymentByFilter(@RequestBody FilterRequest request) {
		try {
			int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
			List<PaymentBean> listOfPaymentByFilter = paymentService.listOfPaymentByFilter(request.getFilters(),limit);
			return ResponseEntity.ok(listOfPaymentByFilter);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

}

package com.tms.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.payment.bean.PaymentBean;
import com.tms.payment.service.impl.PaymentServiceImpl;

;

@RestController
@RequestMapping(value = "/payment")
public class PaymentController {

	@Autowired
	private PaymentServiceImpl paymentService;

	@PostMapping(value = "/makePayment")
	public PaymentBean makePayment(@RequestBody PaymentBean paymentBean) {
		PaymentBean payment = paymentService.makePayment(paymentBean);
		return payment;
	}

	@GetMapping(value = "/getPaymentById/{paymentId}")
	public PaymentBean getPaymentById(@PathVariable Long paymentId) {
		PaymentBean paymentById = paymentService.getPaymentById(paymentId);
		return paymentById;
	}

	@DeleteMapping(value = "/deletePayment/{paymentId}")
	public String deletePayment(@PathVariable Long paymentId) {
		String deletePayment = paymentService.deletePayment(paymentId);
		return deletePayment;
	}
}

package com.tms.payment.service;



import com.tms.payment.bean.PaymentBean;

public interface PaymentService {

	PaymentBean makePayment(PaymentBean paymentBean);

	PaymentBean getPaymentById(Long paymentId);



	String deletePayment(Long paymentId);

}

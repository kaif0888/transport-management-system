package com.tms.payment.service;



import java.util.List;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.payment.bean.PaymentBean;

public interface PaymentService {

//	PaymentBean makePayment(PaymentBean paymentBean);
//
//	PaymentBean getPaymentById(String paymentId);
//
//
//
//	String deletePayment(String paymentId);

	PaymentBean createPayment(PaymentBean paymentBean);

	PaymentBean createbyOrderandCustomerPayment(PaymentBean paymentBean);

	List<PaymentBean> listOfPaymentByFilter(List<FilterCriteriaBean> filters, int limit);

}

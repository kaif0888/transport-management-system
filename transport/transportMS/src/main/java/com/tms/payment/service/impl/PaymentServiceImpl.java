package com.tms.payment.service.impl;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.invoice.entity.InvoiceEntity;
import com.tms.invoice.repository.InvoiceRepository;

import com.tms.payment.bean.PaymentBean;
import com.tms.payment.entity.PaymentEntity;
import com.tms.payment.repository.PaymentRepository;
import com.tms.payment.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private PaymentRepository paymentRepository;

//	@Override
//	public PaymentBean makePayment(PaymentBean paymentBean) {
//	// TODO Auto-generated method stub
//		
//		PaymentEntity paymentEntity = new PaymentEntity();
//		BeanUtils.copyProperties(paymentBean, paymentEntity);
//		
//        Long invoiceId = paymentBean.getInvoiceId();
//        InvoiceEntity invoiceEntity = invoiceRepository.findById(invoiceId).isPresent()
//                ? invoiceRepository.findById(invoiceId).get()
//                : null;
//
//        if (invoiceEntity == null) {
//            throw new RuntimeException("Invoice not found with ID: " + invoiceId);
//        }
//
//		
//		PaymentEntity savedEntity = paymentRepository.save(paymentEntity);
//		PaymentBean savedBean = new PaymentBean();
//		BeanUtils.copyProperties(savedEntity, savedBean);
//		return savedBean;
//	}
//	

	@Override
	public PaymentBean makePayment(PaymentBean paymentBean) {
		Long invoiceId = paymentBean.getInvoiceId();

		if (invoiceId == null) {
			throw new IllegalArgumentException("Invoice ID must not be null");
		}

		InvoiceEntity invoiceEntity = invoiceRepository.findById(invoiceId)
				.orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

		PaymentEntity paymentEntity = new PaymentEntity();
		BeanUtils.copyProperties(paymentBean, paymentEntity);

		// ✅ Set the invoice before saving
		paymentEntity.setInvoice(invoiceEntity);

		PaymentEntity savedEntity = paymentRepository.save(paymentEntity);

		PaymentBean savedBean = new PaymentBean();
		BeanUtils.copyProperties(savedEntity, savedBean);
		savedBean.setInvoiceId(invoiceId); // Optional: make sure invoiceId is returned

		return savedBean;
	}

	@Override
	public PaymentBean getPaymentById(Long paymentId) {
		// TODO Auto-generated method stub

		Optional<PaymentEntity> optional = paymentRepository.findById(paymentId);
		PaymentBean paymentBean = null;
		if (optional.isPresent()) {
			PaymentEntity paymentEntity = optional.get();
			paymentBean = new PaymentBean();
			BeanUtils.copyProperties(paymentEntity, paymentBean);

		}
		return paymentBean;
	}

	@Override
	public String deletePayment(Long paymentId) {
		Optional<PaymentEntity> optional = paymentRepository.findById(paymentId);
		if (!optional.isEmpty() && optional.isPresent()) {
			PaymentEntity entity = optional.get();
			
			Optional<InvoiceEntity> optional1 = invoiceRepository.findById(entity.getInvoice().getInvoiceId());
			if(optional1.isPresent()) {
				InvoiceEntity invoiceEntity = optional1.get();
				invoiceRepository.delete(invoiceEntity);
				System.out.println("Delete...........");
			}
			
			paymentRepository.delete(entity);
//	        paymentRepository.deleteById(paymentId);
			return "The payment is deleted";
		}
		return "The payment was not found and could not be deleted";
	}

}

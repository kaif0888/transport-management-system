package com.tms.payment.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tms.customer.entity.CustomerEntity;
import com.tms.customer.repository.CustomerRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.invoice.entity.InvoiceEntity;
import com.tms.invoice.repository.InvoiceRepository;
import com.tms.order.bean.OrderBean;
import com.tms.order.entity.OrderEntity;
import com.tms.order.repository.OrderRepository;
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

	private String generateUniquePaymentId() {
		String prefix = "PAY-";
		String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
		String fullPrefix = prefix + dateStr + "-";

		List<PaymentEntity> todayPayments = paymentRepository.findByPaymentIdStartingWith(fullPrefix);

		int maxSeq = todayPayments.stream().map(p -> p.getPaymentId().substring(fullPrefix.length())).mapToInt(seq -> {
			try {
				return Integer.parseInt(seq);
			} catch (NumberFormatException e) {
				return 0;
			}
		}).max().orElse(0);

		int nextSeq = maxSeq + 1;
		String formattedSeq = String.format("%03d", nextSeq);

		return fullPrefix + formattedSeq;
	}

//
//	@Override
//	public PaymentBean getPaymentById(String paymentId) {
//		// TODO Auto-generated method stub
//
//		Optional<PaymentEntity> optional = paymentRepository.findById(paymentId);
//		PaymentBean paymentBean = null;
//		if (optional.isPresent()) {
//			PaymentEntity paymentEntity = optional.get();
//			paymentBean = new PaymentBean();
//			BeanUtils.copyProperties(paymentEntity, paymentBean);
//
//		}
//		return paymentBean;
//	}
//

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	OrderRepository orderRepository;

	@Override
	public PaymentBean createPayment(PaymentBean paymentBean) {
		String customerId = paymentBean.getCustomerId().toString();

		// 1. Validate customer exists
		customerRepository.findByCustomerId(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));

		// 2. Get total expected payment (from invoice or customer setup)
		Double totalPayment = paymentBean.getTotalPayment();
		if (totalPayment == null) {
			throw new IllegalArgumentException("Total Payment must not be null");
		}

		// 3. Get all previous advance paid by this customer
		Double alreadyPaid = paymentRepository.getTotalAdvancePaidByCustomer(customerId);
		if (alreadyPaid == null) {
			alreadyPaid = 0.0;
		}

		// 4. Add this new payment
		Double currentAdvance = paymentBean.getAdvancePayment();
		if (currentAdvance == null) {
			currentAdvance = 0.0;
		}

		Double newTotalPaid = alreadyPaid + currentAdvance;
		Double remaining = totalPayment - newTotalPaid;

		// 5. Save this payment entry
		PaymentEntity entity = new PaymentEntity();

		String generatedPaymentId = generateUniquePaymentId();
		entity.setPaymentId(generatedPaymentId);

		entity.setPaymentId(paymentBean.getPaymentId());
		entity.setPaymentMethod(paymentBean.getPaymentMethod());
		entity.setPaymentDate(paymentBean.getPaymentDate());
		entity.setAdvancePayment(currentAdvance);
		entity.setTotalPayment(totalPayment); // keep total in each record for tracking
		entity.setRemainingPayment(remaining);
		entity.setCustomerId(paymentBean.getCustomerId());

		entity.setCreatedDate(LocalDateTime.now());
		entity.setLastModifiedDate(LocalDateTime.now());
		entity.setCreatedBy(paymentBean.getCreatedBy());
		entity.setLastModifiedBy(paymentBean.getLastModifiedBy());

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		entity.setCreatedBy(authentication.getName());

		paymentRepository.save(entity);

		// 6. Return updated bean
		paymentBean.setRemainingPayment(remaining);
		return paymentBean;
	}
	
	
	//for order and customer payment both//
	@Override
	public PaymentBean createbyOrderandCustomerPayment(PaymentBean paymentBean) {
	    String orderId = paymentBean.getOrderId();
	    String customerId = paymentBean.getCustomerId();
	    Double thisPayment = paymentBean.getAdvancePayment();

	    if (thisPayment == null || thisPayment <= 0) {
	        throw new IllegalArgumentException("Advance payment must be a positive amount");
	    }

	    double totalAmount;
	    double alreadyPaid;
	    double remaining;

	    OrderEntity order = null;

	    // Payment by Order
	    if (orderId != null && !orderId.isEmpty()) {
	        order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found"));

	        totalAmount = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
	        alreadyPaid = paymentRepository.getTotalPaidForOrder(orderId);
	    }

	    // Payment by Customer (not tied to specific order)
	    else if (customerId != null && !customerId.isEmpty()) {
	        CustomerEntity customer = customerRepository.findByCustomerId(customerId)
	            .orElseThrow(() -> new RuntimeException("Customer not found"));

	        totalAmount = 0.0; // Define this if you calculate customer-level dues
	        alreadyPaid = paymentRepository.getTotalPaidForCustomer(customerId);
	    } else {
	        throw new IllegalArgumentException("Either orderId or customerId must be provided");
	    }

	    remaining = totalAmount - (alreadyPaid + thisPayment);

	    if (remaining < 0) {
	        throw new IllegalArgumentException("Payment exceeds the due amount");
	    }

	    // Save Payment
	    PaymentEntity entity = new PaymentEntity();
	    entity.setCustomerId(customerId);
	    entity.setOrderId(orderId);
	    entity.setAdvancePayment(thisPayment);
	    entity.setTotalPayment(totalAmount);
	    entity.setRemainingPayment(remaining);
	    entity.setPaymentMethod(paymentBean.getPaymentMethod());
	    entity.setPaymentDate(LocalDateTime.now());

	    String paymentId = "PAY-" + System.currentTimeMillis();
	    entity.setPaymentId(paymentId);

	    entity.setCreatedDate(LocalDateTime.now());
	    entity.setLastModifiedDate(LocalDateTime.now());

	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
	    entity.setCreatedBy(username);
	    entity.setLastModifiedBy(username);

	    paymentRepository.save(entity);

	    // ✅ Update paymentStatus in OrderEntity
	    if (order != null) {
	        if (remaining == 0) {
	            order.setPaymentStatus("paid");
	        } else if ((alreadyPaid + thisPayment) > 0) {
	            order.setPaymentStatus("partially-paid");
	        }
	        else {
	            order.setPaymentStatus("un-paid");
	        }
	        orderRepository.save(order);
	    }

	    // Update return bean
	    paymentBean.setTotalPayment(totalAmount);
	    paymentBean.setRemainingPayment(remaining);

	    return paymentBean;
	}

	
//	@Override
//	public PaymentBean createbyOrderandCustomerPayment(PaymentBean paymentBean) {
//	    String orderId = paymentBean.getOrderId();
//	    String customerId = paymentBean.getCustomerId();
//	    Double thisPayment = paymentBean.getAdvancePayment();
//
//	    if (thisPayment == null || thisPayment <= 0) {
//	        throw new IllegalArgumentException("Advance payment must be a positive amount");
//	    }
//
//	    double totalAmount;
//	    double alreadyPaid;
//	    double remaining;
//
//	    // Payment by Order
//	    if (orderId != null && !orderId.isEmpty()) {
//	        OrderEntity order = orderRepository.findById(orderId)
//	            .orElseThrow(() -> new RuntimeException("Order not found"));
//
//	        // Assume you add totalAmount to OrderEntity or calculate from order items
//	        totalAmount = order.getTotalAmount(); // You must have this in OrderEntity
//	        alreadyPaid = paymentRepository.getTotalPaidForOrder(orderId);
//
//	    // Payment by Customer (not tied to specific order)
//	    } else if (customerId != null && !customerId.isEmpty()) {
//	        CustomerEntity customer = customerRepository.findByCustomerId(customerId)
//	            .orElseThrow(() -> new RuntimeException("Customer not found"));
//
//	        totalAmount = 0.0; // Optional: you may define how to calculate customer-level dues
//	        alreadyPaid = paymentRepository.getTotalPaidForCustomer(customerId);
//
//	        // Optional: throw if you want orderId to be mandatory
//	        // throw new IllegalArgumentException("OrderId is required for payments");
//	    } else {
//	        throw new IllegalArgumentException("Either orderId or customerId must be provided");
//	    }
//
//	    remaining = totalAmount - (alreadyPaid + thisPayment);
//
//	    if (remaining < 0) {
//	        throw new IllegalArgumentException("Payment exceeds the due amount");
//	    }
//
//	    // Save Payment
//	    PaymentEntity entity = new PaymentEntity();
//	    entity.setCustomerId(customerId);
//	    entity.setOrderId(orderId);
//	    entity.setAdvancePayment(thisPayment);
//	    entity.setTotalPayment(totalAmount);
//	    entity.setRemainingPayment(remaining);
//	    entity.setPaymentMethod(paymentBean.getPaymentMethod());
//	    entity.setPaymentDate(LocalDateTime.now());
//	    
//	    
//	    String paymentId = "PAY-" + System.currentTimeMillis();
//	    entity.setPaymentId(paymentId); // ✅ Set the ID
//	    
//		entity.setCreatedDate(LocalDateTime.now());
//		entity.setLastModifiedDate(LocalDateTime.now());
//		entity.setCreatedBy(paymentBean.getCreatedBy());
//		entity.setLastModifiedBy(paymentBean.getLastModifiedBy());
//
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		entity.setCreatedBy(authentication.getName());
//
//	    paymentRepository.save(entity);
//
//	    // Update Bean for return
//	    paymentBean.setTotalPayment(totalAmount);
//	    paymentBean.setRemainingPayment(remaining);
//
//	    return paymentBean;
//	}

	@Autowired
	private FilterCriteriaService<PaymentEntity> filterCriteriaService;
	
	@Override
	public List<PaymentBean> listOfPaymentByFilter(List<FilterCriteriaBean> filters, int limit) {
		try {
			List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(PaymentEntity.class, filters, limit);
			return (List<PaymentBean>) filteredEntities.stream().map(entity -> convertToBean((PaymentEntity) entity))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Error filtering Payment: " + e.getMessage());
		}
	}
	
	private PaymentBean convertToBean (PaymentEntity paymentEntity) {
		PaymentBean paymentBean = new PaymentBean();
//		paymentBean.setCustomerId(paymentBean.getCustomer().getCustomerId());
		BeanUtils.copyProperties(paymentEntity, paymentBean);
		return paymentBean;
	}


//	@Override
//	public PaymentBean createPayment(PaymentBean paymentBean) {
//		// 1. Optional: Validate that customer exists
//
//		customerRepository.findByCustomerId(paymentBean.getCustomerId().toString())
//				.orElseThrow(() -> new RuntimeException("Customer not found"));
//
//		// 2. Calculate remaining payment
//		Double total = paymentBean.getTotalPayment();
//		Double advance = paymentBean.getAdvancePayment();
//
//		if (total == null || advance == null) {
//			throw new IllegalArgumentException("Total or Advance payment cannot be null");
//		}
//
//		Double remaining = total - advance;
//
//		// 3. Set calculated remaining in bean
//		paymentBean.setRemainingPayment(remaining);
//
//		// 4. Create and populate entity
//		PaymentEntity entity = new PaymentEntity();
//		entity.setPaymentId(paymentBean.getPaymentId());
//		entity.setPaymentMethod(paymentBean.getPaymentMethod());
//		entity.setPaymentDate(paymentBean.getPaymentDate());
//		entity.setAdvancePayment(advance);
//		entity.setRemainingPayment(remaining);
//		entity.setTotalPayment(total);
//		entity.setCustomerId(paymentBean.getCustomerId());
//
//		// 5. Save to DB
//		paymentRepository.save(entity);
//
//		// 6. Return updated bean
//		return paymentBean;
//	}

}

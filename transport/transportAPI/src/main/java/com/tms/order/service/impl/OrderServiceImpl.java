package com.tms.order.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.customer.entity.CustomerEntity;
import com.tms.customer.repository.CustomerRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.location.entity.LocationEntity;
import com.tms.location.repository.LocationRepository;
import com.tms.mail.service.MailService;
import com.tms.order.bean.OrderBean;
import com.tms.order.entity.OrderEntity;
import com.tms.order.entity.OrderStatusHistoryEntity;
import com.tms.order.repository.OrderRepository;
import com.tms.order.repository.OrderStatusHistoryRepository;
import com.tms.order.service.OrderService;
import com.tms.orderproduct.entity.OrderProductEntity;
import com.tms.orderproduct.repository.OrderProductRepository;
import com.tms.payment.repository.PaymentRepository;
import com.tms.sms.service.SmsService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private OrderProductRepository orderProductRepository;
    @Autowired private OrderStatusHistoryRepository statusHistoryRepository;
    @Autowired private FilterCriteriaService<OrderEntity> filterCriteriaService;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private SmsService smsService;
    @Autowired private MailService mailService;

    private String generateUniqueOrderId() {
        String prefix = "ORD-";
        String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";
        List<OrderEntity> todayOrders = orderRepository.findByOrderIdStartingWith(fullPrefix);

        int maxSeq = todayOrders.stream()
            .map(o -> o.getOrderId().substring(fullPrefix.length()))
            .mapToInt(seq -> {
                try {
                    return Integer.parseInt(seq);
                } catch (NumberFormatException e) {
                    return 0;
                }
            })
            .max().orElse(0);

        return fullPrefix + String.format("%03d", maxSeq + 1);
    }

    @Override
    public OrderBean createOrder(OrderBean orderBean) {
        CustomerEntity customer = customerRepository.findById(orderBean.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        CustomerEntity receiver = customerRepository.findById(orderBean.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        OrderEntity order = convertToEntity(orderBean, customer, receiver);
        order.setOrderId(generateUniqueOrderId());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        order.setCreatedBy(auth.getName());
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
        order.setBranchIds(currentUser.getBranchIds());

        order = orderRepository.save(order);

        // Save status history
        OrderStatusHistoryEntity history = new OrderStatusHistoryEntity();
        history.setOrderId(order.getOrderId());
        history.setStatus(order.getStatus());
        history.setChangedAt(LocalDateTime.now());
        statusHistoryRepository.save(history);

        return convertToBean(order);
    }

    private OrderEntity convertToEntity(OrderBean bean, CustomerEntity customer, CustomerEntity receiver) {
        OrderEntity entity = new OrderEntity();
        entity.setDispatchDate(bean.getDispatchDate());
        entity.setDeliveryDate(bean.getDeliveryDate());
        entity.setStatus(bean.getStatus());
        entity.setPaymentStatus(bean.getPaymentStatus());
        entity.setCustomer(customer);
        entity.setReceiver(receiver);
        entity.setTotalAmount(bean.getTotalAmount());

        if (bean.getOriginlocationId() != null) {
            LocationEntity origin = locationRepository.findById(bean.getOriginlocationId())
                    .orElseThrow(() -> new RuntimeException("Origin location not found"));
            entity.setOriginlocationId(origin);
        }

        if (bean.getDestinationlocationId() != null) {
            LocationEntity dest = locationRepository.findById(bean.getDestinationlocationId())
                    .orElseThrow(() -> new RuntimeException("Destination location not found"));
            entity.setDestinationlocationId(dest);
        }

        entity.setCreatedBy(bean.getCreatedBy());
        entity.setCreatedDate(LocalDateTime.now());
        entity.setLastModifiedBy(bean.getLastModifiedBy());
        entity.setLastModifiedDate(LocalDateTime.now());
        return entity;
    }

    private OrderBean convertToBean(OrderEntity entity) {
        OrderBean bean = new OrderBean();
        BeanUtils.copyProperties(entity, bean);
        if (entity.getCustomer() != null)
            bean.setCustomerId(entity.getCustomer().getCustomerId());
        if (entity.getReceiver() != null)
            bean.setReceiverId(entity.getReceiver().getCustomerId());
        if (entity.getOriginlocationId() != null)
            bean.setOriginlocationId(entity.getOriginlocationId().getLocationId());
        if (entity.getDestinationlocationId() != null)
            bean.setDestinationlocationId(entity.getDestinationlocationId().getLocationId());
        return bean;
    }

    @Override
    public OrderBean getOrderById(String orderId) {
        return convertToBeanWithExtras(orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found")));
    }

    @Override
    public List<OrderBean> getOrdersByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::convertToBean)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<OrderBean> getOrderbyfilterCriteria(List<FilterCriteriaBean> filters, int limit) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        if (!"ADMIN".equalsIgnoreCase(user.getRole().name())) {
            filters.removeIf(f -> f.getAttribute().equalsIgnoreCase("branchIds"));

            FilterCriteriaBean branchFilter = new FilterCriteriaBean();
            branchFilter.setAttribute("branchIds");
            branchFilter.setOperation(FilterOperation.AMONG);
            branchFilter.setValue(user.getBranchIds());
            branchFilter.setValueType(String.class);

            filters.add(branchFilter);
        }

        List<?> filtered = filterCriteriaService.getListOfFilteredData(OrderEntity.class, filters, limit);
        return filtered.stream().map(entity -> convertToBeanWithExtras((OrderEntity) entity)).collect(Collectors.toList());
    }

    private OrderBean convertToBeanWithExtras(OrderEntity orderEntity) {
        OrderBean bean = new OrderBean();
        BeanUtils.copyProperties(orderEntity, bean);
        bean.setOriginlocationId(orderEntity.getOriginlocationId().getLocationId());
        bean.setDestinationlocationId(orderEntity.getDestinationlocationId().getLocationId());

        if (orderEntity.getCustomer() != null) {
            bean.setCustomerId(orderEntity.getCustomer().getCustomerId());
            bean.setCustomerName(orderEntity.getCustomer().getCustomerName());
        }

        if (orderEntity.getReceiver() != null) {
            bean.setReceiverId(orderEntity.getReceiver().getCustomerId());
            bean.setReceiverName(orderEntity.getReceiver().getCustomerName());
        }

        if (orderEntity.getOriginlocationId() != null) {
            bean.setOriginLocationName(orderEntity.getOriginlocationId().getLocationName());
        }

        if (orderEntity.getDestinationlocationId() != null) {
            bean.setDestinationLocationName(orderEntity.getDestinationlocationId().getLocationName());
        }

        Double advance = paymentRepository.getTotalAdvancePaidForOrder(orderEntity.getOrderId());
        Double total = paymentRepository.getTotalPaymentForOrder(orderEntity.getOrderId());

        if (advance == null) advance = 0.0;
        if (total == null || total == 0.0)
            total = orderEntity.getTotalAmount() != null ? orderEntity.getTotalAmount() : 0.0;

        bean.setAdvancePayment(advance);
        bean.setRemainingPayment(total - advance);

        return bean;
    }
    
    @Override
    public void confirmOrderStatus(String orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getCustomer() == null || order.getOriginlocationId() == null ||
            order.getDestinationlocationId() == null || order.getDispatchDate() == null ||
            order.getDeliveryDate() == null) {
            throw new IllegalStateException("Order is incomplete. Required fields are missing.");
        }

        List<OrderProductEntity> products = orderProductRepository.findByOrder_OrderId(orderId);
        boolean hasNullProduct = products.stream().anyMatch(p -> p.getProduct() == null);

        if (hasNullProduct) {
            throw new IllegalStateException("Please add Order Product.");
        }

        if ("CREATED".equalsIgnoreCase(order.getStatus())) {
            order.setStatus("CONFIRM");
            orderRepository.save(order);

            OrderStatusHistoryEntity history = new OrderStatusHistoryEntity();
            history.setOrderId(orderId);
            history.setStatus("CONFIRM");
            history.setChangedAt(LocalDateTime.now());
            statusHistoryRepository.save(history);

            // Send SMS confirmation to customer
            try {
                CustomerEntity customer = order.getCustomer();
                if (customer != null && customer.getCustomerNumber() != null && !customer.getCustomerNumber().isBlank()) {
//                	String trackingUrl = "http://localhost:3000/consignment/track/" + order.getOrderId();
                	String trackingUrl = "http://localhost:3000/publicDispatchTracking/" + order.getOrderId();
                    String message = String.format(
//                        "Hello %s, your consignment %s has been confirmed. Thank you for using our service!",
                    		   "Hello %s, your consignment %s has been confirmed.%n"
                               + "Track your consignment here: %s%n"
                               + "Thank you for using our service!",
                        customer.getCustomerName() != null ? customer.getCustomerName() : "Customer",
                        order.getOrderId(),trackingUrl
                    );
                    smsService.sendSms(customer.getCustomerNumber(), message);
                    System.out.println("Confirmation SMS sent to: " + customer.getCustomerNumber());
                } else {
                    System.err.println("Customer phone number is missing. SMS not sent.");
                }
            } catch (Exception e) {
                System.err.println("Failed to send confirmation SMS: " + e.getMessage());
            }

        } else {
            throw new IllegalStateException("Order has already been confirmed.");
        }
        
        // send mail
        try {
        	 CustomerEntity customer = order.getCustomer();
            if (customer != null && customer.getCustomerEmail() != null && !customer.getCustomerEmail().isBlank()) {
            	String trackingUrl = "http://localhost:3000/publicDispatchTracking/" + order.getOrderId();
                String emailSubject = "consignment Confirmation - " + order.getOrderId();
                String emailBody = String.format(
//                    "Hello %s,\n\nYour consignment with ID %s has been successfully confirmed.\n\nThank you for using our service!",
                		"Hello %s, your consignment %s has been confirmed.%n"
                        + "Track your consignment here: %s%n"
                        + "Thank you for using our service!",
                    customer.getCustomerName() != null ? customer.getCustomerName() : "Customer",
                    order.getOrderId(),
                    trackingUrl
                );
                mailService.sendSimpleEmail(customer.getCustomerEmail(), emailSubject, emailBody);
                System.out.println("Confirmation email sent to: " + customer.getCustomerEmail());
            } else {
                System.err.println("Customer email is missing. Email not sent.");
            }
        } catch (Exception e) {
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }

    }


//    @Override
//    public void confirmOrderStatus(String orderId) {
//        OrderEntity order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
//
//        if (order.getCustomer() == null || order.getOriginlocationId() == null ||
//            order.getDestinationlocationId() == null || order.getDispatchDate() == null ||
//            order.getDeliveryDate() == null) {
//            throw new IllegalStateException("Order is incomplete. Required fields are missing.");
//        }
//
//        List<OrderProductEntity> products = orderProductRepository.findByOrder_OrderId(orderId);
//        boolean hasNullProduct = products.stream().anyMatch(p -> p.getProduct() == null);
//
//        if (hasNullProduct) {
//            throw new IllegalStateException("Please add Order Product.");
//        }
//
//        if ("CREATED".equalsIgnoreCase(order.getStatus())) {
//            order.setStatus("CONFIRM");
//            orderRepository.save(order);
//
//            OrderStatusHistoryEntity history = new OrderStatusHistoryEntity();
//            history.setOrderId(orderId);
//            history.setStatus("CONFIRM");
//            history.setChangedAt(LocalDateTime.now());
//            statusHistoryRepository.save(history);
//        } else {
//            throw new IllegalStateException("Order has already been confirmed.");
//        }
//    }

    @Override
    public OrderBean updateOrderById(String orderId, OrderBean order) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        entity.setCreatedBy(order.getCreatedBy());
        entity.setCreatedDate(order.getCreatedDate());
        entity.setLastModifiedBy(order.getLastModifiedBy());
        entity.setLastModifiedDate(order.getLastModifiedDate());
        entity.setDispatchDate(order.getDispatchDate());
        entity.setDeliveryDate(order.getDeliveryDate());
        entity.setPaymentStatus(order.getPaymentStatus());
        entity.setStatus(order.getStatus());

        if (order.getCustomerId() != null) {
            CustomerEntity customer = customerRepository.findById(order.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            entity.setCustomer(customer);
        }

        if (order.getReceiverId() != null) {
            CustomerEntity receiver = customerRepository.findById(order.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));
            entity.setReceiver(receiver);
        }

        if (order.getOriginlocationId() != null) {
            LocationEntity origin = locationRepository.findById(order.getOriginlocationId())
                    .orElseThrow(() -> new RuntimeException("Origin location not found"));
            entity.setOriginlocationId(origin);
        }

        if (order.getDestinationlocationId() != null) {
            LocationEntity dest = locationRepository.findById(order.getDestinationlocationId())
                    .orElseThrow(() -> new RuntimeException("Destination location not found"));
            entity.setDestinationlocationId(dest);
        }

        OrderEntity saved = orderRepository.save(entity);

        OrderBean bean = new OrderBean();
        BeanUtils.copyProperties(saved, bean);
        bean.setCustomerId(saved.getCustomer().getCustomerId());
        return bean;
    }
}

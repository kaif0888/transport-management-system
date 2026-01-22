package com.tms.dispatchTracking.service.Impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.customer.entity.CustomerEntity;
import com.tms.dispatch.entity.DispatchEntity;
import com.tms.dispatch.repository.DispatchRepository;
import com.tms.dispatchTracking.bean.DispatchTrackingBean;
import com.tms.dispatchTracking.dto.OrderTrackingResponse;
import com.tms.dispatchTracking.entity.DispatchTrackingEntity;
import com.tms.dispatchTracking.repository.DispatchTrackingRepository;
import com.tms.dispatchTracking.service.DispatchTrackingService;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.invoice.bean.InvoiceBean;
import com.tms.invoice.service.InvoiceService;
import com.tms.location.entity.LocationEntity;
import com.tms.location.repository.LocationRepository;
import com.tms.mail.service.MailService;
import com.tms.manifest.entity.ManifestEntity;
import com.tms.manifest.repository.ManifestRepository;
import com.tms.order.entity.OrderEntity;
import com.tms.order.entity.OrderStatusHistoryEntity;
import com.tms.order.repository.OrderRepository;
import com.tms.order.repository.OrderStatusHistoryRepository;
import com.tms.payment.entity.PaymentEntity;
import com.tms.payment.repository.PaymentRepository;
import com.tms.sms.service.SmsService;

@Service
public class DispatchTrackingServiceImpl implements DispatchTrackingService {

    @Autowired
    private DispatchTrackingRepository dispatchTrackingRepo;
    
    @Autowired
    private DispatchRepository dispatchRepo;
    
    @Autowired
    private ManifestRepository manifestRepo;
    
    @Autowired
    private OrderRepository orderRepo;
    
    @Autowired
    private LocationRepository locationRepo;
    
	@Autowired
	UserRepository  userRepository;
	
	@Autowired
	private SmsService smsService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
    private InvoiceService invoiceService;

	
	@Autowired
	private OrderStatusHistoryRepository statusHistoryRepository;


    @Autowired
    private FilterCriteriaService<DispatchTrackingEntity> filterCriteriaService;
    
    private String generateUniqueTrackingId() {
        String prefix = "DT-";
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";

        List<DispatchTrackingEntity> todayTracking = dispatchTrackingRepo.findByTrackingIdStartingWith(fullPrefix);

        int maxSeq = todayTracking.stream()
            .map(t -> t.getTrackingId().substring(fullPrefix.length()))
            .mapToInt(seq -> {
                try {
                    return Integer.parseInt(seq);
                } catch (NumberFormatException e) {
                    return 0;
                }
            })
            .max()
            .orElse(0);

        String formattedSeq = String.format("%03d", maxSeq + 1);
        return fullPrefix + formattedSeq;
    }
    
    @Override
    @Transactional
    public List<DispatchTrackingBean> createTracking(DispatchTrackingBean bean) {
        try {
            String dispatchId = bean.getDispatchId();
            String locationId = bean.getActiveLocation();

            if (dispatchId == null || dispatchId.trim().isEmpty()) {
                throw new IllegalArgumentException("Dispatch ID cannot be null or empty");
            }
            if (locationId == null || locationId.trim().isEmpty()) {
                throw new IllegalArgumentException("Location ID cannot be null or empty");
            }

            // Fetch Dispatch
            DispatchEntity dispatch = dispatchRepo.findById(dispatchId)
                    .orElseThrow(() -> new RuntimeException("Dispatch not found: " + dispatchId));

            // Fetch Manifest
            ManifestEntity manifest = manifestRepo.findByDispatch_DispatchId(dispatchId)
                    .orElseThrow(() -> new RuntimeException("Manifest not found for dispatch: " + dispatchId));

            // Fetch Orders
            List<OrderEntity> orders = manifest.getOrders();
            if (orders == null || orders.isEmpty()) {
                throw new RuntimeException("No orders linked to dispatch: " + dispatchId);
            }

            // Fetch Location
            LocationEntity location = locationRepo.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Invalid location ID: " + locationId));

            // Aggregate Data
            long totalOrders = orders.size();
            long deliveredOrders = orders.stream()
                    .filter(o -> "DELIVERED".equalsIgnoreCase(o.getStatus()))
                    .count();
            long pendingOrders = totalOrders - deliveredOrders;

            // Already Tracked Orders
            Set<String> trackedOrderIds = dispatchTrackingRepo.findByDispatch_DispatchId(dispatchId).stream()
                    .filter(t -> t.getOrder() != null)
                    .map(t -> t.getOrder().getOrderId())
                    .collect(Collectors.toSet());

            List<DispatchTrackingBean> responseBeans = new ArrayList<>();

            // Get Auth Info
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String createdBy = authentication.getName();
            User currentUser = userRepository.findByEmail(createdBy)
                    .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

            // Track Each Order
            for (OrderEntity order : orders) {
                if (trackedOrderIds.contains(order.getOrderId())) {
                    System.out.println("Tracking already exists for order: " + order.getOrderId());
                    continue;
                }

                DispatchTrackingEntity tracking = new DispatchTrackingEntity();
                tracking.setTrackingId(generateUniqueTrackingId());
                tracking.setDispatch(dispatch);
                tracking.setOrder(order);
                tracking.setActiveLocation(location);
                tracking.setTimeStamp(LocalDateTime.now());
                tracking.setStatus("IN-TRANSIT");
                tracking.setTotalOrders(totalOrders);
                tracking.setDeliveredOrders(deliveredOrders);
                tracking.setPendingOrders(pendingOrders);
                tracking.setCreatedBy(createdBy);
                tracking.setBranchIds(currentUser.getBranchIds());

                // Save tracking
                DispatchTrackingEntity saved = dispatchTrackingRepo.save(tracking);

                // Update order status
                order.setStatus("IN-TRANSIT");
        	    //  Save status history after order is saved
        	    OrderStatusHistoryEntity history = new OrderStatusHistoryEntity();
        	    history.setOrderId(order.getOrderId());
        	    history.setStatus("IN-TRANSIT"); 
        	    history.setChangedAt(java.time.LocalDateTime.now());
        	    statusHistoryRepository.save(history);


                // Prepare Response
                DispatchTrackingBean dto = new DispatchTrackingBean();
                dto.setTrackingId(saved.getTrackingId());
                dto.setDispatchId(dispatchId);
                dto.setOrderId(order.getOrderId());
                dto.setActiveLocation(location.getLocationId());
                dto.setTimeStamp(saved.getTimeStamp().toString());
                dto.setStatus(saved.getStatus());
                dto.setTotalOrder(totalOrders);
                dto.setDeliveredOrders(deliveredOrders);
                dto.setPendingOrders(pendingOrders);

                responseBeans.add(dto);

                System.out.println("Created tracking: " + saved.getTrackingId() + " for order: " + order.getOrderId());
            }

            // Persist order status updates
            orderRepo.saveAll(orders);

            if (responseBeans.isEmpty()) {
                System.out.println("No new tracking entries created – all orders already tracked.");
            }

            return responseBeans;

        } catch (Exception e) {
            System.err.println("Error in createTracking: " + e.getMessage());
            throw new RuntimeException("Failed to create tracking: " + e.getMessage(), e);
        }
    }




    @Override
    @Transactional
    public DispatchTrackingBean updateTracking(DispatchTrackingBean bean) {
        try {
            if (bean.getTrackingId() == null || bean.getTrackingId().trim().isEmpty()) {
                throw new IllegalArgumentException("Tracking ID cannot be null or empty");
            }

            Optional<DispatchTrackingEntity> opt = dispatchTrackingRepo.findById(bean.getTrackingId());
            
            if (!opt.isPresent()) {
                throw new RuntimeException("Tracking entry not found with ID: " + bean.getTrackingId());
            }

            DispatchTrackingEntity dispatchTrackingEntity = opt.get();
            
            // Update fields
            if (bean.getStatus() != null) {
                dispatchTrackingEntity.setStatus(bean.getStatus());
            }
            dispatchTrackingEntity.setTimeStamp(LocalDateTime.now());

            // Save updated entity
            DispatchTrackingEntity savedEntity = dispatchTrackingRepo.save(dispatchTrackingEntity);

            // Convert to bean for response
            DispatchTrackingBean responseBean = new DispatchTrackingBean();
            BeanUtils.copyProperties(savedEntity, responseBean);
            
            if (savedEntity.getDispatch() != null) {
                responseBean.setDispatchId(savedEntity.getDispatch().getDispatchId());
            }
            if (savedEntity.getActiveLocation() != null) {
                responseBean.setActiveLocation(savedEntity.getActiveLocation().getLocationId());
            }
            if (savedEntity.getOrder() != null) {
                responseBean.setOrderId(savedEntity.getOrder().getOrderId());
            }
            responseBean.setTimeStamp(savedEntity.getTimeStamp().toString());

            return responseBean;
            
        } catch (Exception e) {
            System.err.println("Error in updateTracking: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update tracking: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DispatchTrackingBean> listOfDispatchTrackingByFilter(List<FilterCriteriaBean> filters, int limit) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
        	if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().name())) {
        	    // Remove any pre-existing branch filter (if present)
        	    filters.removeIf(f -> f.getAttribute().equalsIgnoreCase("branchIds"));

        	    // Convert comma-separated branchIds string to comma-separated string for value
        	    String branchIds = currentUser.getBranchIds(); // e.g., "BR001,BR002"

        	    FilterCriteriaBean branchFilter = new FilterCriteriaBean();
        	    branchFilter.setAttribute("branchIds");
        	    branchFilter.setOperation(FilterOperation.AMONG);
        	    branchFilter.setValue(branchIds);  // Still a comma-separated string
        	    branchFilter.setValueType(String.class); // Optional

        	    filters.add(branchFilter);
        	}
            List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(
                    DispatchTrackingEntity.class, filters, limit);
            return filteredEntities.stream()
                    .map(entity -> convertToBean1((DispatchTrackingEntity) entity))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in listOfDispatchTrackingByFilter: " + e.getMessage());
            throw new RuntimeException("Error filtering Dispatch Tracking: " + e.getMessage());
        }
    }

    private DispatchTrackingBean convertToBean1(DispatchTrackingEntity dispatchTrackingEntity) {
        DispatchTrackingBean dispatchTrackingBean = new DispatchTrackingBean();
        dispatchTrackingBean.setTimeStamp(String.valueOf(dispatchTrackingEntity.getTimeStamp()));
        BeanUtils.copyProperties(dispatchTrackingEntity, dispatchTrackingBean);

        if (dispatchTrackingEntity.getDispatch() != null) {
            dispatchTrackingBean.setDispatchId(dispatchTrackingEntity.getDispatch().getDispatchId());
            dispatchTrackingBean.setVehiclNumber(dispatchTrackingEntity.getDispatch().getVehicle().getVehiclNumber());
            
            // Fetch order counts from manifest
            String dispatchId = dispatchTrackingEntity.getDispatch().getDispatchId();
            Optional<ManifestEntity> manifestOpt = manifestRepo.findByDispatch_DispatchId(dispatchId);
            if (manifestOpt.isPresent()) {
                ManifestEntity manifest = manifestOpt.get();
                List<OrderEntity> orders = manifest.getOrders();
                
                long totalOrders = orders != null ? orders.size() : 0;
                long deliveredOrders = 0;
                long pendingOrders = 0;
                
                if (orders != null) {
                    deliveredOrders = orders.stream()
                        .mapToLong(order -> "DELIVERED".equalsIgnoreCase(order.getStatus()) ? 1 : 0)
                        .sum();
                    pendingOrders = totalOrders - deliveredOrders;
                }

                dispatchTrackingBean.setTotalOrder(totalOrders);
                dispatchTrackingBean.setDeliveredOrders(deliveredOrders);
                dispatchTrackingBean.setPendingOrders(pendingOrders);
            } else {
                dispatchTrackingBean.setTotalOrder(0);
                dispatchTrackingBean.setDeliveredOrders(0);
                dispatchTrackingBean.setPendingOrders(0);
            }
        } else {
            dispatchTrackingBean.setTotalOrder(0);
            dispatchTrackingBean.setDeliveredOrders(0);
            dispatchTrackingBean.setPendingOrders(0);
        }

        if (dispatchTrackingEntity.getActiveLocation() != null) {
            dispatchTrackingBean.setActiveLocation(dispatchTrackingEntity.getActiveLocation().getLocationId());
            dispatchTrackingBean.setActiveLocationName(dispatchTrackingEntity.getActiveLocation().getLocationName());
        }

        if (dispatchTrackingEntity.getOrder() != null) {
            dispatchTrackingBean.setOrderId(dispatchTrackingEntity.getOrder().getOrderId());
        }
        
//        if (dispatchTrackingEntity.getOrder() != null &&
//        	    dispatchTrackingEntity.getOrder().getCustomer() != null) {
//
//        	    CustomerEntity customer =dispatchTrackingEntity.getOrder().getCustomer();
//        	    
//        	    OrderEntity order = dispatchTrackingEntity.getOrder();
//
//        	    dispatchTrackingBean.setCustomerNumber(customer.getCustomerNumber());
//        	    dispatchTrackingBean.setOrderId(order.getOrderId());
//        	    
//        	}
        if (dispatchTrackingEntity.getOrder() != null) {

            OrderEntity order = dispatchTrackingEntity.getOrder();

            // Order ID
            dispatchTrackingBean.setOrderId(order.getOrderId());

            // Receiver details (IMPORTANT)
            if (order.getReceiver() != null) {
                CustomerEntity receiver = order.getReceiver();

                dispatchTrackingBean.setReceiverName(
                    receiver.getCustomerName()
                );

                dispatchTrackingBean.setCustomerNumber(
                    receiver.getCustomerNumber()
                );
            }
        }



        return dispatchTrackingBean;
    }

    @Override
    @Transactional
    public String deleteTracking(String trackingId) {
        try {
            if (trackingId == null || trackingId.trim().isEmpty()) {
                throw new IllegalArgumentException("Tracking ID cannot be null or empty");
            }

            if (!dispatchTrackingRepo.existsById(trackingId)) {
                throw new RuntimeException("Tracking entry not found with ID: " + trackingId);
            }

            dispatchTrackingRepo.deleteById(trackingId);
            return "Tracking entry with ID " + trackingId + " deleted successfully.";
            
        } catch (Exception e) {
            System.err.println("Error in deleteTracking: " + e.getMessage());
            throw new RuntimeException("Failed to delete tracking: " + e.getMessage(), e);
        }
    }
    
    
    
//    @Override
//    @Transactional
//    public DispatchTrackingBean updateStatus(String trackingId) {
//        try {
//            // Fetch tracking entry
//            DispatchTrackingEntity trackingEntity = dispatchTrackingRepo.findById(trackingId)
//                .orElseThrow(() -> new RuntimeException("Tracking ID not found: " + trackingId));
//
//        if (!"reached".equalsIgnoreCase(trackingEntity.getStatus())) {
//            throw new IllegalStateException("Tracking status must be 'reached' to update to 'delivered'.");
//        }
//
//        // Fetch Dispatch from tracking entity
//        DispatchEntity dispatchEntity = trackingEntity.getDispatch();
//        if (dispatchEntity == null) {
//            throw new RuntimeException("Dispatch not found for tracking ID: " + trackingId);
//        }
//
//        String dispatchId = dispatchEntity.getDispatchId();
//
//        // Fetch Manifest using Dispatch
//        ManifestEntity manifestEntity = manifestRepo.findByDispatch_DispatchId(dispatchId)
//            .orElseThrow(() -> new RuntimeException("Manifest not found for dispatch ID: " + dispatchId));
//
//        // Fetch Order using Manifest
//        List<OrderEntity> orders = manifestEntity.getOrders();
//        if (orders == null || orders.isEmpty()) {
//            throw new RuntimeException("No orders found for Manifest ID: " + manifestEntity.getManifestId());
//        }
//
//            LocationEntity activeLocationEntity = trackingEntity.getActiveLocation();
//            if (activeLocationEntity == null) {
//                throw new RuntimeException("Active location is not set in the tracking record.");
//            }
//            String currentLocationId = activeLocationEntity.getLocationId();
//
//            // Update only orders delivered to the current active location
//            for (OrderEntity order : orders) {
//                if ("IN-TRANSIT".equalsIgnoreCase(order.getStatus())) {
//                    order.setStatus("delivered");
//                    order.setDeliveryDate(LocalDate.now());
//                    orderRepo.save(order);
//            	    //  Save status history after order is saved
//            	    OrderStatusHistoryEntity history = new OrderStatusHistoryEntity();
//            	    history.setOrderId(order.getOrderId());
//            	    history.setStatus("DELIVERD"); 
//            	    history.setChangedAt(java.time.LocalDateTime.now());
//            	    statusHistoryRepository.save(history);
//
//                    
//                }
//            }
//
//            // Check if all orders are delivered now
//            boolean allDelivered = orders.stream()
//                .allMatch(order -> "delivered".equalsIgnoreCase(order.getStatus()));
//
//            // Only update dispatch and manifest if ALL orders are delivered
//            if (allDelivered && "In-Transit".equalsIgnoreCase(dispatchEntity.getStatus())) {
//                dispatchEntity.setStatus("Delivered");
//                dispatchRepo.save(dispatchEntity);
//
//                manifestEntity.setDeliveryDate(LocalDate.now());
//                manifestRepo.save(manifestEntity);
//            }
//
//            // Update tracking status
//            trackingEntity.setStatus("delivered");
//            trackingEntity.setTimeStamp(LocalDateTime.now());
//            dispatchTrackingRepo.save(trackingEntity);
//
//            // Return result bean
//            DispatchTrackingBean resultBean = new DispatchTrackingBean();
//            BeanUtils.copyProperties(trackingEntity, resultBean);
//            resultBean.setDispatchId(dispatchId);
//            resultBean.setActiveLocation(currentLocationId);
//            if (trackingEntity.getOrder() != null) {
//                resultBean.setOrderId(trackingEntity.getOrder().getOrderId());
//            }
//            resultBean.setTimeStamp(trackingEntity.getTimeStamp().toString());
//
//            return resultBean;
//            
//        } catch (Exception e) {
//            System.err.println("Error in updateStatus: " + e.getMessage());
//            e.printStackTrace();
//            throw new RuntimeException("Failed to update status: " + e.getMessage(), e);
//        }
//    }
    
    @Autowired
    PaymentRepository paymentRepo;
    
    @Override
    @Transactional
    public DispatchTrackingBean updateStatus(String trackingId) {
        try {
            DispatchTrackingEntity trackingEntity = dispatchTrackingRepo.findById(trackingId)
                .orElseThrow(() -> new RuntimeException("Tracking ID not found: " + trackingId));

            if (!"reached".equalsIgnoreCase(trackingEntity.getStatus())) {
                throw new IllegalStateException("Tracking status must be 'reached' to update to 'delivered'.");
            }

            DispatchEntity dispatchEntity = trackingEntity.getDispatch();
            if (dispatchEntity == null) {
                throw new RuntimeException("Dispatch not found for tracking ID: " + trackingId);
            }

            String dispatchId = dispatchEntity.getDispatchId();
            
            

            ManifestEntity manifestEntity = manifestRepo.findByDispatch_DispatchId(dispatchId)
                .orElseThrow(() -> new RuntimeException("Manifest not found for dispatch ID: " + dispatchId));

            List<OrderEntity> orders = manifestEntity.getOrders();
            if (orders == null || orders.isEmpty()) {
                throw new RuntimeException("No orders found for Manifest ID: " + manifestEntity.getManifestId());
            }

            LocationEntity activeLocationEntity = trackingEntity.getActiveLocation();
            if (activeLocationEntity == null) {
                throw new RuntimeException("Active location is not set in the tracking record.");
            }
            String currentLocationId = activeLocationEntity.getLocationId();

            // Update only the specific order in this tracking entry
            OrderEntity order = trackingEntity.getOrder();
            if (order == null) {
                throw new RuntimeException("No order associated with this tracking entry.");
            }

            if ("IN-TRANSIT".equalsIgnoreCase(order.getStatus())) {
                order.setStatus("DELIVERED");
                order.setDeliveryDate(LocalDate.now());
                orderRepo.save(order);

                OrderStatusHistoryEntity history = new OrderStatusHistoryEntity();
                history.setOrderId(order.getOrderId());
                history.setStatus("DELIVERED");
                history.setChangedAt(LocalDateTime.now());
                statusHistoryRepository.save(history);
                
                // Send SMS notification to customer
                try {
                    CustomerEntity customer = order.getCustomer();
                    if (customer != null && customer.getCustomerNumber() != null && !customer.getCustomerNumber().isBlank()) {
                        String message = String.format(
                            "Hello %s, your consignment %s has been delivered successfully. Thank you!",
                            customer.getCustomerName() != null ? customer.getCustomerName() : "Customer",
                            order.getOrderId()
                        );
                        smsService.sendSms(customer.getCustomerNumber(), message);
                        System.out.println("Delivery SMS sent to: " + customer.getCustomerNumber());
                    } else {
                        System.err.println("Customer phone number is missing. SMS not sent.");
                    }
                } catch (Exception ex) {
                    System.err.println("Failed to send delivery SMS: " + ex.getMessage());
                }
                
                
                // send mail
                try {
                	 CustomerEntity customer = order.getCustomer();
                    if (customer != null && customer.getCustomerEmail() != null && !customer.getCustomerEmail().isBlank()) {
                        String emailSubject = "Consignment Status - " + order.getOrderId();
                        String emailBody = String.format(
                            "Hello %s,\n\nYour consignment with ID %s has been successfully delivered.\n\nThank you for using our service!",
                            customer.getCustomerName() != null ? customer.getCustomerName() : "Customer",
                            order.getOrderId()
                        );
                        mailService.sendSimpleEmail(customer.getCustomerEmail(), emailSubject, emailBody);
                        System.out.println("Delivered email sent to: " + customer.getCustomerEmail());
                    } else {
                        System.err.println("Customer email is missing. Email not sent.");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to send confirmation email: " + e.getMessage());
                }
            }
//           Optional<PaymentEntity>  opt =paymentRepo.findByCustomerId(order.getCustomer().getCustomerId());
//           PaymentEntity payment = opt.get();
//           Double advancePayment = payment.getAdvancePayment();
//           Double   totalPayment = payment.getRemainingPayment();
//           Double remainingPayment = payment.getRemainingPayment();
//           
//           if(advancePayment==totalPayment || remainingPayment==0)
//           {
//        	   
//           }
           
            InvoiceBean invoice = null;
           
           Optional<PaymentEntity> opt = paymentRepo.findByCustomerId(
        	        order.getCustomer().getCustomerId()
        	);

        	boolean isPaymentCompleted = false;

        	if (opt.isPresent()) {
        	    PaymentEntity payment = opt.get();
//        	    Double advancePayment = payment.getAdvancePayment();
//        	    Double totalPayment = payment.getRemainingPayment();
//        	    Double remainingPayment = payment.getRemainingPayment();
//
//        	    isPaymentCompleted =
//        	            (advancePayment != null && totalPayment != null && advancePayment.equals(totalPayment))
//        	            || (remainingPayment != null && remainingPayment == 0);
        	    Double advancePayment = payment.getAdvancePayment();
        	    Double totalPayment = payment.getTotalPayment();
        	    Double remainingPayment = payment.getRemainingPayment();

        	    isPaymentCompleted =
        	        (advancePayment != null && totalPayment != null && advancePayment.equals(totalPayment))
        	        || (remainingPayment != null && remainingPayment == 0);

        	}
        	
        	try {
                PaymentEntity payment = paymentRepo
                    .findByOrder_OrderId(order.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Payment not found for order"));

                invoice = invoiceService.createInvoiceFromPayment(payment.getPaymentId());

                System.out.println("Invoice generated successfully: " + invoice.getInvoiceNumber());

            } catch (Exception ex) {
                System.err.println("Invoice generation failed: " + ex.getMessage());
            }
            
            
           

            // Recalculate delivered and pending orders in manifest
            long deliveredCount = orders.stream()
                .filter(o -> "DELIVERED".equalsIgnoreCase(o.getStatus()))
                .count();
            long pendingCount = orders.size() - deliveredCount;

            manifestEntity.setDeliveredOrders((int) deliveredCount);
            manifestEntity.setPendingOrders((int) pendingCount);

            // If all orders are delivered, mark dispatch and manifest as delivered
            boolean allDelivered = deliveredCount == orders.size();
            if (allDelivered && "IN-TRANSIT".equalsIgnoreCase(dispatchEntity.getStatus()) && isPaymentCompleted) {
                dispatchEntity.setStatus("DELIVERED");
                dispatchRepo.save(dispatchEntity);

                manifestEntity.setDeliveryDate(LocalDate.now());
            }

            manifestRepo.save(manifestEntity);

            //  Update tracking entity status
            trackingEntity.setStatus("DELIVERED");
            trackingEntity.setTimeStamp(LocalDateTime.now());
            dispatchTrackingRepo.save(trackingEntity);

            // Prepare response bean
            DispatchTrackingBean resultBean = new DispatchTrackingBean();
            BeanUtils.copyProperties(trackingEntity, resultBean);
            resultBean.setDispatchId(dispatchId);
            resultBean.setActiveLocation(currentLocationId);
            resultBean.setTimeStamp(trackingEntity.getTimeStamp().toString());
            
            if (invoice != null) {
                resultBean.setInvoiceId(invoice.getInvoiceId());
                resultBean.setInvoiceNumber(invoice.getInvoiceNumber());
                resultBean.setInvoiceGenerated(true);
                resultBean.setInvoicePath("D:/XaltoFiles/XaltoExchange/invoice");

            } else {
                resultBean.setInvoiceGenerated(false);
            }

            if (order != null) {
                resultBean.setOrderId(order.getOrderId());
            }
            resultBean.setTimeStamp(trackingEntity.getTimeStamp().toString());

            return resultBean;

        } catch (Exception e) {
            System.err.println("Error in updateStatus: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update status: " + e.getMessage(), e);
        }
    }

//    @Override
//    @Transactional
//    public DispatchTrackingBean updateStatus(String trackingId) {
//        try {
//            DispatchTrackingEntity trackingEntity = dispatchTrackingRepo.findById(trackingId)
//                .orElseThrow(() -> new RuntimeException("Tracking ID not found: " + trackingId));
//
//            if (!"reached".equalsIgnoreCase(trackingEntity.getStatus())) {
//                throw new IllegalStateException("Tracking status must be 'reached' to update to 'delivered'.");
//            }
//
//            DispatchEntity dispatchEntity = trackingEntity.getDispatch();
//            if (dispatchEntity == null) {
//                throw new RuntimeException("Dispatch not found for tracking ID: " + trackingId);
//            }
//
//            String dispatchId = dispatchEntity.getDispatchId();
//
//            ManifestEntity manifestEntity = manifestRepo.findByDispatch_DispatchId(dispatchId)
//                .orElseThrow(() -> new RuntimeException("Manifest not found for dispatch ID: " + dispatchId));
//
//            List<OrderEntity> orders = manifestEntity.getOrders();
//            if (orders == null || orders.isEmpty()) {
//                throw new RuntimeException("No orders found for Manifest ID: " + manifestEntity.getManifestId());
//            }
//
//            LocationEntity activeLocationEntity = trackingEntity.getActiveLocation();
//            if (activeLocationEntity == null) {
//                throw new RuntimeException("Active location is not set in the tracking record.");
//            }
//            String currentLocationId = activeLocationEntity.getLocationId();
//
//            for (OrderEntity order : orders) {
//                if ("IN-TRANSIT".equalsIgnoreCase(order.getStatus())) {
//                    order.setStatus("DELIVERED");
//                    order.setDeliveryDate(LocalDate.now());
//                    orderRepo.save(order);
//
//                    OrderStatusHistoryEntity history = new OrderStatusHistoryEntity();
//                    history.setOrderId(order.getOrderId());
//                    history.setStatus("DELIVERED");
//                    history.setChangedAt(java.time.LocalDateTime.now());
//                    statusHistoryRepository.save(history);
//                }
//            }
//
//            // ✅ Recalculate delivered and pending orders
//            long deliveredCount = orders.stream()
//                .filter(order -> "DELIVERED".equalsIgnoreCase(order.getStatus()))
//                .count();
//            long pendingCount = orders.size() - deliveredCount;
//
//            manifestEntity.setDeliveredOrders((int) deliveredCount);
//            manifestEntity.setPendingOrders((int) pendingCount);
//
//            // ✅ Update dispatch and manifest if all delivered
//            boolean allDelivered = deliveredCount == orders.size();
//            if (allDelivered && "IN-TRANSIT".equalsIgnoreCase(dispatchEntity.getStatus())) {
//                dispatchEntity.setStatus("DELIVERED");
//                dispatchRepo.save(dispatchEntity);
//
//                manifestEntity.setDeliveryDate(LocalDate.now());
//            }
//
//            manifestRepo.save(manifestEntity);
//
//            // ✅ Update tracking status
//            trackingEntity.setStatus("DELIVERED");
//            trackingEntity.setTimeStamp(LocalDateTime.now());
//            dispatchTrackingRepo.save(trackingEntity);
//
//            // ✅ Return response
//            DispatchTrackingBean resultBean = new DispatchTrackingBean();
//            BeanUtils.copyProperties(trackingEntity, resultBean);
//            resultBean.setDispatchId(dispatchId);
//            resultBean.setActiveLocation(currentLocationId);
//            if (trackingEntity.getOrder() != null) {
//                resultBean.setOrderId(trackingEntity.getOrder().getOrderId());
//            }
//            resultBean.setTimeStamp(trackingEntity.getTimeStamp().toString());
//
//            return resultBean;
//
//        } catch (Exception e) {
//            System.err.println("Error in updateStatus: " + e.getMessage());
//            e.printStackTrace();
//            throw new RuntimeException("Failed to update status: " + e.getMessage(), e);
//        }
//    }


    @Override
    public DispatchTrackingBean getDispatchTrackingById(DispatchTrackingBean inputBean) {
        try {
            if (inputBean.getTrackingId() == null || inputBean.getTrackingId().trim().isEmpty()) {
                throw new IllegalArgumentException("Tracking ID cannot be null or empty");
            }

            Optional<DispatchTrackingEntity> entityOpt = dispatchTrackingRepo.findById(inputBean.getTrackingId());

            if (entityOpt.isEmpty()) {
                throw new RuntimeException("Tracking ID not found: " + inputBean.getTrackingId());
            }

            DispatchTrackingEntity trackingEntity = entityOpt.get();
            DispatchTrackingBean bean = new DispatchTrackingBean();

            // Copy common properties
            BeanUtils.copyProperties(trackingEntity, bean);
            bean.setTimeStamp(trackingEntity.getTimeStamp().toString());

            // Set dispatchId
            String dispatchId = null;
            if (trackingEntity.getDispatch() != null) {
                dispatchId = trackingEntity.getDispatch().getDispatchId();
                bean.setDispatchId(dispatchId);
            }

            // Set active location
            if (trackingEntity.getActiveLocation() != null) {
                bean.setActiveLocation(trackingEntity.getActiveLocation().getLocationId());
            }

            // Set orderId
            if (trackingEntity.getOrder() != null) {
                bean.setOrderId(trackingEntity.getOrder().getOrderId());
            }

            // Fetch order counts based on dispatchId
            if (dispatchId != null) {
                Optional<ManifestEntity> manifestOpt = manifestRepo.findByDispatch_DispatchId(dispatchId);
                if (manifestOpt.isPresent()) {
                    List<OrderEntity> orders = manifestOpt.get().getOrders();

                    long totalOrders = orders != null ? orders.size() : 0;
                    long deliveredOrders = orders != null
                            ? orders.stream().filter(o -> "DELIVERED".equalsIgnoreCase(o.getStatus())).count()
                            : 0;
                    long pendingOrders = totalOrders - deliveredOrders;

                    bean.setTotalOrder(totalOrders);
                    bean.setDeliveredOrders(deliveredOrders);
                    bean.setPendingOrders(pendingOrders);
                } else {
                    bean.setTotalOrder(0);
                    bean.setDeliveredOrders(0);
                    bean.setPendingOrders(0);
                }
            } else {
                bean.setTotalOrder(0);
                bean.setDeliveredOrders(0);
                bean.setPendingOrders(0);
            }

            return bean;
            
        } catch (Exception e) {
            System.err.println("Error in getDispatchTrackingById: " + e.getMessage());
            throw new RuntimeException("Failed to get tracking by ID: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<DispatchTrackingBean> getOrderTrackingByDispatchId(String dispatchId) {
        try {
            if (dispatchId == null || dispatchId.trim().isEmpty()) {
                throw new IllegalArgumentException("Dispatch ID cannot be null or empty");
            }

            // 1. Fetch Manifest by Dispatch ID
            ManifestEntity manifest = manifestRepo.findByDispatch_DispatchId(dispatchId)
                    .orElseThrow(() -> new RuntimeException("Manifest not found for dispatchId: " + dispatchId));

            // 2. Fetch Orders from Manifest
            List<OrderEntity> orders = manifest.getOrders();
            if (orders == null || orders.isEmpty()) {
                throw new RuntimeException("No orders found in manifest for dispatchId: " + dispatchId);
            }

            // 3. Fetch tracking entries for dispatch
            List<DispatchTrackingEntity> trackingEntities = dispatchTrackingRepo.findByDispatch_DispatchId(dispatchId);

            // 4. Map orderId → tracking entity (if tracking exists)
            Map<String, DispatchTrackingEntity> trackingMap = trackingEntities.stream()
                    .filter(e -> e.getOrder() != null)
                    .collect(Collectors.toMap(e -> e.getOrder().getOrderId(), e -> e));

            // 5. Build DTO list
            List<DispatchTrackingBean> response = new ArrayList<>();
            for (OrderEntity order : orders) {
                DispatchTrackingBean bean = new DispatchTrackingBean();
                bean.setDispatchId(dispatchId);
                bean.setOrderId(order.getOrderId());
                bean.setStatus(order.getStatus());

                if (trackingMap.containsKey(order.getOrderId())) {
                    DispatchTrackingEntity tracking = trackingMap.get(order.getOrderId());
                    bean.setTrackingId(tracking.getTrackingId());
                    bean.setTimeStamp(tracking.getTimeStamp().toString());
                    bean.setActiveLocation(tracking.getActiveLocation() != null
                            ? tracking.getActiveLocation().getLocationId()
                            : null);
                } else {
                    // Tracking not yet created for this order
                    bean.setTrackingId(null);
                    bean.setTimeStamp(null);
                    bean.setActiveLocation(null);
                }

                response.add(bean);
            }

            return response;
            
        } catch (Exception e) {
            System.err.println("Error in getOrderTrackingByDispatchId: " + e.getMessage());
            throw new RuntimeException("Failed to get order tracking: " + e.getMessage(), e);
        }
    }

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Override
    public List<OrderTrackingResponse> getTrackingByOrderId(String orderId) {
        List<OrderStatusHistoryEntity> historyList =
            orderStatusHistoryRepository.findByOrderIdOrderByChangedAtAsc(orderId);

        List<OrderTrackingResponse> responses = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (OrderStatusHistoryEntity history : historyList) {
            OrderTrackingResponse response = new OrderTrackingResponse();
            response.setStatus(history.getStatus());
            response.setTimestamp(history.getChangedAt().format(formatter));

            // Set displayStatus & description based on status
            switch (history.getStatus().toUpperCase()) {
            case "PENDING":
                response.setDisplayStatus("Your order is booked");
                response.setDescription("Your order has been received wait for confirmed");
                response.setColor("#FFA500"); // Orange
                break;
            case "CREATED":
                response.setDisplayStatus("Order Created");
                response.setDescription("Your order has been created");
                response.setColor("#00BFFF"); // Deep Sky Blue
                break;
            case "CONFIRM":
                response.setDisplayStatus("Order Confirmed");
                response.setDescription("Your order is confirmed");
                response.setColor("#1E90FF"); // Dodger Blue
                break;
            case "DISPATCHED":
                response.setDisplayStatus("Order Dispatched");
                response.setDescription("Your order has been dispatched");
                response.setColor("#6A5ACD"); // Slate Blue
                break;
            case "IN-TRANSIT":
                response.setDisplayStatus("In Transit");
                response.setDescription("Your order is on the way");
                response.setColor("#20B2AA"); // Light Sea Green
                break;
            case "DELIVERED":
                response.setDisplayStatus("Delivered");
                response.setDescription("Your order has been delivered successfully");
                response.setColor("#32CD32"); // Lime Green
                break;
            default:
                response.setDisplayStatus("Unknown");
                response.setDescription("No description available");
                response.setColor("#A9A9A9"); // Dark Gray for unknown
        }


            responses.add(response);
        }

        return responses;
    }



    
}

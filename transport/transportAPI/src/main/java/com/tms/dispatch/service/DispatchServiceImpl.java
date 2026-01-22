package com.tms.dispatch.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
import com.tms.dispatch.bean.DispatchBean;
import com.tms.dispatch.entity.DispatchEntity;
import com.tms.dispatch.repository.DispatchRepository;
import com.tms.dispatchTracking.bean.DispatchTrackingBean;
import com.tms.dispatchTracking.service.DispatchTrackingService;
import com.tms.driver.entity.DriverEntity;
import com.tms.driver.repository.DriverRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.mail.service.MailService;
import com.tms.manifest.entity.ManifestEntity;
import com.tms.manifest.repository.ManifestRepository;
import com.tms.order.entity.OrderEntity;
import com.tms.sms.service.SmsService;
import com.tms.vehicle.entity.VehicleEntity;
import com.tms.vehicle.repository.VehicleRepository;

@Service
public class DispatchServiceImpl implements DispatchService {

    @Autowired private DispatchRepository dispatchRepository;
    @Autowired private DriverRepository driverRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ManifestRepository manifestRepo;
    @Autowired private DispatchTrackingService dispatchTrackingService;
    @Autowired private FilterCriteriaService<DispatchEntity> filterCriteriaService;
    @Autowired private SmsService smsService;
    @Autowired private MailService mailService;

    private final Object dispatchIdLock = new Object();

    private String generateUniqueId() {
        String prefix = "DISP-";
        String dateStr = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";

        synchronized (dispatchIdLock) {
            List<DispatchEntity> todayDispatches = dispatchRepository.findByDispatchIdStartingWith(fullPrefix);
            int maxSeq = todayDispatches.stream()
                    .map(d -> d.getDispatchId().substring(fullPrefix.length()))
                    .filter(seq -> seq.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .max().orElse(0);
            return fullPrefix + String.format("%03d", maxSeq + 1);
        }
    }

    @Override
    public DispatchBean createDispatch(DispatchBean dispatchBean) {
        DriverEntity driver = driverRepository.findById(dispatchBean.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        VehicleEntity vehicle = vehicleRepository.findById(dispatchBean.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        DispatchEntity dispatch = new DispatchEntity();
        BeanUtils.copyProperties(dispatchBean, dispatch);
        dispatch.setDispatchId(generateUniqueId());
        dispatch.setDriver(driver);
        dispatch.setVehicle(vehicle);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        dispatch.setCreatedBy(username);

        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
        dispatch.setBranchIds(currentUser.getBranchIds());

        DispatchEntity saved = dispatchRepository.save(dispatch);

        DispatchBean response = convertToDispatchBean(saved);
        return response;
    }

    @Override
    public List<DispatchBean> getAllDispatches() {
        return dispatchRepository.findAll().stream()
                .map(this::convertToDispatchBean)
                .collect(Collectors.toList());
    }

    @Override
    public DispatchBean updateDispatchById(String dispatchId, DispatchBean dispatchBean) {
        DispatchEntity dispatch = dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new RuntimeException("Dispatch not found"));

        DriverEntity driver = driverRepository.findById(dispatchBean.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        VehicleEntity vehicle = vehicleRepository.findById(dispatchBean.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        BeanUtils.copyProperties(dispatchBean, dispatch, "dispatchId");
        dispatch.setDriver(driver);
        dispatch.setVehicle(vehicle);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        dispatch.setLastModifiedBy(auth.getName());

        DispatchEntity updated = dispatchRepository.save(dispatch);
        return convertToDispatchBean(updated);
    }

    @Override
    public List<DispatchBean> listOfDispatchByFilter(List<FilterCriteriaBean> filters, int limit) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().name())) {
                filters.removeIf(f -> f.getAttribute().equalsIgnoreCase("branchIds"));
                FilterCriteriaBean branchFilter = new FilterCriteriaBean();
                branchFilter.setAttribute("branchIds");
                branchFilter.setOperation(FilterOperation.AMONG);
                branchFilter.setValue(currentUser.getBranchIds());
                branchFilter.setValueType(String.class);
                filters.add(branchFilter);
            }

            List<?> entities = filterCriteriaService.getListOfFilteredData(DispatchEntity.class, filters, limit);
            return entities.stream()
                    .map(e -> convertToDispatchBean((DispatchEntity) e))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error filtering Dispatch: " + e.getMessage());
        }
    }

    private DispatchBean convertToDispatchBean(DispatchEntity entity) {
        DispatchBean bean = new DispatchBean();
        bean.setDispatchId(entity.getDispatchId());
        bean.setDispatchType(entity.getDispatchType());
        bean.setStatus(entity.getStatus());
        bean.setCreatedBy(entity.getCreatedBy());
        bean.setLastModifiedBy(entity.getLastModifiedBy());
        bean.setLastModifiedDate(entity.getLastModifiedDate());

        if (entity.getVehicle() != null) {
            bean.setVehicleId(entity.getVehicle().getVehicleId());
            bean.setModel(entity.getVehicle().getModel());
            bean.setRegistrationNumber(entity.getVehicle().getRegistrationNumber());
            bean.setVehiclNumber(entity.getVehicle().getVehiclNumber());
        }

        if (entity.getDriver() != null) {
            bean.setDriverId(entity.getDriver().getDriverId());
            bean.setDriverName(entity.getDriver().getName());
        }

        return bean;
    }

    @Override
    @Transactional
    public DispatchBean dispatchUpdateByDriverPiksUp(String dispatchId, String activeLocation) {
        DispatchEntity dispatch = dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new RuntimeException("Dispatch not found: " + dispatchId));

        if ("In-Transit".equalsIgnoreCase(dispatch.getStatus())) {
            throw new RuntimeException("Dispatch is already marked as In-Transit");
        }

        DispatchTrackingBean trackingBean = new DispatchTrackingBean();
        trackingBean.setDispatchId(dispatchId);
        trackingBean.setActiveLocation(activeLocation);
        trackingBean.setStatus("In-Transit");

        List<DispatchTrackingBean> createdTracking = dispatchTrackingService.createTracking(trackingBean);
        if (createdTracking.isEmpty()) {
            throw new RuntimeException("Tracking creation failed or no orders found");
        }

        dispatch.setStatus("In-Transit");
        DispatchEntity updated = dispatchRepository.save(dispatch);
        

        //  Send SMS to customers for each order in this dispatch
        try {
            Optional<ManifestEntity> manifestOpt = manifestRepo.findByDispatch_DispatchId(dispatchId);
             ManifestEntity manifest = manifestOpt.get();
                List<OrderEntity> orders = manifest.getOrders();
                for (OrderEntity order : orders) {
                    CustomerEntity customer = order.getCustomer();
                    if (customer != null && customer.getCustomerNumber() != null && !customer.getCustomerNumber().isBlank()) {
                        String message = String.format(
                            "Hello %s, your order %s is now In-Transit. We'll notify you once it's delivered.",
                            customer.getCustomerName() != null ? customer.getCustomerName() : "Customer",
                            order.getOrderId()
                        );
                        smsService.sendSms(customer.getCustomerNumber(), message);
                        System.out.println("In-Transit SMS sent to: " + customer.getCustomerNumber());
                    }
                }
            
        } catch (Exception ex) {
            System.err.println("Failed to send In-Transit SMS: " + ex.getMessage());
        }
        
        
     // send mail
        try {
        	  Optional<ManifestEntity> manifestOpt = manifestRepo.findByDispatch_DispatchId(dispatchId);
              ManifestEntity manifest = manifestOpt.get();
              List<OrderEntity> orders = manifest.getOrders();
              for (OrderEntity order : orders) {
        	 CustomerEntity customer = order.getCustomer();
            if (customer != null && customer.getCustomerEmail() != null && !customer.getCustomerEmail().isBlank()) {
                String emailSubject = "Consignment Status - " + order.getOrderId();
                String emailBody = String.format(
                    "Hello %s,\n\nYour consignment with ID %s is now In-Transit. We'll notify you once it's delivered..\n\nThank you for using our service!",
                    customer.getCustomerName() != null ? customer.getCustomerName() : "Customer",
                    order.getOrderId()
                );
                mailService.sendSimpleEmail(customer.getCustomerEmail(), emailSubject, emailBody);
                System.out.println("Delivered email sent to: " + customer.getCustomerEmail());
            } else {
                System.err.println("Customer email is missing. Email not sent.");
            }
        }
        }catch (Exception e) {
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }

        return convertToDispatchBean(updated);
    }
}

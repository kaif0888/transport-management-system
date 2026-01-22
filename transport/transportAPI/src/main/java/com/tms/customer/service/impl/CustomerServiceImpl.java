package com.tms.customer.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.customer.bean.CustomerBean;
import com.tms.customer.entity.CustomerEntity;
import com.tms.customer.repository.CustomerRepository;
import com.tms.customer.service.CustomerService;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.location.bean.LocationBean;
import com.tms.location.entity.LocationEntity;
import com.tms.location.repository.LocationRepository;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FilterCriteriaService<CustomerEntity> filterCriteriaService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LocationRepository locationRepository;

    /**
     * Generates a unique customer ID with format: CUST-YYYYMMDD-XXX
     * Example: CUST-20250111-001
     */
    private String generateCustomerId() {
        String prefix = "CUST-";
        String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";

        List<CustomerEntity> todayCustomers = customerRepository.findByCustomerIdStartingWith(fullPrefix);

        int maxSeq = todayCustomers.stream()
                .map(c -> c.getCustomerId().substring(fullPrefix.length()))
                .mapToInt(seq -> {
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
    
    /**
     * Generates a unique location ID with format: LOC-YYYYMMDD-XXX
     * Example: LOC-20250111-001
     */
    private String generateLocationId() {
        String prefix = "LOC";
        String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";

        List<LocationEntity> todayLocations = locationRepository.findByLocationIdStartingWith(fullPrefix);

        int maxSeq = todayLocations.stream()
            .map(loc -> loc.getLocationId().substring(fullPrefix.length()))
            .mapToInt(seq -> {
                try {
                    return Integer.parseInt(seq);
                } catch (NumberFormatException e) {
                    return 0;
                }
            })
            .max()
            .orElse(0);

        int nextSeq = maxSeq + 1;
        String formattedSeq = String.format("%03d", nextSeq);

        return fullPrefix + formattedSeq;
    }

    @Override
    public CustomerBean createCustomer(CustomerBean customerBean) {
        CustomerEntity customerEntity = new CustomerEntity();

        // Generate unique customer ID
        String generatedCustomerId = generateCustomerId();
        customerEntity.setCustomerId(generatedCustomerId);

        // Set basic customer information
        customerEntity.setCustomerName(customerBean.getCustomerName());
        customerEntity.setCustomerInfo(customerBean.getCustomerInfo());
        customerEntity.setCustomerNumber(customerBean.getCustomerNumber());
        customerEntity.setCustomerEmail(customerBean.getCustomerEmail());
        
        // Find or create billing address location
        LocationEntity billingLocationEntity = findOrCreateLocation(customerBean.getBillingAddress());
        customerEntity.setBillingAddress(billingLocationEntity);
        
        // Find or create shipping address location
        LocationEntity shippingLocationEntity = findOrCreateLocation(customerBean.getShippingAddress());
        customerEntity.setShippingAddress(shippingLocationEntity);
        
        // Set local addresses (detailed text addresses)
        customerEntity.setLocalBillingAddress(customerBean.getLocalBillingAddress());
        customerEntity.setLocalShippingAddress(customerBean.getLocalShippingAddress());

        // Set audit fields
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        customerEntity.setCreatedBy(authentication.getName());
        customerEntity.setLastModifiedBy(authentication.getName());
        customerEntity.setLastModifiedDate(LocalDateTime.now());

        // Get branchId from current authenticated user
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
        customerEntity.setBranchIds(currentUser.getBranchIds());

        // Save to database
        customerRepository.save(customerEntity);

        // Set generated ID back to bean for response
        customerBean.setCustomerId(customerEntity.getCustomerId());

        return customerBean;
    }

    @Override
    public CustomerBean getCustomerById(String customerId) {
        Optional<CustomerEntity> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            return convertToBean(optionalCustomer.get());
        } else {
            throw new RuntimeException("Customer not found with ID: " + customerId);
        }
    }

    @Override
    public CustomerBean updateCustomer(CustomerBean customerBean) {
        if (customerBean.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID cannot be null for update");
        }

        Optional<CustomerEntity> existingCustomerOpt = customerRepository.findById(customerBean.getCustomerId());
        if (existingCustomerOpt.isPresent()) {
            CustomerEntity existingCustomer = existingCustomerOpt.get();

            // Update basic customer information
            existingCustomer.setCustomerName(customerBean.getCustomerName());
            existingCustomer.setCustomerInfo(customerBean.getCustomerInfo());
            existingCustomer.setCustomerNumber(customerBean.getCustomerNumber());
            existingCustomer.setCustomerEmail(customerBean.getCustomerEmail());
            
            // Find or create billing address location
            LocationEntity billingLocationEntity = findOrCreateLocation(customerBean.getBillingAddress());
            existingCustomer.setBillingAddress(billingLocationEntity);
            
            // Find or create shipping address location
            LocationEntity shippingLocationEntity = findOrCreateLocation(customerBean.getShippingAddress());
            existingCustomer.setShippingAddress(shippingLocationEntity);
            
            // Update local addresses (detailed text addresses)
            existingCustomer.setLocalBillingAddress(customerBean.getLocalBillingAddress());
            existingCustomer.setLocalShippingAddress(customerBean.getLocalShippingAddress());

            // Update audit fields
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            existingCustomer.setLastModifiedBy(authentication.getName());
            existingCustomer.setLastModifiedDate(LocalDateTime.now());

            // Save updated entity
            CustomerEntity updatedEntity = customerRepository.save(existingCustomer);
            return convertToBean(updatedEntity);
        } else {
            throw new RuntimeException("Customer not found with ID: " + customerBean.getCustomerId());
        }
    }

    @Override
    public void deleteCustomer(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new RuntimeException("Customer not found with ID: " + customerId);
        }
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<CustomerBean> filterCustomers(List<FilterCriteriaBean> filters, int limit) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
                
            // Apply branch filter for non-admin users
            if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().name())) {
                // Remove any pre-existing branch filter (if present)
                filters.removeIf(f -> f.getAttribute().equalsIgnoreCase("branchIds"));

                // Convert comma-separated branchIds string to comma-separated string for value
                String branchIds = currentUser.getBranchIds(); // e.g., "BR001,BR002"

                FilterCriteriaBean branchFilter = new FilterCriteriaBean();
                branchFilter.setAttribute("branchIds");
                branchFilter.setOperation(FilterOperation.AMONG);
                branchFilter.setValue(branchIds);  // Still a comma-separated string
                branchFilter.setValueType(String.class);

                filters.add(branchFilter);
            }
            
            // Execute filter query
            @SuppressWarnings("unchecked")
            List<CustomerEntity> filteredEntities = (List<CustomerEntity>) filterCriteriaService
                    .getListOfFilteredData(CustomerEntity.class, filters, limit);

            // Convert entities to beans and return
            return filteredEntities.stream()
                    .map(this::convertToBean)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error filtering customers: " + e.getMessage(), e);
        }
    }

    // ==================== Location Management Methods ====================
    
    /**
     * Finds an existing location or creates a new one
     * Search criteria: pincode + locationArea + district + state
     * @param bean The LocationBean with location details
     * @return LocationEntity (existing or newly created)
     */
    private LocationEntity findOrCreateLocation(LocationBean bean) {
        if (bean == null) {
            return null;
        }
        
        // Normalize values for comparison
        String pincode = normalizeString(bean.getPincode());
        String locationArea = normalizeString(bean.getLocationArea());
        String district = normalizeString(bean.getDistrict());
        String state = normalizeString(bean.getState());
        
        // Search for existing location with same pincode, area, district, and state
        List<LocationEntity> existingLocations = locationRepository
            .findByPincodeAndLocationAreaAndDistrictAndState(pincode, locationArea, district, state);
        
        if (!existingLocations.isEmpty()) {
            // Return the first matching location
            System.out.println("Found existing location: " + existingLocations.get(0).getLocationId());
            return existingLocations.get(0);
        }
        
        // No existing location found, create a new one
        System.out.println("Creating new location for pincode: " + pincode);
        return createNewLocation(bean);
    }
    
    /**
     * Creates a new location entity
     * @param bean The LocationBean with location details
     * @return Newly created LocationEntity
     */
    private LocationEntity createNewLocation(LocationBean bean) {
        LocationEntity locationEntity = new LocationEntity();
        
        // Generate unique location ID
        String uniqueId = generateLocationId();
        locationEntity.setLocationId(uniqueId);
        
        // Set location details
        locationEntity.setLocationArea(bean.getLocationArea());
        locationEntity.setLocationAddress(bean.getLocationAddress());
        locationEntity.setPincode(bean.getPincode());
        locationEntity.setState(bean.getState());
        locationEntity.setCircle(bean.getCircle());
        locationEntity.setDistrict(bean.getDistrict());
        locationEntity.setBlock(bean.getBlock());
        locationEntity.setCountry(bean.getCountry());
        locationEntity.setStatus("Active");
        
        // Generate location name
        String generatedLocationName = buildLocationName(bean);
        locationEntity.setLocationName(generatedLocationName);
        
        // Set audit fields
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        locationEntity.setCreatedBy(authentication.getName());
        locationEntity.setCreatedDate(LocalDateTime.now());
        locationEntity.setLastModifiedBy(authentication.getName());
        locationEntity.setLastModifiedDate(LocalDateTime.now());
        
        // Get branchId from current authenticated user
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
        locationEntity.setBranchIds(currentUser.getBranchIds());
        
        // Save and return
        return locationRepository.save(locationEntity);
    }
    
    /**
     * Builds a formatted location name from location details
     * Format: Area, Block, Circle, District (State) - Pincode
     * @param bean The LocationBean
     * @return Formatted location name string
     */
    private String buildLocationName(LocationBean bean) {
        StringBuilder name = new StringBuilder();
        
        if (isNotEmpty(bean.getLocationArea())) {
            name.append(bean.getLocationArea());
        }
        
        if (isNotEmpty(bean.getBlock())) {
            if (name.length() > 0) name.append(", ");
            name.append(bean.getBlock());
        }
        
        if (isNotEmpty(bean.getCircle())) {
            if (name.length() > 0) name.append(", ");
            name.append(bean.getCircle());
        }
        
        if (isNotEmpty(bean.getDistrict())) {
            if (name.length() > 0) name.append(", ");
            name.append(bean.getDistrict());
        }
        
        if (isNotEmpty(bean.getState())) {
            if (name.length() > 0) name.append(" (");
            name.append(bean.getState()).append(")");
        }
        
        if (isNotEmpty(bean.getPincode())) {
            if (name.length() > 0) name.append(" - ");
            name.append(bean.getPincode());
        }
        
        return name.toString();
    }
    
    /**
     * Normalizes a string for comparison (trims and converts to lowercase)
     * @param value The string to normalize
     * @return Normalized string or empty string if null
     */
    private String normalizeString(String value) {
        return value != null ? value.trim().toLowerCase() : "";
    }
    
    /**
     * Checks if a string is not empty
     * @param value The string to check
     * @return true if not null and not empty after trimming
     */
    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // ==================== Conversion Helper Methods ====================
    
    /**
     * Converts LocationEntity to LocationBean
     * @param entity The LocationEntity to convert
     * @return LocationBean or null if entity is null
     */
    private LocationBean convertLocationToBean(LocationEntity entity) {
        if (entity == null) {
            return null;
        }
        
        LocationBean bean = new LocationBean();
        bean.setLocationId(entity.getLocationId());
        bean.setLocationName(entity.getLocationName());
        bean.setLocationArea(entity.getLocationArea());
        bean.setLocationAddress(entity.getLocationAddress());
        bean.setPincode(entity.getPincode());
        bean.setState(entity.getState());
        bean.setStatus(entity.getStatus());
        bean.setCircle(entity.getCircle());
        bean.setDistrict(entity.getDistrict());
        bean.setBlock(entity.getBlock());
        bean.setCountry(entity.getCountry());
        
        return bean;
    }

    /**
     * Converts CustomerEntity to CustomerBean
     * @param entity The CustomerEntity to convert
     * @return CustomerBean with all fields mapped
     */
    private CustomerBean convertToBean(CustomerEntity entity) {
        CustomerBean bean = new CustomerBean();

        // Set basic customer information
        bean.setCustomerId(entity.getCustomerId());
        bean.setCustomerName(entity.getCustomerName());
        bean.setCustomerInfo(entity.getCustomerInfo());
        bean.setCustomerNumber(entity.getCustomerNumber());
        bean.setCustomerEmail(entity.getCustomerEmail());
        
        // Convert location entities to beans
        bean.setBillingAddress(convertLocationToBean(entity.getBillingAddress()));
        bean.setShippingAddress(convertLocationToBean(entity.getShippingAddress()));
        
        // Set local addresses (detailed text addresses)
        bean.setLocalBillingAddress(entity.getLocalBillingAddress());
        bean.setLocalShippingAddress(entity.getLocalShippingAddress());
        
        // Set audit fields
        bean.setCreatedBy(entity.getCreatedBy());
        bean.setCreatedDate(entity.getCreatedDate());
        bean.setLastModifiedBy(entity.getLastModifiedBy());
        bean.setLastModifiedDate(entity.getLastModifiedDate());

        return bean;
    }
}
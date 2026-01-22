package com.tms.bookingCost.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.bookingCost.bean.BookingCostBean;
import com.tms.bookingCost.entity.BookingCostEntity;
import com.tms.bookingCost.repository.BookingCostRepository;
import com.tms.bookingCost.sevice.BookingCostService;
import com.tms.customer.entity.CustomerEntity;
import com.tms.customer.repository.CustomerRepository;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.location.entity.LocationEntity;
import com.tms.location.repository.LocationRepository;
import com.tms.order.entity.OrderEntity;
import com.tms.order.repository.OrderRepository;


@Service
public class BookingCostServiceImpl implements BookingCostService {

    @Autowired
    private BookingCostRepository bookingCostRepo;

    @Autowired
    private OrderRepository orderRepo;
    
    @Autowired
    private CustomerRepository customerRepo;
    
    @Autowired
    private LocationRepository locationRepo;
    
	@Autowired
	UserRepository  userRepository;
    
    private String generateUniqueId() {
        String prefix = "BOOK-COST-";
        String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";

        // Fetch all Details with today's prefix and find the highest sequence number
        List<BookingCostEntity> todayDetails = bookingCostRepo.findByBookingCostIdStartingWith(fullPrefix);

        int maxSeq = todayDetails.stream()
            .map(c -> c.getBookingCostId().substring(fullPrefix.length()))
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
    public BookingCostBean createBookingCost(BookingCostBean bookingCostBean) {
       

       
        CustomerEntity customerEntity = customerRepo.findById(bookingCostBean.getCustomerId())
        		.orElseThrow(() -> new RuntimeException("Customer not found"));
        
        LocationEntity originEntity = locationRepo.findById(bookingCostBean.getOriginId())
        		.orElseThrow(() -> new RuntimeException("Orign not found"));
        
        LocationEntity destinationEntity = locationRepo.findById(bookingCostBean.getDestinationId())
        		.orElseThrow(() -> new RuntimeException("Destination not found"));
        
        OrderEntity orderEntity = orderRepo.findById(bookingCostBean.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: "));

        BookingCostEntity bookingCostEntity = new BookingCostEntity();
        BeanUtils.copyProperties(bookingCostBean, bookingCostEntity);

        // Generate and set the unique BookingCost ID
        String generatedId = generateUniqueId();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        bookingCostEntity.setCreatedBy(authentication.getName());
   	 // Get branchId from current authenticated user
	    User currentUser = userRepository.findByEmail(authentication.getName())
	            .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
	    bookingCostEntity.setBranchIds(currentUser.getBranchIds());
        
        
        
        bookingCostEntity.setBookingCostId(generatedId);
        bookingCostEntity.setCustomer(customerEntity);
        bookingCostEntity.setOrigin(originEntity);
        bookingCostEntity.setDestination(destinationEntity);
       
        
        bookingCostEntity.setOrder(orderEntity);

        BookingCostEntity savedEntity = bookingCostRepo.save(bookingCostEntity);

        BookingCostBean savedBean = new BookingCostBean();
        BeanUtils.copyProperties(savedEntity, savedBean);
        
        savedBean.setOrderId(savedEntity.getOrder().getOrderId());
        savedBean.setCustomerId(savedEntity.getCustomer().getCustomerId());
        savedBean.setOriginId(savedEntity.getOrigin().getLocationId());
        savedBean.setDestinationId(savedEntity.getDestination().getLocationId());

        return savedBean;
    }

    @Override
    public List<BookingCostBean> listBookingCost() {
        List<BookingCostEntity> entities = bookingCostRepo.findAll();
        List<BookingCostBean> beans = new ArrayList<>();

        for (BookingCostEntity entity : entities) {
            BookingCostBean bookingCostBean = new BookingCostBean();
            BeanUtils.copyProperties(entity, bookingCostBean);
         
            
            if (entity.getOrder() != null) {
    	        bookingCostBean.setOrderId(entity.getOrder().getOrderId());
    	    }
            
            if(entity.getCustomer() != null)
            {
            	bookingCostBean.setCustomerId(entity.getCustomer().getCustomerId());
            }
            
            if(entity.getOrigin() != null)
            {
            	bookingCostBean.setOriginId(entity.getOrigin().getLocationId());
            }
            
            if(entity.getDestination() != null)
            {
            	bookingCostBean.setDestinationId(entity.getDestination().getLocationId());
            }
            beans.add(bookingCostBean);
        }

        return beans;
    }

    @Override
    public BookingCostBean getByBookingCostId(String bookingCostId) {
        Optional<BookingCostEntity> opt = bookingCostRepo.findById(bookingCostId);
        if (opt.isPresent()) {
            BookingCostEntity entity = opt.get();
            BookingCostBean bean = new BookingCostBean();
            BeanUtils.copyProperties(entity, bean);
          
            if (entity.getOrder() != null) {
            	bean.setOrderId(entity.getOrder().getOrderId());
    	    }
            
            if(entity.getCustomer() != null)
            {
            	bean.setCustomerId(entity.getCustomer().getCustomerId());
            }
            
            if(entity.getOrigin() != null)
            {
            	bean.setOriginId(entity.getOrigin().getLocationId());
            }
            
            if(entity.getDestination() != null)
            {
            	bean.setDestinationId(entity.getDestination().getLocationId());
            }
            return bean;
        }
        return null;
    }

    @Override
    public BookingCostBean updateBookingCost(BookingCostBean bookingCostBean) {
        Optional<BookingCostEntity> opt = bookingCostRepo.findById(bookingCostBean.getBookingCostId());
        if (opt.isPresent()) {
            BookingCostEntity entity = opt.get();

            entity.setAmount(bookingCostBean.getAmount());
        
            entity.setBookingDate(bookingCostBean.getBookingDate());

         
             
            OrderEntity orderEntity = orderRepo.findById(bookingCostBean.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: "));
            

            CustomerEntity customerEntity = customerRepo.findById(bookingCostBean.getCustomerId())
            		.orElseThrow(() -> new RuntimeException("Customer not found"));
            
            LocationEntity originEntity = locationRepo.findById(bookingCostBean.getOriginId())
            		.orElseThrow(() -> new RuntimeException("Orign not found"));
            
            LocationEntity destinationEntity = locationRepo.findById(bookingCostBean.getDestinationId())
            		.orElseThrow(() -> new RuntimeException("Destination not found"));
            
            
            entity.setOrder(orderEntity);
            entity.setCustomer(customerEntity);
            entity.setDestination(destinationEntity);
            entity.setOrigin(originEntity);
            
            BookingCostEntity savedEntity = bookingCostRepo.save(entity);

            BookingCostBean bean = new BookingCostBean();
            BeanUtils.copyProperties(savedEntity, bean);
            
            
            if (savedEntity.getOrder() != null) {
            	bean.setOrderId(entity.getOrder().getOrderId());
    	    }
            return bean;
        }
        return null;
    }

    @Override
    public String deleteBookingCost(String bookingCostId) {
        if (bookingCostId != null && bookingCostRepo.existsById(bookingCostId)) {
            bookingCostRepo.deleteById(bookingCostId);
            return "The BookingCost has been deleted.";
        }
        return "The BookingCost was not found or could not be deleted.";
    }
    
    @Autowired
   	private FilterCriteriaService<BookingCostEntity> filterCriteriaService;

	@Override
	public List<BookingCostBean> listOfBookingCostByFilter(List<FilterCriteriaBean> filters, int limit) {
		try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            String username = authentication.getName();
//
//            User currentUser = userRepository.findByEmail(username)
//                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
//        	if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().name())) {
//        	    // Remove any pre-existing branch filter (if present)
//        	    filters.removeIf(f -> f.getAttribute().equalsIgnoreCase("branchIds"));
//
//        	    // Convert comma-separated branchIds string to comma-separated string for value
//        	    String branchIds = currentUser.getBranchIds(); // e.g., "BR001,BR002"
//
//        	    FilterCriteriaBean branchFilter = new FilterCriteriaBean();
//        	    branchFilter.setAttribute("branchIds");
//        	    branchFilter.setOperation(FilterOperation.AMONG);
//        	    branchFilter.setValue(branchIds);  // Still a comma-separated string
//        	    branchFilter.setValueType(String.class); // Optional
//
//        	    filters.add(branchFilter);
//        	}
			List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(BookingCostEntity.class, filters,
					limit);
			return (List<BookingCostBean>) filteredEntities.stream()
					.map(entity -> convertToBean((BookingCostEntity) entity)).collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Error filtering Booking: " + e.getMessage());
		}
	}
	
	private BookingCostBean convertToBean(BookingCostEntity bookingCostEntity) {
		BookingCostBean bookingCostBean = new BookingCostBean();
		bookingCostBean.setBookingCostId(bookingCostEntity.getBookingCostId());
	    bookingCostBean.setAmount(bookingCostEntity.getAmount());
	    bookingCostBean.setBookingDate(bookingCostEntity.getBookingDate());
	    bookingCostBean.setDeliveryDate(bookingCostEntity.getDeliveryDate());
	    bookingCostBean.setBranchIds(bookingCostEntity.getBranchIds());
	 
	    
	    if (bookingCostEntity.getOrder() != null) {
	        bookingCostBean.setOrderId(bookingCostEntity.getOrder().getOrderId());
	    }
	    
	    if(bookingCostEntity.getCustomer() != null)
        {
        	bookingCostBean.setCustomerId(bookingCostEntity.getCustomer().getCustomerId());
        }
        
        if(bookingCostEntity.getOrigin() != null)
        {
        	bookingCostBean.setOriginId(bookingCostEntity.getOrigin().getLocationId());
        }
        
        if(bookingCostEntity.getDestination() != null)
        {
        	bookingCostBean.setDestinationId(bookingCostEntity.getDestination().getLocationId());
        }

	
		return bookingCostBean;
	}
}

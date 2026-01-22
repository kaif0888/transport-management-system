package com.tms.manifest.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.dispatch.entity.DispatchEntity;
import com.tms.dispatch.repository.DispatchRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.location.entity.LocationEntity;
import com.tms.location.repository.LocationRepository;
import com.tms.manifest.bean.ManifestBean;
import com.tms.manifest.entity.ManifestEntity;
import com.tms.manifest.repository.ManifestRepository;
import com.tms.manifest.service.ManifestService;
import com.tms.order.bean.OrderBean;
import com.tms.order.entity.OrderEntity;
import com.tms.order.entity.OrderStatusHistoryEntity;
import com.tms.order.repository.OrderRepository;
import com.tms.order.repository.OrderStatusHistoryRepository;

import jakarta.transaction.Transactional;

@Service
public class ManifestServiceImpl implements ManifestService {
	@Autowired
	private ManifestRepository manifestRepo;

	@Autowired
	private DispatchRepository dispatchRepo;

//    @Autowired
//    private ProductRepository productRepo;

	@Autowired
	private LocationRepository locationRepo;

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private OrderStatusHistoryRepository statusHistoryRepository;

	@Autowired
	private FilterCriteriaService<ManifestEntity> filterCriteriaService;

	private String generateUniqueManifestId() {
		String prefix = "MANI-FEST-"; // Manifest prefix
		String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
		String fullPrefix = prefix + dateStr + "-";

		// Fetch all Manifest entities with today's prefix
		List<ManifestEntity> todayManifests = manifestRepo.findByManifestIdStartingWith(fullPrefix);

		int maxSeq = todayManifests.stream().map(m -> m.getManifestId().substring(fullPrefix.length()))
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

	@Override
	@Transactional
	public ManifestBean createManifest(ManifestBean manifestBean) {
		if (manifestBean == null) {
			throw new IllegalArgumentException("ManifestBean must not be null");
		}

		String manifestId = generateUniqueManifestId();

		String dispatchId = manifestBean.getDispatchId();
		if (dispatchId == null) {
			throw new IllegalArgumentException("Dispatch Id must not be null");
		}

		DispatchEntity dispatch = dispatchRepo.findById(dispatchId)
				.orElseThrow(() -> new RuntimeException("Dispatch not found: " + dispatchId));

		List<String> orderIds = manifestBean.getOrderIds();
		if (orderIds == null || orderIds.isEmpty()) {
			throw new IllegalArgumentException("Order Ids must not be null or empty");
		}

		List<OrderEntity> orders = orderRepo.findAllById(orderIds);
		if (orders.size() != orderIds.size()) {
			throw new RuntimeException("One or more Order IDs are invalid");
		}

		// Set order status to DISPATCHED and save status history
		for (OrderEntity order : orders) {
			order.setStatus("DISPATCHED");

			OrderStatusHistoryEntity history = new OrderStatusHistoryEntity();
			history.setOrderId(order.getOrderId());
			history.setStatus("DISPATCHED");
			history.setChangedAt(java.time.LocalDateTime.now());

			statusHistoryRepository.save(history);
		}
		orderRepo.saveAll(orders);

		LocationEntity startLocation = locationRepo.findById(manifestBean.getStartLocationId()).orElseThrow(
				() -> new RuntimeException("Start location not found: " + manifestBean.getStartLocationId()));

//		LocationEntity endLocation = locationRepo.findById(manifestBean.getEndLocationId())
//				.orElseThrow(() -> new RuntimeException("End location not found: " + manifestBean.getEndLocationId()));

		ManifestEntity entity = new ManifestEntity();
		entity.setManifestId(manifestId);
		entity.setDispatch(dispatch);
		entity.setOrders(orders);
		entity.setStartLocation(startLocation);
//		entity.setEndLocation(endLocation);
//		entity.setDeliveryDate(manifestBean.getDeliveryDate());

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String createdBy = authentication.getName();
		entity.setCreatedBy(createdBy);

		User currentUser = userRepository.findByEmail(createdBy)
				.orElseThrow(() -> new RuntimeException("Logged-in user not found"));
		entity.setBranchIds(currentUser.getBranchIds());

		// ✅ Set only total orders at this point
		entity.setTotalOrders(orders.size());
		entity.setPendingOrders(0); // Will be updated when manifest is delivered
		entity.setDeliveredOrders(0);

		ManifestEntity saved = manifestRepo.save(entity);

		ManifestBean savedBean = new ManifestBean();
		savedBean.setManifestId(saved.getManifestId());
		savedBean.setDispatchId(dispatchId);
		savedBean.setOrderIds(orderIds);
		savedBean.setStartLocationId(startLocation.getLocationId());
//		savedBean.setEndLocationId(endLocation.getLocationId());
//		savedBean.setDeliveryDate(saved.getDeliveryDate());

		savedBean.setTotalOrders(orders.size());
		savedBean.setPendingOrders(0); // default
		savedBean.setDeliveredOrders(0); // default

		return savedBean;
	}

	@Override
	public List<ManifestBean> listManifestBean() {
		List<ManifestEntity> entities = manifestRepo.findAll();
		List<ManifestBean> beans = new ArrayList<>();

		for (ManifestEntity entity : entities) {
			ManifestBean bean = new ManifestBean();
			bean.setManifestId(entity.getManifestId());

			if (entity.getDispatch() != null) {
				bean.setDispatchId(entity.getDispatch().getDispatchId());
			}

//		        if (entity.getProducts() != null) {
//		        	List<String> productIds = entity.getProducts().stream()
//		                    .map(ProductEntity::getProductId)
//		                    .collect(Collectors.toList()); 
//
//		            bean.setProductIds(productIds);
//		        }

			if (entity.getOrders() != null) {
				List<String> orderIds = entity.getOrders().stream().map(OrderEntity::getOrderId)
						.collect(Collectors.toList());

				bean.setOrderIds(orderIds);
			}

			if (entity.getStartLocation() != null) {
				bean.setStartLocationId(entity.getStartLocation().getLocationId());
			}
//
//			if (entity.getEndLocation() != null) {
//				bean.setEndLocationId(entity.getEndLocation().getLocationId());
//			}

//		        if(entity.getOrder() != null)
//		        {
//		        	bean.setOrderId(entity.getOrder().getOrderId());
//		        }

//			bean.setDeliveryDate(entity.getDeliveryDate());

			beans.add(bean);
		}

		return beans;
	}

	@Override
	public ManifestBean updateManifestBean(ManifestBean manifestBean) {
		ManifestEntity entity = manifestRepo.findById(manifestBean.getManifestId())
				.orElseThrow(() -> new RuntimeException("Manifest not found with ID: " + manifestBean.getManifestId()));

		if (manifestBean.getStartLocationId() != null) {
			LocationEntity start = locationRepo.findById(manifestBean.getStartLocationId())
					.orElseThrow(() -> new RuntimeException("Start Location not found"));
			entity.setStartLocation(start);
		}

//		if (manifestBean.getEndLocationId() != null) {
//			LocationEntity end = locationRepo.findById(manifestBean.getEndLocationId())
//					.orElseThrow(() -> new RuntimeException("End Location not found"));
//			entity.setEndLocation(end);
//		}

//		    if (manifestBean.getProductIds() != null && !manifestBean.getProductIds().isEmpty()) {
//		        List<ProductEntity> products = productRepo.findAllById(manifestBean.getProductIds());
//		        entity.setProducts(products);
//		    }

		if (manifestBean.getOrderIds() != null && !manifestBean.getOrderIds().isEmpty()) {
			List<OrderEntity> orders = orderRepo.findAllById(manifestBean.getOrderIds());
			entity.setOrders(orders);
		}

//		entity.setDeliveryDate(manifestBean.getDeliveryDate());

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		entity.setLastModifiedBy(authentication.getName());

		ManifestEntity updated = manifestRepo.save(entity);

		ManifestBean updatedBean = new ManifestBean();
		updatedBean.setManifestId(updated.getManifestId());
		updatedBean.setDispatchId(updated.getDispatch().getDispatchId());

//		    updatedBean.setProductIds(
//		        updated.getProducts().stream()
//		            .map(ProductEntity::getProductId)
//		            .collect(Collectors.toList())
//		    );
		updatedBean.setOrderIds(updated.getOrders().stream().map(OrderEntity::getOrderId).collect(Collectors.toList()));

		updatedBean.setStartLocationId(updated.getStartLocation().getLocationId());
//		updatedBean.setEndLocationId(updated.getEndLocation().getLocationId());
//		updatedBean.setDeliveryDate(updated.getDeliveryDate());

		return updatedBean;
	}

	@Override
	public ManifestBean getManifestById(String manifestId) {
		Optional<ManifestEntity> manifestEntityOpt = manifestRepo.findById(manifestId);
		ManifestBean bean = new ManifestBean();

		if (manifestEntityOpt.isPresent()) {
			ManifestEntity entity = manifestEntityOpt.get();

			// Set basic manifest details
			bean.setManifestId(entity.getManifestId());
//			bean.setDeliveryDate(entity.getDeliveryDate());

			if (entity.getDispatch() != null) {
				bean.setDispatchId(entity.getDispatch().getDispatchId());
			}

			if (entity.getStartLocation() != null) {
				bean.setStartLocationId(entity.getStartLocation().getLocationId());
				bean.setStartLocationName(entity.getStartLocation().getLocationName());
			}

//			if (entity.getEndLocation() != null) {
//				bean.setEndLocationId(entity.getEndLocation().getLocationId());
//				bean.setEndLocationName(entity.getEndLocation().getLocationName());
//			}

			// Convert OrderEntities to OrderBeans
			List<OrderEntity> orderEntities = entity.getOrders();
			List<OrderBean> orderBeans = new ArrayList<>();
			List<String> orderIds = new ArrayList<>();
			for (OrderEntity order : orderEntities) {
			    OrderBean orderBean = new OrderBean();

			    // Direct field mappings
			    orderBean.setOrderId(order.getOrderId());
			    orderBean.setDispatchDate(order.getDispatchDate());
			    orderBean.setDeliveryDate(order.getDeliveryDate());
			    orderBean.setCreatedDate(order.getCreatedDate());
			    orderBean.setStatus(order.getStatus());
			    orderBean.setPaymentStatus(order.getPaymentStatus());
			    orderBean.setTotalAmount(order.getTotalAmount());
		

			    // Customer details
			    if (order.getCustomer() != null) {
			        orderBean.setCustomerId(order.getCustomer().getCustomerId());
			        orderBean.setCustomerName(order.getCustomer().getCustomerName()); // Assuming getCustomerName() exists
			    }

			    // Receiver details

		        if (order.getReceiver() != null) {
		        	orderBean.setReceiverId(order.getReceiver().getCustomerId());
		        	orderBean.setReceiverName(order.getReceiver().getCustomerName());
		        }

			    // Origin Location
			    if (order.getOriginlocationId() != null) {
			        orderBean.setOriginlocationId(order.getOriginlocationId().getLocationId());
			        orderBean.setOriginLocationName(order.getOriginlocationId().getLocationName());
			    }

//			    // Destination Location
//			    if (order.getDestinationlocationId() != null) {
//			        orderBean.setDestinationlocationId(order.getDestinationlocationId().getLocationId());
//			        orderBean.setDestinationLocationName(order.getDestinationlocationId().getLocationName());
//			    }

			    orderBeans.add(orderBean);
			    orderIds.add(order.getOrderId());
			}


//			for (OrderEntity order : orderEntities) {
//				OrderBean orderBean = new OrderBean();
//				orderBean.setOrderId(order.getOrderId());
//				orderBean.setCustomerId(order.getCustomer().getCustomerId());
//				if (order.getOriginlocationId() != null) {
//					orderBean.setOriginlocationId(order.getOriginlocationId().getLocationId());
//				}
//
//				if (order.getDestinationlocationId() != null) {
//					orderBean.setDestinationlocationId(order.getDestinationlocationId().getLocationId());
//				}
//				orderBean.setDispatchDate(order.getDispatchDate());
//				orderBean.setDeliveryDate(order.getDeliveryDate());
//				orderBean.setStatus(order.getStatus());
//				orderBean.setPaymentStatus(order.getPaymentStatus());
//
//				orderBeans.add(orderBean);
//				orderIds.add(order.getOrderId());
//			}

			bean.setOrders(orderBeans);
			bean.setOrderIds(orderIds);
		}

		return bean;
	}

	@Override
	public ManifestBean updateManifestById(String manifestId, ManifestBean manifestBean) {
		ManifestEntity existingManifest = manifestRepo.findById(manifestId)
				.orElseThrow(() -> new RuntimeException("Manifest not found with ID: " + manifestId));

		if (manifestBean.getDispatchId() != null) {
			DispatchEntity dispatch = dispatchRepo.findById(manifestBean.getDispatchId()).orElseThrow(
					() -> new RuntimeException("Dispatch not found with ID: " + manifestBean.getDispatchId()));
			existingManifest.setDispatch(dispatch);
		}

		if (manifestBean.getStartLocationId() != null) {
			LocationEntity startLocation = locationRepo.findById(manifestBean.getStartLocationId())
					.orElseThrow(() -> new RuntimeException(
							"Start location not found with ID: " + manifestBean.getStartLocationId()));
			existingManifest.setStartLocation(startLocation);
		}

		if (manifestBean.getEndLocationId() != null) {
			LocationEntity endLocation = locationRepo.findById(manifestBean.getEndLocationId()).orElseThrow(
					() -> new RuntimeException("End location not found with ID: " + manifestBean.getEndLocationId()));
			existingManifest.setEndLocation(endLocation);
		}

		if (manifestBean.getOrderIds() != null && !manifestBean.getOrderIds().isEmpty()) {
			List<OrderEntity> orders = orderRepo.findAllById(manifestBean.getOrderIds());
			if (orders.size() != manifestBean.getOrderIds().size()) {
				throw new RuntimeException("One or more order IDs are invalid.");
			}
			existingManifest.setOrders(orders);
		}

		if (manifestBean.getDeliveryDate() != null) {
			existingManifest.setDeliveryDate(manifestBean.getDeliveryDate());
		}

		ManifestEntity updatedEntity = manifestRepo.save(existingManifest);

		ManifestBean updatedBean = new ManifestBean();
		updatedBean.setManifestId(updatedEntity.getManifestId());
		updatedBean.setDispatchId(updatedEntity.getDispatch().getDispatchId());
		updatedBean.setStartLocationId(updatedEntity.getStartLocation().getLocationId());
//		updatedBean.setEndLocationId(updatedEntity.getEndLocation().getLocationId());
//		updatedBean.setDeliveryDate(updatedEntity.getDeliveryDate());
		updatedBean.setOrderIds(
				updatedEntity.getOrders().stream().map(OrderEntity::getOrderId).collect(Collectors.toList()));
		return updatedBean;
	}

	@Override
	public List<ManifestBean> filterManifests(List<FilterCriteriaBean> filterCriteriaList, int maxResults) {
		try {
//			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//			String username = authentication.getName();
//
//			User currentUser = userRepository.findByEmail(username)
//					.orElseThrow(() -> new RuntimeException("Logged-in user not found"));
//			if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().name())) {
//				// Remove any pre-existing branch filter (if present)
//				filterCriteriaList.removeIf(f -> f.getAttribute().equalsIgnoreCase("branchIds"));
//
//				// Convert comma-separated branchIds string to comma-separated string for value
//				String branchIds = currentUser.getBranchIds(); // e.g., "BR001,BR002"
//
//				FilterCriteriaBean branchFilter = new FilterCriteriaBean();
//				branchFilter.setAttribute("branchIds");
//				branchFilter.setOperation(FilterOperation.AMONG);
//				branchFilter.setValue(branchIds); // Still a comma-separated string
//				branchFilter.setValueType(String.class); // Optional
//
//				filterCriteriaList.add(branchFilter);
//			}
			List<ManifestEntity> filteredEntities = (List<ManifestEntity>) filterCriteriaService
					.getListOfFilteredData(ManifestEntity.class, filterCriteriaList, maxResults);

			return filteredEntities.stream().map(this::convertToBean).collect(Collectors.toList());

		} catch (Exception e) {
			throw new RuntimeException("Error while filtering manifests: " + e.getMessage(), e);
		}
	}

	private ManifestBean convertToBean(ManifestEntity entity) {
		ManifestBean bean = new ManifestBean();

		bean.setManifestId(entity.getManifestId());

		if (entity.getDispatch() != null) {
			bean.setDispatchId(entity.getDispatch().getDispatchId());
			bean.setVehiclNumber(entity.getDispatch().getVehicle().getVehiclNumber());
		}

		if (entity.getOrders() != null) {
			List<String> orderIds = entity.getOrders().stream().map(OrderEntity::getOrderId)
					.collect(Collectors.toList());
			bean.setOrderIds(orderIds);
		}

		if (entity.getStartLocation() != null) {
			bean.setStartLocationId(entity.getStartLocation().getLocationId());
			bean.setStartLocationName(entity.getStartLocation().getLocationName());
		}
//
//		if (entity.getEndLocation() != null) {
//			bean.setEndLocationId(entity.getEndLocation().getLocationId());
//			bean.setEndLocationName(entity.getEndLocation().getLocationName());
//		}
//		
//		bean.setDeliveryDate(entity.getDeliveryDate());
		bean.setLastModifiedBy(entity.getLastModifiedBy());
		bean.setCreatedBy(entity.getCreatedBy());
		bean.setLastModifiedDate(entity.getLastModifiedDate());

		return bean;
	}

	@Override
	public String deleteManifestById(String manifestId) {
		// TODO Auto-generated method stub
		if (manifestId != null) {
			manifestRepo.deleteById(manifestId);
			return "Manifest of id :" + manifestId + " has been deleted";
		}
		return "ManifestId cannot be null";
	}

}

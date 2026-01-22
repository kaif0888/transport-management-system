package com.tms.orderproduct.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.tms.boxes.entity.HSNCodeEntity;
import com.tms.boxes.repository.HSNCodeRepository;
import com.tms.customer.entity.CustomerEntity;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.mail.service.MailService;
import com.tms.order.entity.OrderEntity;
import com.tms.order.entity.OrderStatusHistoryEntity;
import com.tms.order.repository.OrderRepository;
import com.tms.order.repository.OrderStatusHistoryRepository;
import com.tms.product.bean.ProductBean;
import com.tms.product.entity.ProductEntity;
import com.tms.product.repository.ProductRepository;
import com.tms.sms.service.SmsService;
import com.tms.orderproduct.bean.OrderProductBean;
import com.tms.orderproduct.entity.OrderProductEntity;
import com.tms.orderproduct.repository.OrderProductRepository;
import com.tms.orderproduct.service.OrderProductService;

import jakarta.transaction.Transactional;

@Service
public class OrderProductServiceImpl implements OrderProductService {

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmsService smsService;

    @Autowired
    private MailService mailService;

    @Autowired
    private OrderStatusHistoryRepository statusHistoryRepository;

    @Autowired
    private FilterCriteriaService<OrderProductEntity> filterCriteriaService;

    /* =========================================================
       ID GENERATOR
       ========================================================= */
    private synchronized String generateUniqueOrderProductId() {
        String prefix = "ORD-PD";
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";

        List<OrderProductEntity> todayList =
                orderProductRepository.findByOrderProductIdStartingWith(fullPrefix);

        int maxSeq = todayList.stream()
                .map(e -> e.getOrderProductId().substring(fullPrefix.length()))
                .mapToInt(seq -> {
                    try {
                        return Integer.parseInt(seq);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);

        return fullPrefix + String.format("%03d", maxSeq + 1);
    }
    @Autowired
    HSNCodeRepository hsnCodeRepository;

    /* =========================================================
       SAVE ORDER PRODUCTS (MAIN METHOD)
       ========================================================= */
    @Override
    @Transactional
    public OrderProductBean saveOrderProduct(OrderProductBean bean) {

        if (bean.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID must not be null");
        }
    
        OrderEntity order = orderRepository.findById(bean.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
       

        if (bean.getProducts() == null || bean.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Product list is empty");
        }

       
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        List<ProductBean> savedProducts = new ArrayList<>();

        List<ProductEntity>productent= new ArrayList<>();
        for (ProductBean productBean : bean.getProducts()) {
        	ProductEntity pro= new ProductEntity();
        	BeanUtils.copyProperties(productBean, pro);
                productent.add(pro);
            ProductEntity product = productRepository.findById(productBean.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
      
            OrderProductEntity entity = new OrderProductEntity();
            entity.setOrder(order);
            entity.setProduct(product);
             
            /* ✅ CRITICAL FIX */
            entity.setOrderProductId(generateUniqueOrderProductId());
            product.setDescription(productBean.getDescription());
            product.setHsnCode(productBean.getHsnCode());
            entity.setCreatedBy(auth.getName());
            entity.setCreatedDate(LocalDateTime.now());
            entity.setLastModifiedBy(auth.getName());
            entity.setLastModifiedDate(LocalDateTime.now());
            entity.setBranchIds(currentUser.getBranchIds());
            productRepository.save(product);
            orderProductRepository.save(entity);
            savedProducts.add(productBean);
        }
        order.setProductEntity(productent);
        updateOrderStatus(order);
        notifyCustomer(order);

        OrderProductBean response = new OrderProductBean();
        response.setOrderId(order.getOrderId());
        response.setProducts(savedProducts);
        return response;
    }

    /* =========================================================
       HELPERS
       ========================================================= */
    private void updateOrderStatus(OrderEntity order) {
        order.setStatus("CREATED");
        orderRepository.save(order);

        OrderStatusHistoryEntity history = new OrderStatusHistoryEntity();
        history.setOrderId(order.getOrderId());
        history.setStatus(order.getStatus());
        history.setChangedAt(LocalDateTime.now());
        statusHistoryRepository.save(history);
    }

    private void notifyCustomer(OrderEntity order) {
        CustomerEntity customer = order.getCustomer();
        if (customer == null) return;

        try {
            if (customer.getCustomerNumber() != null) {
                smsService.sendSms(
                        customer.getCustomerNumber(),
                        "Hello " + customer.getCustomerName()
                                + ", your consignment "
                                + order.getOrderId()
                                + " has been created."
                );
            }
        } catch (Exception ignored) {}

        try {
            if (customer.getCustomerEmail() != null) {
                mailService.sendSimpleEmail(
                        customer.getCustomerEmail(),
                        "Consignment Created - " + order.getOrderId(),
                        "Your consignment has been successfully created."
                );
            }
        } catch (Exception ignored) {}
    }

    /* =========================================================
       READ OPERATIONS
       ========================================================= */
    @Override
    public List<OrderProductBean> getAllOrderProducts() {
        return orderProductRepository.findAll()
                .stream()
                .map(this::convertToBean)
                .collect(Collectors.toList());
    }

    @Override
    public OrderProductBean getOrderProductById(String id) {
        return orderProductRepository.findById(id)
                .map(this::convertToBean)
                .orElseThrow(() -> new RuntimeException("OrderProduct not found"));
    }

    @Override
    public void deleteOrderProduct(String id) {
        if (!orderProductRepository.existsById(id)) {
            throw new RuntimeException("OrderProduct not found");
        }
        orderProductRepository.deleteById(id);
    }

    /* =========================================================
       FILTER
       ========================================================= */
    @Override
    public List<OrderProductBean> getOrderProductsbyfilterCriteria(
            List<FilterCriteriaBean> filters, int limit) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"ADMIN".equalsIgnoreCase(user.getRole().name())) {
            filters.removeIf(f -> "branchIds".equalsIgnoreCase(f.getAttribute()));

            FilterCriteriaBean branchFilter = new FilterCriteriaBean();
            branchFilter.setAttribute("branchIds");
            branchFilter.setOperation(FilterOperation.AMONG);
            branchFilter.setValue(user.getBranchIds());
            branchFilter.setValueType(String.class);

            filters.add(branchFilter);
        }

        return filterCriteriaService
                .getListOfFilteredData(OrderProductEntity.class, filters, limit)
                .stream()
                .map(e -> convertToBean((OrderProductEntity) e))
                .collect(Collectors.toList());
    }

    /* =========================================================
       MAPPER
       ========================================================= */
    private OrderProductBean convertToBean(OrderProductEntity entity) {
        OrderProductBean bean = new OrderProductBean();
        bean.setOrderProductId(entity.getOrderProductId());
        bean.setOrderId(entity.getOrder().getOrderId());
        bean.setProductId(entity.getProduct().getProductId());
        bean.setLastModifiedBy(entity.getLastModifiedBy());
        bean.setLastModifiedDate(entity.getLastModifiedDate());
        return bean;
    }

    @Override
    @Transactional
    public OrderProductBean updateOrderProduct(String id, OrderProductBean bean) {

        OrderProductEntity entity = orderProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderProduct not found for ID: " + id));

        // Optional updates (only if provided)
        if (bean.getOrderId() != null) {
            OrderEntity order = orderRepository.findById(bean.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            entity.setOrder(order);
        }

        if (bean.getProductId() != null) {
            ProductEntity product = productRepository.findById(bean.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            entity.setProduct(product);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        entity.setLastModifiedBy(auth.getName());
        entity.setLastModifiedDate(LocalDateTime.now());

        OrderProductEntity saved = orderProductRepository.save(entity);
        return convertToBean(saved);
    }
    
    @Override
    @Transactional
    public List<OrderProductBean> saveOrderProducts(OrderProductBean bean) {

        if (bean.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID must not be null");
        }

        if (bean.getProducts() == null || bean.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Product list is empty");
        }

        OrderEntity order = orderRepository.findById(bean.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        List<OrderProductBean> savedBeans = new ArrayList<>();

        for (ProductBean productBean : bean.getProducts()) {

            ProductEntity product = productRepository.findById(productBean.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderProductEntity entity = new OrderProductEntity();
            entity.setOrder(order);
            entity.setProduct(product);

            /* ✅ Mandatory ID */
            entity.setOrderProductId(generateUniqueOrderProductId());

            entity.setCreatedBy(auth.getName());
            entity.setCreatedDate(LocalDateTime.now());
            entity.setLastModifiedBy(auth.getName());
            entity.setLastModifiedDate(LocalDateTime.now());
            entity.setBranchIds(currentUser.getBranchIds());

            OrderProductEntity saved = orderProductRepository.save(entity);
            savedBeans.add(convertToBean(saved));
        }

        return savedBeans;
    }
    
    @Override
    public List<OrderProductBean> getProductsByOrderId(String orderId) {

        if (orderId == null) {
            throw new IllegalArgumentException("Order ID must not be null");
        }

        List<OrderProductEntity> entities =
                orderProductRepository.findByOrderOrderId(orderId);

        return entities.stream()
                .map(this::convertToBean)
                .collect(Collectors.toList());
    }



}

// =====================================================
// FILE 6: OrderBoxServiceImpl.java - UPDATED IMPLEMENTATION
// =====================================================
package com.tms.boxes.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.boxes.bean.BoxBean;
import com.tms.boxes.bean.BoxProductBean;
import com.tms.boxes.entity.BoxEntity;
import com.tms.boxes.entity.BoxProductEntity;
import com.tms.boxes.entity.HSNCodeEntity;
import com.tms.boxes.entity.OrderBoxEntity;
import com.tms.boxes.repository.BoxProductRepository;
import com.tms.boxes.repository.BoxRepository;
import com.tms.boxes.repository.HSNCodeRepository;
import com.tms.boxes.repository.OrderBoxRepository;
import com.tms.boxes.service.OrderBoxService;
import com.tms.order.entity.OrderEntity;
import com.tms.order.repository.OrderRepository;
import com.tms.product.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderBoxServiceImpl implements OrderBoxService {
	
    @Autowired
    private OrderBoxRepository orderBoxRepository;
    
    @Autowired
    private BoxRepository boxRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private BoxProductRepository boxProductRepository;
    
    @Autowired
    private HSNCodeRepository hsnCodeRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public void addBoxToOrder(String orderId, String boxId) {
        System.out.println("📦 Adding box to order: orderId=" + orderId + ", boxId=" + boxId);
        
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        BoxEntity box = boxRepository.findById(boxId)
            .orElseThrow(() -> new RuntimeException("Box not found: " + boxId));
        
        // Validate box status
        if (!"PACKED".equals(box.getStatus())) {
            throw new RuntimeException("Box must be PACKED before adding to order. Current status: " + box.getStatus());
        }
        
        // Check if box is already added to this order
        Optional<OrderBoxEntity> existing = orderBoxRepository.findByOrderIdAndBoxId(orderId, boxId);
        if (existing.isPresent()) {
            throw new RuntimeException("Box is already added to this order");
        }
        
        // Check if box is already assigned to another order
        List<OrderBoxEntity> boxOrders = orderBoxRepository.findByBoxId(boxId);
        if (!boxOrders.isEmpty()) {
            throw new RuntimeException("Box is already assigned to another order");
        }
        
        // Create order-box relationship
        OrderBoxEntity orderBox = new OrderBoxEntity();
        orderBox.setOrderId(orderId);
        orderBox.setBoxId(boxId);
        orderBox.setAddedDate(LocalDateTime.now());
        
        // Get next sequence number
        List<OrderBoxEntity> existingBoxes = orderBoxRepository.findByOrderId(orderId);
        orderBox.setBoxSequence(existingBoxes.size() + 1);
        
        orderBoxRepository.save(orderBox);
        System.out.println("✅ Order-box relationship created with sequence: " + orderBox.getBoxSequence());
        
        // Update box status to IN_TRANSIT
        box.setStatus("IN_TRANSIT");
        box.setLastModifiedDate(LocalDateTime.now());
        boxRepository.save(box);
        System.out.println("✅ Box status updated to IN_TRANSIT");
        
        // Update order status if it's still PENDING
        if ("PENDING".equalsIgnoreCase(order.getStatus())) {
            order.setStatus("CONFIRM");
            orderRepository.save(order);
            System.out.println("✅ Order status updated to CONFIRM");
        }
    }
    
    @Override
    public List<BoxBean> getBoxesForOrder(String orderId) {
        System.out.println("📦 Fetching boxes for order: " + orderId);
        
        List<OrderBoxEntity> orderBoxes = orderBoxRepository.findByOrderId(orderId);
        System.out.println("✅ Found " + orderBoxes.size() + " boxes for order");
        
        return orderBoxes.stream()
            .map(ob -> {
                Optional<BoxEntity> boxOpt = boxRepository.findById(ob.getBoxId());
                if (boxOpt.isPresent()) {
                    BoxEntity box = boxOpt.get();
                    HSNCodeEntity hsnCode = hsnCodeRepository.findById(box.getHsnCode()).orElse(null);
                    return convertToBoxBean(box, hsnCode);
                }
                return null;
            })
            .filter(bean -> bean != null)
            .collect(Collectors.toList());
    }
    
    @Override
    public void removeBoxFromOrder(String orderId, String boxId) {
        System.out.println("📦 Removing box from order: orderId=" + orderId + ", boxId=" + boxId);
        
        OrderBoxEntity orderBox = orderBoxRepository.findByOrderIdAndBoxId(orderId, boxId)
            .orElseThrow(() -> new RuntimeException("Box not found in this order"));
        
        BoxEntity box = boxRepository.findById(boxId)
            .orElseThrow(() -> new RuntimeException("Box not found"));
        
        // Only allow removal if box is still IN_TRANSIT (not delivered)
        if ("DELIVERED".equals(box.getStatus())) {
            throw new RuntimeException("Cannot remove a delivered box from order");
        }
        
        // Remove order-box relationship
        orderBoxRepository.delete(orderBox);
        System.out.println("✅ Order-box relationship removed");
        
        // Update box status back to PACKED
        box.setStatus("PACKED");
        box.setLastModifiedDate(LocalDateTime.now());
        boxRepository.save(box);
        System.out.println("✅ Box status reverted to PACKED");
    }
    
    private BoxBean convertToBoxBean(BoxEntity entity, HSNCodeEntity hsnCode) {
        BoxBean bean = new BoxBean();
        bean.setBoxId(entity.getBoxId());
        bean.setBoxName(entity.getBoxName());
        bean.setBoxCode(entity.getBoxCode());
        bean.setHsnCode(entity.getHsnCode());
        bean.setHsnDescription(hsnCode != null ? hsnCode.getDescription() : null);
        bean.setDescription(entity.getDescription());
        bean.setMaxWeight(entity.getMaxWeight());
        bean.setTotalValue(entity.getTotalValue());
        bean.setStatus(entity.getStatus());
        bean.setCreatedBy(entity.getCreatedBy());
        bean.setCreatedDate(entity.getCreatedDate());
        
        // Parse dimensions
        if (entity.getDimensions() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                bean.setDimensions(mapper.readValue(entity.getDimensions(), Map.class));
            } catch (Exception e) {
                bean.setDimensions(new HashMap<>());
            }
        }
        
        // Get products in this box
        List<BoxProductEntity> products = boxProductRepository.findByBoxId(entity.getBoxId());
        List<BoxProductBean> productBeans = products.stream()
            .map(this::convertProductToBean)
            .collect(Collectors.toList());
        bean.setProducts(productBeans);
        
        return bean;
    }
    
    private BoxProductBean convertProductToBean(BoxProductEntity entity) {
        BoxProductBean bean = new BoxProductBean();
        bean.setId(entity.getId());
        bean.setProductId(entity.getProductId());
        bean.setQuantity(entity.getQuantity());
        bean.setWeightPerUnit(entity.getWeightPerUnit());
        bean.setPricePerUnit(entity.getPricePerUnit());
        bean.setTotalWeight(entity.getTotalWeight());
        bean.setTotalPrice(entity.getTotalPrice());
        
        productRepository.findById(entity.getProductId()).ifPresent(product -> {
//            bean.setProductName(product.getProductName());
//            bean.setProductCode(product.getProductCode());
        });
        
        return bean;
    }
}
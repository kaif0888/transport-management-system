package com.tms.boxes.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.JwtSecurity.entity.User; 
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.boxes.bean.BoxBean;
import com.tms.boxes.bean.BoxProductBean;
import com.tms.boxes.entity.BoxEntity;
import com.tms.boxes.entity.BoxProductEntity;
import com.tms.boxes.entity.HSNCodeEntity;
import com.tms.boxes.repository.BoxProductRepository;
import com.tms.boxes.repository.BoxRepository;
import com.tms.boxes.repository.HSNCodeRepository;
import com.tms.boxes.service.BoxService;
import com.tms.product.entity.ProductEntity;
import com.tms.product.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BoxServiceImpl implements BoxService {
    
    @Autowired
    private BoxRepository boxRepository;
    
    @Autowired
    private BoxProductRepository boxProductRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private HSNCodeRepository hsnCodeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private String generateUniqueBoxId() {
        String prefix = "BOX";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomStr = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + timestamp.substring(timestamp.length() - 8) + randomStr;
    }
    
    @Override
    public BoxBean createBox(BoxBean boxBean) {
        System.out.println("📦 Starting box creation process...");
        
        // Validate HSN Code first
        HSNCodeEntity hsnCode = hsnCodeRepository.findById(boxBean.getHsnCode())
            .orElseThrow(() -> new RuntimeException("Invalid HSN Code: " + boxBean.getHsnCode()));
        
        System.out.println("✅ HSN Code validated: " + hsnCode.getHsnCode());
        
        BoxEntity box = new BoxEntity();
        box.setBoxId(generateUniqueBoxId());
        box.setBoxName(boxBean.getBoxName());
        box.setBoxCode(boxBean.getBoxCode());
        box.setHsnCode(boxBean.getHsnCode());
        box.setDescription(boxBean.getDescription());
        box.setMaxWeight(boxBean.getMaxWeight());
        box.setStatus("EMPTY");
        box.setTotalValue(0.0);
        
        // Store dimensions as JSON
        if (boxBean.getDimensions() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                box.setDimensions(mapper.writeValueAsString(boxBean.getDimensions()));
                System.out.println("✅ Dimensions stored as JSON");
            } catch (Exception e) {
                System.err.println("⚠️ Error storing dimensions: " + e.getMessage());
                box.setDimensions("{}");
            }
        }
        
        // Get authenticated user - Handle gracefully
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (authentication != null && authentication.getName() != null) 
            ? authentication.getName() 
            : "anonymousUser";
        
        System.out.println("👤 User email from authentication: " + userEmail);
        
        box.setCreatedBy(userEmail);
        box.setCreatedDate(LocalDateTime.now());
        box.setLastModifiedBy(userEmail);
        box.setLastModifiedDate(LocalDateTime.now());
        
        // Handle branch IDs gracefully - Don't fail if user not found
        box.setBranchIds(""); // Set empty by default
        
        if (!"anonymousUser".equals(userEmail)) {
            try {
                Optional<User> userOptional = userRepository.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    User currentUser = userOptional.get();
                    if (currentUser.getBranchIds() != null && !currentUser.getBranchIds().isEmpty()) {
                        box.setBranchIds(currentUser.getBranchIds());
                        System.out.println("✅ Branch IDs set from user: " + currentUser.getBranchIds());
                    } else {
                        System.out.println("⚠️ User found but has no branch IDs");
                    }
                } else {
                    System.out.println("⚠️ User not found in database, continuing with empty branch IDs");
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error fetching user branch IDs: " + e.getMessage());
                // Continue execution - don't fail the box creation
            }
        }
        
        System.out.println("💾 Saving box to database...");
        BoxEntity saved = boxRepository.save(box);
        System.out.println("✅ Box saved successfully with ID: " + saved.getBoxId());
        
        return convertToBean(saved, hsnCode);
    }
    
    @Override
    public BoxBean addProductToBox(String boxId, String productId, Integer quantity) {
        BoxEntity box = boxRepository.findById(boxId)
            .orElseThrow(() -> new RuntimeException("Box not found"));
        
        ProductEntity product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        List<BoxProductEntity> existingProducts = boxProductRepository.findByBoxId(boxId);
        Optional<BoxProductEntity> existing = existingProducts.stream()
            .filter(bp -> bp.getProductId().equals(productId))
            .findFirst();
        
        BoxProductEntity boxProduct;
        if (existing.isPresent()) {
            boxProduct = existing.get();
            boxProduct.setQuantity(boxProduct.getQuantity() + quantity);
        } else {
            boxProduct = new BoxProductEntity();
            boxProduct.setBoxId(boxId);
            boxProduct.setProductId(productId);
            boxProduct.setQuantity(quantity);
//            boxProduct.setWeightPerUnit(product.getWeight() != null ? product.getWeight().doubleValue() : 0.0);
//            boxProduct.setPricePerUnit(product.getPrice() != null ? product.getPrice().doubleValue() : 0.0);
        }
        
//        double weightPerUnit = product.getWeight() != null ? product.getWeight().doubleValue() : 0.0;
//        double pricePerUnit = product.getPrice() != null ? product.getPrice().doubleValue() : 0.0;
        
//        boxProduct.setTotalWeight(boxProduct.getQuantity() * weightPerUnit);
//        boxProduct.setTotalPrice(boxProduct.getQuantity() * pricePerUnit);
        boxProduct.setAddedDate(LocalDateTime.now());
        
        boxProductRepository.save(boxProduct);
        updateBoxTotals(boxId);
        
        HSNCodeEntity hsnCode = hsnCodeRepository.findById(box.getHsnCode()).orElse(null);
        return convertToBean(boxRepository.findById(boxId).get(), hsnCode);
    }
    
    @Override
    public BoxBean removeProductFromBox(String boxId, String productId) {
        boxProductRepository.deleteByBoxIdAndProductId(boxId, productId);
        updateBoxTotals(boxId);
        
        BoxEntity box = boxRepository.findById(boxId)
            .orElseThrow(() -> new RuntimeException("Box not found"));
        HSNCodeEntity hsnCode = hsnCodeRepository.findById(box.getHsnCode()).orElse(null);
        return convertToBean(box, hsnCode);
    }
    
    private void updateBoxTotals(String boxId) {
        BoxEntity box = boxRepository.findById(boxId)
            .orElseThrow(() -> new RuntimeException("Box not found"));
        
        List<BoxProductEntity> products = boxProductRepository.findByBoxId(boxId);
        
        double totalValue = products.stream()
            .mapToDouble(bp -> bp.getTotalPrice() != null ? bp.getTotalPrice() : 0.0)
            .sum();
        
        box.setTotalValue(totalValue);
        box.setStatus(products.isEmpty() ? "EMPTY" : "PACKED");
        box.setLastModifiedDate(LocalDateTime.now());
        
        boxRepository.save(box);
    }
    
    @Override
    public BoxBean getBoxById(String boxId) {
        BoxEntity box = boxRepository.findById(boxId)
            .orElseThrow(() -> new RuntimeException("Box not found"));
        HSNCodeEntity hsnCode = hsnCodeRepository.findById(box.getHsnCode()).orElse(null);
        return convertToBean(box, hsnCode);
    }
    
    @Autowired
    private ProductRepository productRepo;
    
    @Override
    public List<ProductEntity> getAllBoxes(Map<String, Object> filters) {
    	String orderId =null;
    	if (filters.containsKey("filters")) {
    	    List<Map<String, Object>> filterList =
    	            (List<Map<String, Object>>) filters.get("filters");

    	    for (Map<String, Object> filter : filterList) {
    	        if ("orderId".equals(filter.get("attribute"))) {
    	            orderId = (String) filter.get("value");
    	            break;
    	        }
    	    }
    	}
    	
        List<ProductEntity> products = productRepo.findByOrderId(orderId);
       return products;
    }
    
    private BoxBean convertToBean(BoxEntity entity, HSNCodeEntity hsnCode) {
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
        
        if (entity.getDimensions() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                bean.setDimensions(mapper.readValue(entity.getDimensions(), Map.class));
            } catch (Exception e) {
                bean.setDimensions(new HashMap<>());
            }
        }
        
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
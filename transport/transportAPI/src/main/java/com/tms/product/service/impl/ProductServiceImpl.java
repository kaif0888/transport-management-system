package com.tms.product.service.impl;

import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.boxes.entity.HSNCodeEntity;
import com.tms.boxes.repository.HSNCodeRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.product.bean.ProductBean;
import com.tms.product.entity.ProductEntity;
import com.tms.product.repository.ProductRepository;
import com.tms.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilterCriteriaService<ProductEntity> productFilterCriteriaService;
    
    @Autowired
    HSNCodeRepository hsnCodeRepository;

    /* =====================================================
       UNIQUE PRODUCT ID GENERATOR
       Example: BOX-20260114-001
       ===================================================== */
    private String generateUniqueProductId() {
        String prefix = "BOX-";
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";

        List<ProductEntity> todayProducts =
                productRepository.findByProductIdStartingWith(fullPrefix);

        int maxSeq = todayProducts.stream()
                .map(p -> p.getProductId().substring(fullPrefix.length()))
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

    /* =====================================================
       CREATE PRODUCT
       ===================================================== */
//    @Override
//    public ProductBean createProduct(ProductBean bean) {
//
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        User currentUser = userRepository.findByEmail(authentication.getName())
//                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
//
//        ProductEntity entity = new ProductEntity();
//        
//        if(bean.getHsnCode().length()>8) {
//        	throw new IllegalArgumentException("HSN CODE Size should not greater than 8");
//        }
//
//        entity.setProductId(generateUniqueProductId());
//        entity.setBoxCode(bean.getBoxCode());
//        entity.setBoxName(bean.getBoxName());
//        entity.setHsnCode(bean.getHsnCode());
//        entity.setDescription(bean.getDescription());
//
//        entity.setMaxWeight(bean.getMaxWeight());
//        entity.setActualWeight(bean.getActualWeight());
//
//        entity.setDimensions(bean.getDimensions());
//        entity.setTotalValue(bean.getTotalValue());
//
//        entity.setStatus(bean.getStatus());
//        entity.setBranchIds(currentUser.getBranchIds());
//
//        entity.setCreatedBy(authentication.getName());
//        entity.setCreatedDate(LocalDateTime.now());
//        entity.setLastModifiedBy(authentication.getName());
//        entity.setLastModifiedDate(LocalDateTime.now());
//
//        ProductEntity saved = productRepository.save(entity);
//        return convertToBean(saved);
//    }
    
    @Override
    public ProductBean createProduct(ProductBean bean) {
 
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
 
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
 
        ProductEntity entity = new ProductEntity();
 
        entity.setProductId(generateUniqueProductId());
        entity.setBoxCode(bean.getBoxCode());
        entity.setBoxName(bean.getBoxName());
        
        // ✅ Save valid HSN code string
        
        entity.setStorageCondition(bean.getStorageCondition());
        entity.setWeight(bean.getWeight());
              entity.setHeight(bean.getHeight());
        entity.setLength(bean.getLength());
        entity.setWidth(bean.getWidth());
        entity.setTotalValue(bean.getTotalValue());
        entity.setStatus(bean.getStatus());
        entity.setBranchIds(currentUser.getBranchIds());
 
        entity.setCreatedBy(authentication.getName());
        entity.setCreatedDate(LocalDateTime.now());
        entity.setLastModifiedBy(authentication.getName());
        entity.setLastModifiedDate(LocalDateTime.now());
 
        ProductEntity saved = productRepository.save(entity);
        return convertToBean(saved);
    }

    /* =====================================================
       GET BY ID
       ===================================================== */
    @Override
    public ProductBean getProductById(String id) {
        return productRepository.findById(id)
                .map(this::convertToBean)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    /* =====================================================
       GET ALL
       ===================================================== */
    @Override
    public List<ProductBean> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToBean)
                .collect(Collectors.toList());
    }

    /* =====================================================
       UPDATE PRODUCT
       ===================================================== */
    @Override
    public ProductBean updateProduct(ProductBean bean) {

        ProductEntity entity = productRepository.findById(bean.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found with ID: " + bean.getProductId()));

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        entity.setBoxCode(bean.getBoxCode());
        entity.setBoxName(bean.getBoxName());
        entity.setHsnCode(bean.getHsnCode());
        entity.setDescription(bean.getDescription());

        entity.setWeight(bean.getWeight());
        entity.setHeight(bean.getHeight());
        entity.setLength(bean.getLength());
        entity.setWidth(bean.getWidth());
        entity.setTotalValue(bean.getTotalValue());
        entity.setStatus(bean.getStatus());

        entity.setLastModifiedBy(authentication.getName());
        entity.setLastModifiedDate(LocalDateTime.now());

        ProductEntity updated = productRepository.save(entity);
        return convertToBean(updated);
    }

    /* =====================================================
       DELETE
       ===================================================== */
    @Override
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    /* =====================================================
       FILTER PRODUCTS (ROLE + BRANCH BASED)
       ===================================================== */
    @Override
    public List<ProductBean> filterProducts(List<FilterCriteriaBean> filters, int limit) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        // Non-admin users → restrict by branchIds
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().name())) {

            filters.removeIf(f ->
                    "branchIds".equalsIgnoreCase(f.getAttribute()));

            FilterCriteriaBean branchFilter = new FilterCriteriaBean();
            branchFilter.setAttribute("branchIds");
            branchFilter.setOperation(FilterOperation.AMONG);
            branchFilter.setValue(currentUser.getBranchIds());
            branchFilter.setValueType(String.class);

            filters.add(branchFilter);
        }

        @SuppressWarnings("unchecked")
        List<ProductEntity> entities =
                (List<ProductEntity>) productFilterCriteriaService
                        .getListOfFilteredData(ProductEntity.class, filters, limit);

        return entities.stream()
                .map(this::convertToBean)
                .collect(Collectors.toList());
    }

    /* =====================================================
       ENTITY → BEAN
       ===================================================== */
    private ProductBean convertToBean(ProductEntity entity) {

        ProductBean bean = new ProductBean();

        bean.setProductId(entity.getProductId());
        bean.setBoxCode(entity.getBoxCode());
        bean.setBoxName(entity.getBoxName());
        bean.setHsnCode(entity.getHsnCode());
        bean.setDescription(entity.getDescription());

        bean.setWeight(entity.getWeight());
        bean.setHeight(entity.getHeight());
        bean.setWidth(entity.getWidth());
        bean.setLength(entity.getLength());
        bean.setTotalValue(entity.getTotalValue());
 
        bean.setStatus(entity.getStatus());
        bean.setBranchIds(entity.getBranchIds());
       bean.setStorageCondition(entity.getStorageCondition());
        bean.setCreatedBy(entity.getCreatedBy());
        bean.setCreatedDate(entity.getCreatedDate());
        bean.setLastModifiedBy(entity.getLastModifiedBy());
        bean.setLastModifiedDate(entity.getLastModifiedDate());

        return bean;
    }

	@Override
	public List<ProductBean> getProductsByCategoryId(String categoryId) {
		// TODO Auto-generated method stub
		return null;
	}
}

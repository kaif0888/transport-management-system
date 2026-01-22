package com.tms.product.service.impl;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.product.bean.ProductBean;
import com.tms.product.entity.ProductEntity;
import com.tms.product.repository.ProductRepository;
import com.tms.product.service.ProductService;
import com.tms.productcategory.entity.ProductCategoryEntity;
import com.tms.productcategory.repository.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private FilterCriteriaService<ProductEntity> productFilterCriteriaService;

    @Autowired
    private FilterCriteriaService<ProductCategoryEntity> categoryFilterCriteriaService;

    
    @Override
    public ProductBean createProduct(ProductBean bean) {
        ProductCategoryEntity category = categoryRepository.findById(bean.getCategoryId())
        		.orElseThrow(() -> new RuntimeException("Category not found with ID: " + bean.getCategoryId()));

        ProductEntity entity = new ProductEntity();
        entity.setProductCode(bean.getProductCode());
        entity.setProductName(bean.getProductName());
        entity.setCategory(category);
        entity.setWeight(bean.getWeight());
        entity.setPrice(bean.getPrice());
 
        ProductEntity saved = productRepository.save(entity);
        return convertToBean(saved);
    }

    @Override
    public ProductBean getProductById(Long id) {
        return productRepository.findById(id)
            .map(this::convertToBean)
            .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<ProductBean> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::convertToBean)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductBean> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategory_CategoryId(categoryId).stream()
            .map(this::convertToBean)
            .collect(Collectors.toList());
    }

    @Override
    public ProductBean updateProduct(ProductBean bean) {
        ProductEntity entity = productRepository.findById(bean.getProductId())
        		.orElseThrow(() -> new RuntimeException("Product not found with ID: " + bean.getProductId()));

        ProductCategoryEntity category = categoryRepository.findById(bean.getCategoryId())
        		.orElseThrow(() -> new RuntimeException("Category not found with ID: " + bean.getCategoryId()));

        entity.setProductCode(bean.getProductCode());
        entity.setProductName(bean.getProductName());
        entity.setCategory(category);
        entity.setWeight(bean.getWeight());
        entity.setPrice(bean.getPrice());

        ProductEntity saved = productRepository.save(entity);
        return convertToBean(saved);
    }
    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductBean> filterProducts(List<FilterCriteriaBean> filters, int limit) {
        try {
            @SuppressWarnings("unchecked")
            List<ProductEntity> filteredEntities = (List<ProductEntity>) productFilterCriteriaService
                .getListOfFilteredData(ProductEntity.class, filters, limit);

            return filteredEntities.stream()
                .map(this::convertToBean)
                .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error filtering products: " + e.getMessage(), e);
        }
    }

    private ProductBean convertToBean(ProductEntity entity) {
        ProductBean bean = new ProductBean();
        bean.setProductId(entity.getProductId());
        bean.setProductCode(entity.getProductCode());
        bean.setProductName(entity.getProductName());
        bean.setWeight(entity.getWeight());
        bean.setPrice(entity.getPrice());
        bean.setCategoryId(entity.getCategory().getCategoryId());
        bean.setCategoryName(entity.getCategory().getCategoryName());
        return bean;
    }
}

package com.tms.productcategory.service.impl;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.productcategory.bean.ProductCategoryBean;
import com.tms.productcategory.entity.ProductCategoryEntity;
import com.tms.productcategory.repository.ProductCategoryRepository;
import com.tms.productcategory.service.ProductCategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryRepository categoryRepository;
    
    @Autowired
    private FilterCriteriaService<ProductCategoryEntity> filterCriteriaService;


    @Override
    public ProductCategoryBean createCategory(ProductCategoryBean bean) {
        ProductCategoryEntity entity = new ProductCategoryEntity();
        entity.setCategoryName(bean.getCategoryName());

        entity = categoryRepository.save(entity);
        bean.setCategoryId(entity.getCategoryId());
        return bean;
    }

    @Override
    public ProductCategoryBean getCategoryById(Long id) {
        ProductCategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return convertToBean(entity);
    }

    @Override
    public List<ProductCategoryBean> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::convertToBean).collect(Collectors.toList());
    }

    @Override
    public ProductCategoryBean updateCategory(ProductCategoryBean bean) {
        return createCategory(bean); // Assumes ID is set
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private ProductCategoryBean convertToBean(ProductCategoryEntity entity) {
        ProductCategoryBean bean = new ProductCategoryBean();
        bean.setCategoryId(entity.getCategoryId());
        bean.setCategoryName(entity.getCategoryName());
        return bean;
    }

    @Override
    public List<ProductCategoryBean> filteredProductCategorys(List<FilterCriteriaBean> filters, int limit) {
        try {
            @SuppressWarnings("unchecked")
            List<ProductCategoryEntity> filteredEntities = (List<ProductCategoryEntity>) filterCriteriaService
                    .getListOfFilteredData(ProductCategoryEntity.class, filters, limit);

            return filteredEntities.stream()
                    .map(this::convertToBean)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error filtering product categories: " + e.getMessage(), e);
        }
    }

}

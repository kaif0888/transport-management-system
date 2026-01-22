package com.tms.productcategory.service;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.productcategory.bean.ProductCategoryBean;

import java.util.List;

public interface ProductCategoryService {
    ProductCategoryBean createCategory(ProductCategoryBean bean);
    ProductCategoryBean getCategoryById(Long id);
    List<ProductCategoryBean> getAllCategories();
    ProductCategoryBean updateCategory(ProductCategoryBean bean);
    void deleteCategory(Long id);
	List<ProductCategoryBean> filteredProductCategorys(List<FilterCriteriaBean> filters, int limit);
}

package com.tms.productcategory.service;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.productcategory.bean.ProductCategoryBean;

import java.util.List;

public interface ProductCategoryService {
    ProductCategoryBean createCategory(ProductCategoryBean bean);
    ProductCategoryBean getCategoryById(String id);
    List<ProductCategoryBean> getAllCategories();
    ProductCategoryBean updateCategory(ProductCategoryBean bean);
    void deleteCategory(String id);
	List<ProductCategoryBean> filteredProductCategorys(List<FilterCriteriaBean> filters, int limit);
	ProductCategoryBean updateCategorybyId(ProductCategoryBean productCategoryBean);
}

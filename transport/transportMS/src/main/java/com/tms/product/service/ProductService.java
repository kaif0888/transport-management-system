package com.tms.product.service;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.product.bean.ProductBean;
import java.util.List;

public interface ProductService {
    ProductBean createProduct(ProductBean bean);
    ProductBean getProductById(Long id);
    List<ProductBean> getAllProducts();
    List<ProductBean> getProductsByCategoryId(Long categoryId);
    ProductBean updateProduct(ProductBean bean);
    void deleteProduct(Long id);
	List<ProductBean> filterProducts(List<FilterCriteriaBean> filters, int limit);
    

}

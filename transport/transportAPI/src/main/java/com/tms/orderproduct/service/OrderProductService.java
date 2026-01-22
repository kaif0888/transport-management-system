package com.tms.orderproduct.service;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.orderproduct.bean.OrderProductBean;
import com.tms.orderproduct.entity.OrderProductEntity;

import java.util.List;

public interface OrderProductService {

    OrderProductBean saveOrderProduct(OrderProductBean bean);

    List<OrderProductBean> getAllOrderProducts();

    OrderProductBean getOrderProductById(String id);

    void deleteOrderProduct(String id);

	OrderProductBean updateOrderProduct(String id, OrderProductBean bean);
	
	List<OrderProductBean> saveOrderProducts(OrderProductBean bean);

	List<OrderProductBean> getOrderProductsbyfilterCriteria(List<FilterCriteriaBean> filters, int limit);

	List<OrderProductBean> getProductsByOrderId(String orderId);

}

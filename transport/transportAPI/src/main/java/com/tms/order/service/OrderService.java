package com.tms.order.service;

import java.util.List;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.order.bean.OrderBean;

public interface OrderService {
	
	public OrderBean createOrder(OrderBean order);
	
	public OrderBean getOrderById(String orderId);
	
	public List<OrderBean> getOrdersByCustomerId(String customerId);
	
	public void deleteOrder(String orderId);

	public List<OrderBean> getOrderbyfilterCriteria(List<FilterCriteriaBean> filters, int limit);

	public void confirmOrderStatus(String orderId);
	
	public OrderBean updateOrderById(String orderId,OrderBean order);
	
}

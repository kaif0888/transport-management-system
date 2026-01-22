package com.tms.order.service;

import java.util.List;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.order.bean.OrderBean;

public interface OrderService {
	
	public OrderBean createOrder(OrderBean order);
	
	public OrderBean getOrderById(Long orderId);
	
	public List<OrderBean> getOrdersByCustomerId(Long customerId);
	
	public void deleteOrder(Long orderId);
	
}

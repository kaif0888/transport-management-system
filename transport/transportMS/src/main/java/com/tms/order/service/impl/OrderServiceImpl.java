
package com.tms.order.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.tms.customer.entity.CustomerEntity;
import com.tms.customer.repository.CustomerRepository;
import com.tms.location.entity.LocationEntity;
import com.tms.order.bean.OrderBean;
import com.tms.order.entity.OrderEntity;
import com.tms.order.repository.OrderRepository;
import com.tms.order.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public OrderBean createOrder(OrderBean orderBean) {
		CustomerEntity customerEntity = customerRepository.findById(orderBean.getCustomerId())
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		OrderEntity orderEntity = convertToEntity(orderBean, customerEntity);

		orderEntity = orderRepository.save(orderEntity);

		return convertToBean(orderEntity);
	}

	@Override
	public OrderBean getOrderById(Long orderId) {
		OrderEntity orderEntity = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found"));
		return convertToBean(orderEntity);
	}

	@Override
	public List<OrderBean> getOrdersByCustomerId(Long customerId) {
		
		List<OrderEntity> orderEntities = orderRepository.findByCustomerId(customerId);

		return orderEntities.stream().map(this::convertToBean).collect(Collectors.toList());
	}

	@Override
	public void deleteOrder(Long orderId) {
		orderRepository.deleteById(orderId);
	}

	private OrderEntity convertToEntity(OrderBean orderBean, CustomerEntity customerEntity) {
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setDispatchDate(orderBean.getDispatchDate());
		orderEntity.setDeliveryDate(orderBean.getDeliveryDate());
		orderEntity.setStatus(orderBean.getStatus());
		orderEntity.setPaymentStatus(orderBean.getPaymentStatus());
		orderEntity.setCustomer(customerEntity);
		orderEntity.setCreatedBy(orderBean.getCreatedBy());
		orderEntity.setCreatedDate(orderBean.getCreatedDate());
		orderEntity.setLastModifiedBy(orderBean.getLastModifiedBy());
		orderEntity.setLastModifiedDate(orderBean.getLastModifiedDate());
		orderEntity.setOriginlocationId(orderBean.getOriginlocationId());
		orderEntity.setDestinationlocationId(orderBean.getDestinationlocationId());
 
		return orderEntity;
	}

	private OrderBean convertToBean(OrderEntity orderEntity) {
		OrderBean orderBean = new OrderBean();
		orderBean.setOrderId(orderEntity.getOrderId());
		orderBean.setDispatchDate(orderEntity.getDispatchDate());
		orderBean.setDeliveryDate(orderEntity.getDeliveryDate());
		orderBean.setStatus(orderEntity.getStatus());
		orderBean.setPaymentStatus(orderEntity.getPaymentStatus());
		orderBean.setCustomerId(orderEntity.getCustomer().getCustomerId());
		orderBean.setCreatedBy(orderEntity.getCreatedBy());
		orderBean.setCreatedDate(orderEntity.getCreatedDate());
		orderBean.setLastModifiedBy(orderEntity.getLastModifiedBy());
		orderBean.setLastModifiedDate(orderEntity.getLastModifiedDate());
		orderBean.setOriginlocationId(orderEntity.getOriginlocationId());;
		orderBean.setDestinationlocationId(orderEntity.getDestinationlocationId());
		
		return orderBean;
	}

}

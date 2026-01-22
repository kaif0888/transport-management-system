package com.tms.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.drivertms.been.DriverBeen;
import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.order.bean.OrderBean;
import com.tms.order.service.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/order")
@Api(value = "Order Management System", tags = "Order Management")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@ApiOperation(value = "Create a new order", response = OrderBean.class, notes = "This API is used to create an order for a customer, providing necessary details like origin, destination, and dates.")
	@PostMapping(value = "/createOrder")
	public ResponseEntity<OrderBean> createOrder(@RequestBody OrderBean orderBean) {
		try {
			OrderBean createdOrder = orderService.createOrder(orderBean);
			return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@ApiOperation(value = "Get order by ID", response = OrderBean.class, notes = "This API is used to fetch an order by its unique ID. It returns all details associated with the order.")
	@GetMapping(value = "/getOrderById/{orderId}")
	public ResponseEntity<OrderBean> getOrderById(@PathVariable("orderId") Long orderId) {
		try {
			OrderBean orderBean = orderService.getOrderById(orderId);
			return new ResponseEntity<>(orderBean, HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "Get orders by customer ID", response = List.class, notes = "This API is used to fetch all orders associated with a specific customer using their customer ID.")
	@GetMapping(value = "/getOrdersByCustomerId/{customerId}")
	public ResponseEntity<List<OrderBean>> getOrdersByCustomerId(@PathVariable("customerId") Long customerId) {
		try {
			List<OrderBean> orders = orderService.getOrdersByCustomerId(customerId);
			if (orders.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(orders, HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "Delete an order by ID", response = Void.class, notes = "This API is used to delete an order using its unique ID.")
	@DeleteMapping(value = "/deleteOrder/{orderId}")
	public ResponseEntity<Void> deleteOrder(@PathVariable("orderId") Long orderId) {
		try {
			orderService.deleteOrder(orderId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/listOfOrderByFilter")
	public ResponseEntity<List<OrderBean>> getOrderbyfilterCriteria(@RequestBody FilterRequest request) {
		try {
			int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
			List<OrderBean> getOrderbyfilterCriteria = orderService.getOrderbyfilterCriteria(request.getFilters(),
					limit);
			return ResponseEntity.ok(getOrderbyfilterCriteria);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}
}

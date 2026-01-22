package com.tms.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.boxes.bean.BoxBean;
import com.tms.boxes.service.OrderBoxService;
import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.order.bean.OrderBean;
import com.tms.order.service.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*")
@Api(value = "Order Management System", tags = "Order Management")
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderBoxService orderBoxService;
	
	@PostMapping("/listOfOrderByFilter")
	public ResponseEntity<List<OrderBean>> getOrderbyfilterCriteria(@RequestBody FilterRequest request) {
		try {
			int limit = request.getLimit() != null ? request.getLimit() : 100;
			List<OrderBean> getOrderbyfilterCriteria = orderService.getOrderbyfilterCriteria(request.getFilters(),limit);
			return ResponseEntity.ok(getOrderbyfilterCriteria);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}
	
	@PutMapping(value = "/confirmOrderStatus/{orderId}")
    public ResponseEntity<String> confirmOrderStatus(@PathVariable String orderId) {
        try {
            orderService.confirmOrderStatus(orderId);
            return ResponseEntity.ok("Order status updated to confirm.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
 
	@ApiOperation(value = "Create a new order", response = OrderBean.class)
	@PostMapping(value = "/createOrder")
	public ResponseEntity<OrderBean> createOrder(@RequestBody OrderBean orderBean) {
		try {
			OrderBean createdOrder = orderService.createOrder(orderBean);
			return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@ApiOperation(value = "Get order by ID", response = OrderBean.class)
	@GetMapping(value = "/getOrderById/{orderId}")
	public ResponseEntity<OrderBean> getOrderById(@PathVariable("orderId") String orderId) {
		try {
			OrderBean orderBean = orderService.getOrderById(orderId);
			return new ResponseEntity<>(orderBean, HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "Get orders by customer ID", response = List.class)
	@GetMapping(value = "/getOrdersByCustomerId/{customerId}")
	public ResponseEntity<List<OrderBean>> getOrdersByCustomerId(@PathVariable("customerId") String customerId) {
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
	
	@PutMapping("/updateOrderById/{orderId}")
	public OrderBean updateByOrderId(@PathVariable String orderId,@RequestBody OrderBean order) {
		return orderService.updateOrderById(orderId, order);
	}

	@ApiOperation(value = "Delete an order by ID", response = Void.class)
	@DeleteMapping(value = "/deleteOrder/{orderId}")
	public ResponseEntity<Void> deleteOrder(@PathVariable("orderId") String orderId) {
		try {
			orderService.deleteOrder(orderId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	// ==================== BOX MANAGEMENT ENDPOINTS ====================
	
	@ApiOperation(value = "Add box to order")
	@PostMapping(value = "/addBoxToOrder/{orderId}/{boxId}")
	public ResponseEntity<String> addBoxToOrder(
			@PathVariable("orderId") String orderId,
			@PathVariable("boxId") String boxId) {
		try {
			System.out.println("🎯 Controller: addBoxToOrder called - orderId: " + orderId + ", boxId: " + boxId);
			orderBoxService.addBoxToOrder(orderId, boxId);
			return new ResponseEntity<>("Box added to order successfully", HttpStatus.OK);
		} catch (RuntimeException e) {
			System.err.println("❌ Controller: Error adding box - " + e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@ApiOperation(value = "Get boxes for order", response = List.class)
	@GetMapping(value = "/getBoxesForOrder/{orderId}")
	public ResponseEntity<List<BoxBean>> getBoxesForOrder(@PathVariable("orderId") String orderId) {
		try {
			System.out.println("🎯 Controller: getBoxesForOrder called - orderId: " + orderId);
			List<BoxBean> boxes = orderBoxService.getBoxesForOrder(orderId);
			System.out.println("✅ Controller: Found " + boxes.size() + " boxes");
			return new ResponseEntity<>(boxes, HttpStatus.OK);
		} catch (Exception e) {
			System.err.println("❌ Controller: Error fetching boxes - " + e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ApiOperation(value = "Remove box from order")
	@DeleteMapping(value = "/removeBoxFromOrder/{orderId}/{boxId}")
	public ResponseEntity<String> removeBoxFromOrder(
			@PathVariable("orderId") String orderId,
			@PathVariable("boxId") String boxId) {
		try {
			System.out.println("🎯 Controller: removeBoxFromOrder called - orderId: " + orderId + ", boxId: " + boxId);
			orderBoxService.removeBoxFromOrder(orderId, boxId);
			return new ResponseEntity<>("Box removed from order successfully", HttpStatus.OK);
		} catch (RuntimeException e) {
			System.err.println("❌ Controller: Error removing box - " + e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}

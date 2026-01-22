package com.tms.orderproduct.controller;

import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.order.bean.OrderBean;
import com.tms.orderproduct.bean.OrderProductBean;
import com.tms.orderproduct.entity.OrderProductEntity;
import com.tms.orderproduct.repository.OrderProductRepository;
import com.tms.orderproduct.service.OrderProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderProducts/")
public class OrderProductController {

    @Autowired
    private OrderProductService orderProductService;
    
    @Autowired
    OrderProductRepository orderProductRepository;
    
	@PostMapping("/listOfOrderProductsByFilter")
	public ResponseEntity<List<OrderProductBean>> getOrderProductsbyfilterCriteria(@RequestBody FilterRequest request) {
		try {
			int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
			List<OrderProductBean> getOrderProductsbyfilterCriteria = orderProductService.getOrderProductsbyfilterCriteria(request.getFilters(),limit);
			return ResponseEntity.ok(getOrderProductsbyfilterCriteria);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}
	

    @PostMapping("createOrderProducts")
    public OrderProductBean createOrderProducts(@RequestBody OrderProductBean bean) {
        return orderProductService.saveOrderProduct(bean);
    }

    @GetMapping("getAllOrderProducts")
    public List<OrderProductBean> getAllOrderProducts() {
        return orderProductService.getAllOrderProducts();
    }

    @GetMapping("getOrderProductsById/{id}")
    public OrderProductBean getOrderProductsById(@PathVariable String id) {
        return orderProductService.getOrderProductById(id);
    }

    @DeleteMapping("deleteOrderProductsById/{id}")
    public void deleteOrderProductsById(@PathVariable String id) {
        orderProductService.deleteOrderProduct(id);
    }
    
    @PutMapping("updateOrderProduct/{id}")
    public OrderProductBean updateOrderProduct(@PathVariable String id, @RequestBody OrderProductBean bean) {
        return orderProductService.updateOrderProduct(id, bean);
    }
    



    @GetMapping("/byOrder/{orderId}")
    public ResponseEntity<List<OrderProductBean>> getByOrderId(@PathVariable String orderId) {
        List<OrderProductBean> products = orderProductService.getProductsByOrderId(orderId);
        return ResponseEntity.ok(products);
    }


}

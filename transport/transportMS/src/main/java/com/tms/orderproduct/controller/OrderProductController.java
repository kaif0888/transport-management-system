package com.tms.orderproduct.controller;

import com.tms.orderproduct.bean.OrderProductBean;
import com.tms.orderproduct.service.OrderProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderProducts/")
public class OrderProductController {

    @Autowired
    private OrderProductService orderProductService;

    @PostMapping("createOrderProducts")
    public OrderProductBean createOrderProducts(@RequestBody OrderProductBean bean) {
        return orderProductService.saveOrderProduct(bean);
    }

    @GetMapping("getAllOrderProducts")
    public List<OrderProductBean> getAllOrderProducts() {
        return orderProductService.getAllOrderProducts();
    }

    @GetMapping("getOrderProductsById/{id}")
    public OrderProductBean getOrderProductsById(@PathVariable Long id) {
        return orderProductService.getOrderProductById(id);
    }

    @DeleteMapping("deleteOrderProductsById/{id}")
    public void deleteOrderProductsById(@PathVariable Long id) {
        orderProductService.deleteOrderProduct(id);
    }
}

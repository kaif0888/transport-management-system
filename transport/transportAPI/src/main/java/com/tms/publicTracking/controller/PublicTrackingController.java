package com.tms.publicTracking.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.dispatchTracking.dto.OrderTrackingResponse;
import com.tms.dispatchTracking.service.DispatchTrackingService;
import com.tms.order.bean.OrderBean;

import com.tms.order.service.OrderService;

@RestController
@RequestMapping("/api/public/tracking")
public class PublicTrackingController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DispatchTrackingService dispatchTrackingService;

    //  Order summary (left card)
//    @GetMapping("/order/{orderId}")
//    public ResponseEntity<OrderBean> getOrderSummary(@PathVariable String orderId) {
//        return ResponseEntity.ok(orderService.getOrderById(orderId));
//    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderBean> getOrderSummary(@PathVariable String orderId) {
        try {
            OrderBean orderBean = orderService.getOrderById(orderId);
            return new ResponseEntity<>(orderBean, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    //  Timeline (right card)
    @GetMapping("/order/{orderId}/timeline")
    public ResponseEntity<List<OrderTrackingResponse>> getOrderTimeline(
            @PathVariable String orderId) {
        return ResponseEntity.ok(
            dispatchTrackingService.getTrackingByOrderId(orderId)
        );
    }
}



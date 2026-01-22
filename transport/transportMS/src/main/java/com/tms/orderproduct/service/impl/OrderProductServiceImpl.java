package com.tms.orderproduct.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.order.entity.OrderEntity;
import com.tms.order.repository.OrderRepository;
import com.tms.product.entity.ProductEntity;
import com.tms.product.repository.ProductRepository;
import com.tms.orderproduct.bean.OrderProductBean;
import com.tms.orderproduct.entity.OrderProductEntity;
import com.tms.orderproduct.repository.OrderProductRepository;
import com.tms.orderproduct.service.OrderProductService;

@Service
public class OrderProductServiceImpl implements OrderProductService {

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public OrderProductBean saveOrderProduct(OrderProductBean bean) {
        OrderProductEntity entity = new OrderProductEntity();

        // Set quantity, price per unit, and total weight
        entity.setQuantity(bean.getQuantity());
        entity.setPricePerUnit(bean.getPricePerUnit());
        entity.setTotalWeight(bean.getTotalWeight());

        // Fetch and assign order and product
        Optional<OrderEntity> order = orderRepository.findById(bean.getOrderId());
        Optional<ProductEntity> product = productRepository.findById(bean.getProductId());

        if (order.isPresent() && product.isPresent()) {
            entity.setOrder(order.get());
            entity.setProduct(product.get());
        } else {
            // Optional: throw exception or return null if order/product not found
            return null;
        }

        // Save and return updated bean
        entity = orderProductRepository.save(entity);
        bean.setOrderProductId(entity.getOrderProductId());
        return bean;
    }

    @Override
    public List<OrderProductBean> getAllOrderProducts() {
        List<OrderProductEntity> entities = orderProductRepository.findAll();
        List<OrderProductBean> beans = new ArrayList<>();

        for (OrderProductEntity entity : entities) {
            OrderProductBean bean = new OrderProductBean();
            bean.setOrderProductId(entity.getOrderProductId());
            bean.setOrderId(entity.getOrder().getOrderId());
            bean.setProductId(entity.getProduct().getProductId());
            bean.setQuantity(entity.getQuantity());
            bean.setPricePerUnit(entity.getPricePerUnit());
            bean.setTotalWeight(entity.getTotalWeight());
            beans.add(bean);
        }

        return beans;
    }

    @Override
    public OrderProductBean getOrderProductById(Long id) {
        Optional<OrderProductEntity> optional = orderProductRepository.findById(id);

        if (optional.isPresent()) {
            OrderProductEntity entity = optional.get();
            OrderProductBean bean = new OrderProductBean();
            bean.setOrderProductId(entity.getOrderProductId());
            bean.setOrderId(entity.getOrder().getOrderId());
            bean.setProductId(entity.getProduct().getProductId());
            bean.setQuantity(entity.getQuantity());
            bean.setPricePerUnit(entity.getPricePerUnit());
            bean.setTotalWeight(entity.getTotalWeight());
            return bean;
        }

        return null;
    }

    @Override
    public void deleteOrderProduct(Long id) {
        orderProductRepository.deleteById(id);
    }
}

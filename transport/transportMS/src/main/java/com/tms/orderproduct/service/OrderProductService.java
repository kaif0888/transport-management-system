package com.tms.orderproduct.service;

import com.tms.orderproduct.bean.OrderProductBean;

import java.util.List;

public interface OrderProductService {

    OrderProductBean saveOrderProduct(OrderProductBean bean);

    List<OrderProductBean> getAllOrderProducts();

    OrderProductBean getOrderProductById(Long id);

    void deleteOrderProduct(Long id);
}

package com.company.order.repository;

import java.util.List;
import java.util.Optional;

import com.company.order.model.Order;

public interface OrderRepository {

    Optional<Order> findById(String orderId);

    List<Order> findByCustomerId(String customerId);
}

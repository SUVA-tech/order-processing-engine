package com.company.order.service;

import java.util.List;
import java.util.Optional;

import com.company.order.exception.InvalidOrderException;
import com.company.order.exception.OrderNotFoundException;
import com.company.order.model.Customer;
import com.company.order.model.Order;
import com.company.order.model.OrderItem;
import com.company.order.model.OrderStatus;
import com.company.order.repository.OrderRepository;

public class OrderServiceImpl {
	
	 private final OrderRepository orderRepository;
	 
	 

	

	 public OrderServiceImpl(OrderRepository orderRepository) {
	        this.orderRepository = orderRepository;
	    }

    private static final double MAX_ORDER_TOTAL = 500_000;

    public void createOrder(Order order, Customer customer) {

        if (!customer.isActive()) {
            throw new InvalidOrderException("Customer is inactive");
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new InvalidOrderException("Order has no items");
        }

        double totalAmount = 0;

        for (OrderItem item : order.getItems()) {

            if (item.getPrice() <= 0) {
                throw new InvalidOrderException("Item price must be greater than zero");
            }

            if (item.getQuantity() <= 0) {
                throw new InvalidOrderException("Item quantity must be greater than zero");
            }

            totalAmount += item.getPrice() * item.getQuantity();
        }

        if (totalAmount > MAX_ORDER_TOTAL) {
            throw new InvalidOrderException("Order total exceeds allowed limit");
        }
    }
    
    public void cancelOrder(Order order, String reason) {

        if (reason == null || reason.trim().isEmpty()) {
            throw new InvalidOrderException("Cancellation reason is mandatory");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderException("Order is already cancelled");
        }

        order.cancel(reason);
    }
    
    public Order getOrderById(String orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found for ID: " + orderId));
    }

    public List<Order> getOrdersByCustomerId(String customerId) {

        if (customerId == null || customerId.trim().isEmpty()) {
            throw new InvalidOrderException("Customer ID cannot be null or empty");
        }

        List<Order> orders =
                Optional.ofNullable(orderRepository.findByCustomerId(customerId))
                        .orElse(List.of());

        if (orders.isEmpty()) {
            throw new OrderNotFoundException(
                    "No orders found for customer ID: " + customerId);
        }

        return orders;
    }



}

package com.company.order.service;

import com.company.order.exception.ComplianceViolationException;
import com.company.order.model.Customer;
import com.company.order.model.Order;
import com.company.order.model.OrderItem;

public class ComplianceService {

    private static final double MAX_ORDER_TOTAL = 5_00_000;

    public void validateOrder(Order order, Customer customer) {
        validateCustomer(customer);
        validateItems(order);
        validateTotalAmount(order);
    }

    private void validateCustomer(Customer customer) {
        if (!customer.isActive()) {
            throw new ComplianceViolationException("Customer is inactive");
        }
    }

    private void validateItems(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new ComplianceViolationException("Order has no items");
        }

        for (OrderItem item : order.getItems()) {
            if (item.getPrice() <= 0) {
                throw new ComplianceViolationException("Item price must be greater than 0");
            }
            if (item.getQuantity() <= 0) {
                throw new ComplianceViolationException("Item quantity must be greater than 0");
            }
        }
    }

    private void validateTotalAmount(Order order) {
        double totalAmount = order.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        if (totalAmount > MAX_ORDER_TOTAL) {
            throw new ComplianceViolationException("Order total cannot exceed ₹5,00,000");
        }
    }

}

package com.company.order.service;

import com.company.order.exception.ComplianceViolationException;
import com.company.order.model.Customer;
import com.company.order.model.Order;
import com.company.order.model.OrderItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ComplianceServiceTest {

    private ComplianceService complianceService;

    @BeforeEach
    void setUp() {
        complianceService = new ComplianceService();
    }

    @Test
    @DisplayName("Should throw exception if customer is inactive")
    void shouldThrowIfCustomerInactive() {
        Customer customer = new Customer(false);
        Order order = new Order();
        order.addItem(new OrderItem(100, 1));

        assertThrows(ComplianceViolationException.class,
                () -> complianceService.validateOrder(order, customer),
                "Inactive customer should not be allowed to create order");
    }

    @Test
    @DisplayName("Should throw exception if order has no items")
    void shouldThrowIfNoItems() {
        Customer customer = new Customer(true);
        Order emptyOrder = new Order();

        assertThrows(ComplianceViolationException.class,
                () -> complianceService.validateOrder(emptyOrder, customer),
                "Order with no items should fail compliance");
    }

    @Test
    @DisplayName("Should throw exception if item price is <= 0")
    void shouldThrowIfItemPriceInvalid() {
        Customer customer = new Customer(true);
        OrderItem invalidItem = new OrderItem(0, 1);
        Order order = new Order();
        order.addItem(invalidItem);

        assertThrows(ComplianceViolationException.class,
                () -> complianceService.validateOrder(order, customer),
                "Item price must be greater than 0");
    }

    @Test
    @DisplayName("Should throw exception if quantity <= 0")
    void shouldThrowIfQuantityInvalid() {
        Customer customer = new Customer(true);
        OrderItem invalidItem = new OrderItem(100, 0);
        Order order = new Order();
        order.addItem(invalidItem);

        assertThrows(ComplianceViolationException.class,
                () -> complianceService.validateOrder(order, customer),
                "Item quantity must be greater than 0");
    }

    @Test
    @DisplayName("Should throw exception if total amount exceeds 5 lakh")
    void shouldThrowIfTotalAmountExceedsLimit() {
        Customer customer = new Customer(true);
        OrderItem expensiveItem = new OrderItem(6_00_000, 1);
        Order order = new Order();
        order.addItem(expensiveItem);

        assertThrows(ComplianceViolationException.class,
                () -> complianceService.validateOrder(order, customer),
                "Order total cannot exceed ₹5,00,000");
    }
}

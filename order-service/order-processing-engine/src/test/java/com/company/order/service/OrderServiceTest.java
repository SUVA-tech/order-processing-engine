package com.company.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.company.order.exception.InvalidOrderException;
import com.company.order.exception.OrderNotFoundException;
import com.company.order.model.Customer;
import com.company.order.model.Order;
import com.company.order.model.OrderItem;
import com.company.order.model.OrderStatus;
import com.company.order.repository.OrderRepository;

class OrderServiceTest {

    private OrderRepository repository;
    private OrderServiceImpl service;

    @BeforeEach
    void setup() {
        repository = mock(OrderRepository.class);
        service = new OrderServiceImpl(repository);
    }

    // -------------------- Order Creation --------------------

    @Test
    @DisplayName("Order creation should fail when customer is inactive")
    void shouldFailToCreateOrderWhenCustomerIsInactive() {

        // Arrange
        Customer inactiveCustomer = new Customer(false);
        Order order = new Order();

        // Act & Assert
        assertThrows(
                InvalidOrderException.class,
                () -> service.createOrder(order, inactiveCustomer),
                "Expected InvalidOrderException when customer is inactive"
        );
    }

    @Test
    @DisplayName("Order creation should fail when order has no items")
    void shouldFailToCreateOrderWhenOrderHasNoItems() {

        // Arrange
        Customer activeCustomer = new Customer(true);
        Order emptyOrder = new Order();

        // Act & Assert
        assertThrows(
                InvalidOrderException.class,
                () -> service.createOrder(emptyOrder, activeCustomer),
                "Expected InvalidOrderException when order has no items"
        );
    }

    @Test
    @DisplayName("Order creation should fail when item price is zero or negative")
    void shouldFailToCreateOrderWhenItemPriceIsInvalid() {

        // Arrange
        Customer activeCustomer = new Customer(true);
        OrderItem itemWithInvalidPrice = new OrderItem(0, 1);
        Order order = new Order();
        order.addItem(itemWithInvalidPrice);

        // Act & Assert
        assertThrows(
                InvalidOrderException.class,
                () -> service.createOrder(order, activeCustomer),
                "Expected InvalidOrderException when item price is invalid"
        );
    }

    @Test
    @DisplayName("Order creation should fail when item quantity is zero or negative")
    void shouldFailToCreateOrderWhenItemQuantityIsInvalid() {

        // Arrange
        Customer activeCustomer = new Customer(true);
        OrderItem itemWithInvalidQuantity = new OrderItem(100, 0);
        Order order = new Order();
        order.addItem(itemWithInvalidQuantity);

        // Act & Assert
        assertThrows(
                InvalidOrderException.class,
                () -> service.createOrder(order, activeCustomer),
                "Expected InvalidOrderException when item quantity is invalid"
        );
    }

    @Test
    @DisplayName("Order creation should fail when total order amount exceeds limit")
    void shouldFailToCreateOrderWhenTotalAmountExceedsLimit() {

        // Arrange
        Customer activeCustomer = new Customer(true);
        OrderItem expensiveItem = new OrderItem(500_001, 1);
        Order order = new Order();
        order.addItem(expensiveItem);

        // Act & Assert
        assertThrows(
                InvalidOrderException.class,
                () -> service.createOrder(order, activeCustomer),
                "Expected InvalidOrderException when total amount exceeds limit"
        );
    }

    // -------------------- Order Cancellation --------------------

    @Test
    @DisplayName("Order cancellation should fail when reason is empty")
    void shouldFailToCancelOrderWhenReasonIsEmpty() {

        // Arrange
        Order order = new Order();

        // Act & Assert
        assertThrows(
                InvalidOrderException.class,
                () -> service.cancelOrder(order, ""),
                "Expected exception when cancellation reason is empty"
        );
    }

    @Test
    @DisplayName("Order cancellation should fail when order is already cancelled")
    void shouldFailToCancelAlreadyCancelledOrder() {

        // Arrange
        Order order = new Order();
        order.cancel("Customer requested");

        // Act & Assert
        assertThrows(
                InvalidOrderException.class,
                () -> service.cancelOrder(order, "Another reason"),
                "Expected exception when cancelling an already cancelled order"
        );
    }

    @Test
    @DisplayName("Order cancellation should succeed for valid order and reason")
    void shouldCancelOrderSuccessfully() {

        // Arrange
        Order order = new Order();

        // Act
        service.cancelOrder(order, "Customer requested");

        // Assert
        assertEquals(
                OrderStatus.CANCELLED,
                order.getStatus(),
                "Order status should be CANCELLED after cancellation"
        );
    }

    // -------------------- Order Retrieval --------------------

    @Test
    @DisplayName("Should return order when valid order ID is provided")
    void shouldReturnOrderWhenOrderIdExists() {

        // Arrange
        Order order = new Order();
        when(repository.findById("ORD1")).thenReturn(Optional.of(order));

        // Act
        Order result = service.getOrderById("ORD1");

        // Assert
        assertNotNull(result, "Order should be returned when ID exists");
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order ID is not found")
    void shouldFailWhenOrderIdDoesNotExist() {

        // Arrange
        when(repository.findById("ORD404")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                OrderNotFoundException.class,
                () -> service.getOrderById("ORD404"),
                "Expected OrderNotFoundException when order ID does not exist"
        );
    }

    @Test
    @DisplayName("Should return orders when customer has orders")
    void shouldReturnOrdersForCustomer() {

        // Arrange
        List<Order> orders = List.of(new Order(), new Order());
        when(repository.findByCustomerId("CUST1")).thenReturn(orders);

        // Act
        List<Order> result = service.getOrdersByCustomerId("CUST1");

        // Assert
        assertEquals(2, result.size(), "Customer orders should be returned");
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when customer has no orders")
    void shouldFailWhenCustomerHasNoOrders() {

        // Arrange
        when(repository.findByCustomerId("CUST2")).thenReturn(List.of());

        // Act & Assert
        assertThrows(
                OrderNotFoundException.class,
                () -> service.getOrdersByCustomerId("CUST2"),
                "Expected exception when customer has no orders"
        );
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when repository returns null")
    void shouldFailWhenRepositoryReturnsNull() {

        // Arrange
        when(repository.findByCustomerId("CUST3")).thenReturn(null);

        // Act & Assert
        assertThrows(
                OrderNotFoundException.class,
                () -> service.getOrdersByCustomerId("CUST3"),
                "Expected exception when repository returns null"
        );
    }
}

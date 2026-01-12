package com.company.order.model;

public class Customer {

    private final boolean active;

    public Customer(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}

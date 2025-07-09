package org.sds.sonizone.order.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class Order {
    private UUID id;
    private String customerName;
    private String product;
    private int quantity;
    private LocalDate orderDate;

    // Constructors, Getters, Setters
    public Order() {}

    public Order(UUID id, String customerName, String product, int quantity, LocalDate orderDate) {
        this.id = id;
        this.customerName = customerName;
        this.product = product;
        this.quantity = quantity;
        this.orderDate = orderDate;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
}

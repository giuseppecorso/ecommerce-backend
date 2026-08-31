package com.giuseppe.ecommerce.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderResponse {
    private Long id;
    private String customerName;
    private LocalDateTime dateOrder;
    private String status;
    private List<OrderItemResponse> items;

    public OrderResponse(Long id, String customerName, LocalDateTime dateOrder, String status, List<OrderItemResponse> items) {
        this.id = id;
        this.customerName = customerName;
        this.dateOrder = dateOrder;
        this.status = status;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDateTime getDateOrder() {
        return dateOrder;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}

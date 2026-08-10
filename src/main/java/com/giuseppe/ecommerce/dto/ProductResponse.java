package com.giuseppe.ecommerce.dto;

import java.math.BigDecimal;

public class ProductResponse {
    private Long  id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;

    public ProductResponse(Long id, String name, String description, BigDecimal price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }
}

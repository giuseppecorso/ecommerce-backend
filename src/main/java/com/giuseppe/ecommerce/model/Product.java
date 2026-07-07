package com.giuseppe.ecommerce.model;

import java.math.BigDecimal;

public class Product {
    private String name;
    private String description;
    private Long id;
    private BigDecimal price;
    private int stockQuantity;

    public Product() {
    };

    public Product(Long id, String name, String description, BigDecimal price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    };

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

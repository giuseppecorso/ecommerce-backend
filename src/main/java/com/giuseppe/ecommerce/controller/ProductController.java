package com.giuseppe.ecommerce.controller;

import com.giuseppe.ecommerce.model.Product;
import com.giuseppe.ecommerce.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.List;

@RestController
public class ProductController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/products/demo")
    public Product getProduct() {
        Long id = 1L;
        BigDecimal price = new BigDecimal("19.99");
        String name = "T-Shirt Nike Vintage";
        String description = "T-Shirt Nike stile Vintage colore bianco con stampa";
        return new Product(id, name, description, price, 10);
    }

    @GetMapping("/api/products")
    public List<Product> getProducts() {
        return repository.findAll();
    }


}

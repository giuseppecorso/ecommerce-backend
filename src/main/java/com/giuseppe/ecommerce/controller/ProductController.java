package com.giuseppe.ecommerce.controller;

import com.giuseppe.ecommerce.model.Product;
import com.giuseppe.ecommerce.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    @PostMapping("/api/products")
    public Product addProduct(@RequestBody Product prod) {
        return repository.save(prod);
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Optional<Product> box = repository.findById(id);
        if (box.isPresent()) {
            return ResponseEntity.ok(box.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

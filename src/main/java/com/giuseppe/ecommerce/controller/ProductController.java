package com.giuseppe.ecommerce.controller;

import com.giuseppe.ecommerce.model.Product;
import com.giuseppe.ecommerce.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/products")
    public List<Product> getProducts() {

        return repository.findAll();
    }

    @PostMapping("/api/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product prod) {

        prod.setId(null);

        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(prod));
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

    @DeleteMapping("/api/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Optional<Product> box = repository.findById(id);
        if (box.isPresent()) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/api/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,@RequestBody Product prod) {
        prod.setId(id);
        Optional<Product> box = repository.findById(id);
        if (box.isPresent()) {
            repository.save(prod);
            return ResponseEntity.ok(prod);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
package com.giuseppe.ecommerce.service;

import com.giuseppe.ecommerce.model.Product;
import com.giuseppe.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {

        this.repository = repository;
    }

    public List<Product> getAllProducts() {

        return repository.findAll();
    }

    public Optional<Product> getProductById(Long id) {

        return repository.findById(id);
    }
}

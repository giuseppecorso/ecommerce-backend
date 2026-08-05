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

    public Product addNewProduct(Product prod) {

        prod.setId(null);

        return repository.save(prod);
    }

    public boolean deleteById(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Optional<Product> updateProduct(Long id, Product prod) {
        prod.setId(id);
        if (repository.existsById(id)) {
            return Optional.of(repository.save(prod));
        } else {
            return Optional.empty();
        }
    }
}

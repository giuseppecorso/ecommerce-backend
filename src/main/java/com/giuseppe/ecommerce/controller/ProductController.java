package com.giuseppe.ecommerce.controller;

import com.giuseppe.ecommerce.dto.ProductRequest;
import com.giuseppe.ecommerce.dto.ProductResponse;
import com.giuseppe.ecommerce.model.Product;
import com.giuseppe.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Product Controller")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/api/products")
    @Operation(summary = "Get all products")
    public List<ProductResponse> getProducts() {

        List<ProductResponse> responses = new ArrayList<>();

        for (Product p :  service.getAllProducts()) {
            responses.add(new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStockQuantity()));
        }

        return responses;
    }

    @PostMapping("/api/products")
    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Invalid product data")
    })
    public ResponseEntity<ProductResponse> addProduct(@RequestBody @Valid ProductRequest req) {

        Product newProduct = new Product(null, req.getName(), req.getDescription(), req.getPrice(), req.getStockQuantity());

        Product saved = service.addNewProduct(newProduct);

        ProductResponse resp = new ProductResponse(saved.getId(),  saved.getName(), saved.getDescription(), saved.getPrice(), saved.getStockQuantity());

        return new ResponseEntity<>(resp, HttpStatus.CREATED);

    }

    @GetMapping("/api/products/{id}")
    @Operation(summary = "Get product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful"),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content)
    })
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        Optional<Product> box = service.getProductById(id);
        if (box.isPresent()) {
            Product found = box.get();
            ProductResponse resp = new ProductResponse(found.getId(), found.getName(), found.getDescription(), found.getPrice(), found.getStockQuantity());
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/products/{id}")
    @Operation(summary = "Delete a product by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (service.deleteById(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/api/products/{id}")
    @Operation(summary = "Update a product by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid product data")
    })
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,@RequestBody @Valid ProductRequest req) {

        Product productClient = new Product(id, req.getName(), req.getDescription(), req.getPrice(), req.getStockQuantity());

        Optional<Product> box = service.updateProduct(id, productClient);

        if (box.isPresent()) {
            Product saved = box.get();
            ProductResponse resp = new ProductResponse(saved.getId(), saved.getName(), saved.getDescription(), saved.getPrice(), saved.getStockQuantity());
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
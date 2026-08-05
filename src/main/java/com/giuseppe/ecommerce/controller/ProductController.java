package com.giuseppe.ecommerce.controller;

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
    public List<Product> getProducts() {

        return service.getAllProducts();
    }

    @PostMapping("/api/products")
    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Invalid product data")
    })
    public ResponseEntity<Product> addProduct(@RequestBody @Valid Product prod) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.addNewProduct(prod));

    }

    @GetMapping("/api/products/{id}")
    @Operation(summary = "Get product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful"),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content)
    })
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Optional<Product> box = service.getProductById(id);
        if (box.isPresent()) {
            return ResponseEntity.ok(box.get());
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
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,@RequestBody @Valid Product prod) {

        Optional<Product> box = service.updateProduct(id, prod);

        if (box.isPresent()) {
            return ResponseEntity.ok(box.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
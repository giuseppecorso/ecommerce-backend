package com.giuseppe.ecommerce.controller;

import com.giuseppe.ecommerce.dto.*;
import com.giuseppe.ecommerce.model.Order;
import com.giuseppe.ecommerce.model.OrderItem;
import com.giuseppe.ecommerce.service.OrderService;
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
@Tag(name = "Order Controller")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/api/orders/{id}")
    @Operation(summary = "Get order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful"),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Optional<Order> box = orderService.getOrderById(id);

        if (box.isPresent()) {
            Order found = box.get();
            List<OrderItemResponse> items = new ArrayList<>();
            for (OrderItem item : found.getItems()) {
                items.add(new OrderItemResponse(item.getProduct().getId(), item.getProduct().getName(), item.getQuantity(), item.getPrice()));
            }

            OrderResponse resp = new OrderResponse(found.getId(), found.getCustomerName(), found.getDateOrder(), found.getStatus(), items);

            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping ("/api/orders")
    @Operation (summary = "Create a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Invalid order data"),
            @ApiResponse(responseCode = "404", description = "Invalid product id")
    })
    public ResponseEntity<OrderResponse> addOrder(@RequestBody @Valid OrderRequest req) {

        Optional<Order> box = orderService.createOrder(req);

        if (box.isPresent()) {
            Order found = box.get();
            List<OrderItemResponse> items = new ArrayList<>();
            for (OrderItem item : found.getItems()) {
                items.add(new OrderItemResponse(item.getProduct().getId(), item.getProduct().getName(), item.getQuantity(), item.getPrice()));
            }
            OrderResponse resp = new OrderResponse(found.getId(), found.getCustomerName(), found.getDateOrder(), found.getStatus(), items);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}

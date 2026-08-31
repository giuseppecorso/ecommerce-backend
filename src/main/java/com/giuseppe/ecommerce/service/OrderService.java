package com.giuseppe.ecommerce.service;

import com.giuseppe.ecommerce.model.Order;
import com.giuseppe.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {

        this.repository = repository;
    }

    public Optional<Order> getOrderById(Long id) {

        return repository.findById(id);
    }
}

package com.giuseppe.ecommerce.service;

import com.giuseppe.ecommerce.dto.OrderItemRequest;
import com.giuseppe.ecommerce.dto.OrderRequest;
import com.giuseppe.ecommerce.exception.InsufficientStockException;
import com.giuseppe.ecommerce.exception.InvalidOrderStatusException;
import com.giuseppe.ecommerce.model.*;
import com.giuseppe.ecommerce.repository.OrderItemRepository;
import com.giuseppe.ecommerce.repository.OrderRepository;
import com.giuseppe.ecommerce.repository.ProductRepository;
import com.giuseppe.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, OrderItemRepository orderItemRepository, UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }

    public Optional<Order> getOrderById(Long id, String username, boolean isAdmin) {

        Optional<Order> order = orderRepository.findById(id);

        if (order.isEmpty()) {
            return Optional.empty();
        } else if (isAdmin) {
            return order;
        } else if (order.get().getUser().getUsername().equals(username)) {
            return order;
        } else  {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<Order> createOrder(OrderRequest req, String username) {
        for (OrderItemRequest itemReq : req.getItems()) {
            Optional<Product> box = productRepository.findById(itemReq.getProductId());
            if (box.isEmpty())
                return Optional.empty();
        }

        User user = userRepository.findByUsername(username).orElseThrow();

        Order order = new Order();
        order.setUser(user);
        order.setDateOrder(LocalDateTime.now());
        order.setStatus("NEW");

        Order saved = orderRepository.save(order);

        for (OrderItemRequest itemReq : req.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId()).get();
            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product " + product.getId()
                        + ": requested " + itemReq.getQuantity()
                        + ", available " + product.getStockQuantity());
            }
            product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
            productRepository.save(product);
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(saved);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(product.getPrice());
            saved.getItems().add(orderItem);
            orderItemRepository.save(orderItem);
        }

        return Optional.of(saved);
    }

    public List<Order> getOrders(String username, boolean isAdmin) {
        if (isAdmin) {
            return orderRepository.findAll();
        } else  {
            return orderRepository.findByUserUsername(username);
        }
    }

    public Optional<Order> updateStatus(Long id, String status) {
        try {
            OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStatusException("Invalid order status: " + status + ". Accepted values are NEW, PAID, SHIPPED.");
        }
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            Order found = order.get();
            found.setStatus(status);
            return Optional.of(orderRepository.save(found));
        } else {
            return Optional.empty();
        }
    }
}

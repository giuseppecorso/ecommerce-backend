package com.giuseppe.ecommerce.service;

import com.giuseppe.ecommerce.dto.OrderItemRequest;
import com.giuseppe.ecommerce.dto.OrderRequest;
import com.giuseppe.ecommerce.model.Order;
import com.giuseppe.ecommerce.model.OrderItem;
import com.giuseppe.ecommerce.model.Product;
import com.giuseppe.ecommerce.repository.OrderItemRepository;
import com.giuseppe.ecommerce.repository.OrderRepository;
import com.giuseppe.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, OrderItemRepository orderItemRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Optional<Order> getOrderById(Long id) {

        return orderRepository.findById(id);
    }

    public Optional<Order> createOrder(OrderRequest req) {
        for (OrderItemRequest itemReq : req.getItems()) {
            Optional<Product> box = productRepository.findById(itemReq.getProductId());
            if (box.isEmpty())
                return Optional.empty();
        }

        Order order = new Order();
        order.setCustomerName(req.getCustomerName());
        order.setDateOrder(LocalDateTime.now());
        order.setStatus("NEW");

        Order saved = orderRepository.save(order);

        for (OrderItemRequest itemReq : req.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId()).get();
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

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> updateStatus(Long id, String status) {
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

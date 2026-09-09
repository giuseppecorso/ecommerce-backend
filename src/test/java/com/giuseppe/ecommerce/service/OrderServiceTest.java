package com.giuseppe.ecommerce.service;

import com.giuseppe.ecommerce.dto.OrderItemRequest;
import com.giuseppe.ecommerce.dto.OrderRequest;
import com.giuseppe.ecommerce.exception.InvalidOrderStatusException;
import com.giuseppe.ecommerce.model.Order;
import com.giuseppe.ecommerce.repository.OrderItemRepository;
import com.giuseppe.ecommerce.repository.OrderRepository;
import com.giuseppe.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {
    OrderRepository orderRepository;
    ProductRepository productRepository;
    OrderItemRepository orderItemRepository;
    OrderService orderService;

    @BeforeEach
    void setup() {
        orderRepository = Mockito.mock(OrderRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        orderItemRepository = Mockito.mock(OrderItemRepository.class);
        orderService = new OrderService(orderRepository, productRepository, orderItemRepository);
    }

    @Test
    void updateStatusThrowsWhenStatusIsInvalid () {
        assertThrows(InvalidOrderStatusException.class, () -> orderService.updateStatus(1L, "PATATA"));
    }

    @Test
    void updateStatusReturnsUpdatedOrderWhenStatusIsValid() {
        Order order = new Order();
        order.setId(1L);
        order.setCustomerName("Giuseppe");
        order.setStatus("NEW");

        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(order)).thenReturn(order);

        Optional<Order> result = orderService.updateStatus(1L, "PAID");

        assertTrue(result.isPresent());
        assertEquals("PAID",result.get().getStatus());
    }

    @Test
    void createOrderReturnsEmptyWhenProductDoesNotExist() {
        OrderItemRequest  orderItemRequest = new OrderItemRequest();
        orderItemRequest.setProductId(999L);
        orderItemRequest.setQuantity(1);

        OrderRequest  orderRequest = new OrderRequest();
        orderRequest.setCustomerName("Martina");
        orderRequest.setItems(List.of(orderItemRequest));

        Optional<Order> result = orderService.createOrder(orderRequest);

        assertTrue(result.isEmpty());
    }
}

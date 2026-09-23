package com.giuseppe.ecommerce.service;

import com.giuseppe.ecommerce.dto.OrderItemRequest;
import com.giuseppe.ecommerce.dto.OrderRequest;
import com.giuseppe.ecommerce.exception.InsufficientStockException;
import com.giuseppe.ecommerce.exception.InvalidOrderStatusException;
import com.giuseppe.ecommerce.model.Order;
import com.giuseppe.ecommerce.model.Product;
import com.giuseppe.ecommerce.model.User;
import com.giuseppe.ecommerce.repository.OrderItemRepository;
import com.giuseppe.ecommerce.repository.OrderRepository;
import com.giuseppe.ecommerce.repository.ProductRepository;
import com.giuseppe.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {
    OrderRepository orderRepository;
    ProductRepository productRepository;
    OrderItemRepository orderItemRepository;
    OrderService orderService;
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        orderRepository = Mockito.mock(OrderRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        orderItemRepository = Mockito.mock(OrderItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        orderService = new OrderService(orderRepository, productRepository, orderItemRepository,  userRepository);
    }

    @Test
    void updateStatusThrowsWhenStatusIsInvalid () {
        assertThrows(InvalidOrderStatusException.class, () -> orderService.updateStatus(1L, "PATATA"));
    }

    @Test
    void updateStatusReturnsUpdatedOrderWhenStatusIsValid() {
        Order order = new Order();
        order.setId(1L);
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
        orderRequest.setItems(List.of(orderItemRequest));

        Optional<Order> result = orderService.createOrder(orderRequest, "martina");

        assertTrue(result.isEmpty());
    }

    @Test
    void getOrderByIdReturnsOrderWhenUserIsOwner() {
        User luigi = new User();
        luigi.setUsername("luigi");

        Order order = new Order();
        order.setId(5L);
        order.setUser(luigi);

        Mockito.when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrderById(5L, "luigi", false);

        assertTrue(result.isPresent());
        assertEquals(5L, result.get().getId());
    }
    @Test
    void getOrderByIdReturnsEmptyWhenUserIsNotOwner() {
        User luigi = new User();
        luigi.setUsername("luigi");

        Order order = new Order();
        order.setId(5L);
        order.setUser(luigi);

        Mockito.when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrderById(5L, "mario", false);

        assertTrue(result.isEmpty());

    }

    @Test
    void createOrderThrowsWhenStockIsInsufficient() {
        Product polo = new Product(2L, "Polo", "Polo rossa", new BigDecimal("39.99"), 1);
        Mockito.when(productRepository.findById(2L)).thenReturn(Optional.of(polo));

        User mario = new User();
        mario.setUsername("mario");
        Mockito.when(userRepository.findByUsername("mario")).thenReturn(Optional.of(mario));

        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(new Order());

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(2L);
        itemRequest.setQuantity(5);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setItems(List.of(itemRequest));

        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(orderRequest, "mario"));
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getOrderByIdReturnsOrderWhenUserIsAdmin() {
        User luigi = new User();
        luigi.setUsername("luigi");

        Order order = new Order();
        order.setId(7L);
        order.setUser(luigi);

        Mockito.when(orderRepository.findById(7L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrderById(7L, "admin", true);
        assertTrue(result.isPresent());
        assertEquals(7L, result.get().getId());
    }
}

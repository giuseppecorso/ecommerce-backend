package com.giuseppe.ecommerce.repository;

import com.giuseppe.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

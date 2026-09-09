package com.giuseppe.ecommerce.mapper;

import com.giuseppe.ecommerce.dto.OrderItemResponse;
import com.giuseppe.ecommerce.dto.OrderResponse;
import com.giuseppe.ecommerce.model.Order;
import com.giuseppe.ecommerce.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {
    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            items.add(new OrderItemResponse(item.getProduct().getId(), item.getProduct().getName(), item.getQuantity(), item.getPrice()));
        }
        OrderResponse resp = new OrderResponse(order.getId(), order.getCustomerName(), order.getDateOrder(), order.getStatus(), items);
        return resp;
    }
}

package com.agibank.order.service;

import com.agibank.order.domain.model.dto.OrderCreatedEvent;
import com.agibank.order.domain.model.entity.Order;
import com.agibank.order.domain.model.entity.OrderStatus;
import com.agibank.order.domain.repository.OrderRepository;
import com.agibank.order.kafka.OrderProducer;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    public Order createOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(savedOrder.getId());
        event.setCustomerName(savedOrder.getCustomerName());
        event.setTotalAmount(savedOrder.getTotalAmount());

        orderProducer.sendOrderCreatedEvent(event);
        return savedOrder;
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        return orderRepository.findById(orderId)
            .map(order -> {
                order.setStatus(status);
                return orderRepository.save(order);
            })
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
}

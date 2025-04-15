package com.agibank.order.service;

import com.agibank.order.domain.model.dto.OrderCreatedEvent;
import com.agibank.order.domain.model.entity.Order;
import com.agibank.order.domain.model.entity.OrderStatus;
import com.agibank.order.domain.repository.OrderRepository;
import com.agibank.order.kafka.OrderProducer;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    public Order createOrder(Order order) {
        log.info("Creating new order for customer: {}", order.getCustomerId());
        Order savedOrder = orderRepository.save(order);
        
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(savedOrder.getId());
        event.setCustomerId(savedOrder.getCustomerId());
        event.setTotalAmount(savedOrder.getTotalAmount());

        log.info("Sending order created event for order ID: {}", savedOrder.getId());
        orderProducer.sendOrderCreatedEvent(event);
        return savedOrder;
    }

    public void updateOrderStatus(Long orderId, OrderStatus status) {
        log.info("Updating order status to {} for order ID: {}", status, orderId);
        orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(status);
                    Order updatedOrder = orderRepository.save(order);
                    log.info("Order status updated successfully for order ID: {}", orderId);
                    return updatedOrder;
                })
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {}", orderId);
                    return new RuntimeException("Order not found");
                });
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
}

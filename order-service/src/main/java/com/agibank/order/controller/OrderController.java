package com.agibank.order.controller;

import com.agibank.order.domain.model.dto.OrderRequest;
import com.agibank.order.domain.model.entity.Order;
import com.agibank.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Received request to create order for customer: {}", orderRequest.getCustomerId());
        Order order = new Order();
        order.setCustomerId(orderRequest.getCustomerId());
        order.setTotalAmount(orderRequest.getTotalAmount());
        Order createdOrder = orderService.createOrder(order);
        log.info("Order created successfully with ID: {}", createdOrder.getId());
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        log.info("Retrieving order with ID: {}", id);
        return orderService.findById(id)
                .map(order -> {
                    log.info("Order found: {}", order);
                    return ResponseEntity.ok(order);
                })
                .orElseGet(() -> {
                    log.warn("Order not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}

package com.agibank.order.kafka;

import com.agibank.order.domain.model.entity.OrderStatus;
import com.agibank.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {
    private final OrderService  orderService;

    @KafkaListener(topics = "payment-created", groupId = "order-group")
    public void listenPaymentCreatedEvent(String message) {
        orderService.updateOrderStatus(Long.parseLong(message), OrderStatus.COMPLETED);
    }
}

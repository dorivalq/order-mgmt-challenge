package com.agibank.payment.kafka;

import com.agibank.payment.domain.dto.OrderCreatedEvent;
import com.agibank.payment.domain.model.dto.PaymentEvent;
import com.agibank.payment.domain.enums.PaymentStatus;
import com.agibank.payment.domain.exception.PaymentException;
import com.agibank.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {
    private final PaymentService paymentService;
    private final PaymentProducer paymentProducer;

    @KafkaListener(topics = "order-created", groupId = "order-group")
    public void listenOrderCreatedEvent(String message) {
        log.info("Received order created event: {}", message);
        com.agibank.payment.domain.dto.OrderCreatedEvent orderCreatedEvent = new ObjectMapper().convertValue(message, OrderCreatedEvent.class);
        try {
            log.info("Processing payment for order ID: {}", orderCreatedEvent.getId());
            paymentService.processPayment(orderCreatedEvent);
        } catch (Exception e) {
            log.error("Error processing payment for order ID: {}. Error: {}", orderCreatedEvent.getId(), e.getMessage());
            PaymentEvent paymentEvent = new PaymentEvent(null, orderCreatedEvent.getId(), orderCreatedEvent.getTotalAmount(), PaymentStatus.FAILED);
            paymentProducer.sendMessage(paymentEvent);
            throw new PaymentException("Failed to process payment for order: " + orderCreatedEvent.getId(), e);
        }
    }
}

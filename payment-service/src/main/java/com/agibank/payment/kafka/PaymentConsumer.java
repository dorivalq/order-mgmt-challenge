package com.agibank.payment.kafka;

import com.agibank.payment.domain.dto.OrderEvent;
import com.agibank.payment.domain.dto.PaymentEvent;
import com.agibank.payment.domain.enums.PaymentStatus;
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
    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void listenOrderCreatedEvent(String message) {
        log.info("Received order created event: {}", message);
        OrderEvent orderEvent = new ObjectMapper().convertValue(message, OrderEvent.class);
        try {
            log.info("Processing payment for order ID: {}", orderEvent.getId());
            paymentService.processPayment(orderEvent);
        } catch (Exception e) {
            log.error("Error processing payment for order ID: {}. Error: {}", orderEvent.getId(), e.getMessage());
            PaymentEvent paymentEvent = new PaymentEvent(null, orderEvent.getId(), orderEvent.getTotalAmount(), PaymentStatus.FAILED);
            paymentProducer.sendMessage(paymentEvent);
            throw new PaymentException("Failed to process payment for order: " + orderEvent.getId(), e);
        }
    }
}

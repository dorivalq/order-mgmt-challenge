package com.agibank.payment.kafka;

import com.agibank.payment.domain.model.dto.PaymentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public void sendMessage(PaymentEvent paymentEvent) {
        log.info("Sending payment event for order ID: {}", paymentEvent.getOrderId());
        String eventString = new ObjectMapper().convertValue(paymentEvent, String.class);
        kafkaTemplate.send("order-created", paymentEvent.toString(), paymentEvent)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Payment event sent successfully for order ID: {}", paymentEvent.getOrderId());
                } else {
                    log.error("Failed to send payment event for order ID: {}. Error: {}", 
                        paymentEvent.getOrderId(), ex.getMessage());
                }
            });
    }
}

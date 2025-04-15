package com.agibank.payment.service;

import com.agibank.payment.domain.dto.DepositRequest;
import com.agibank.payment.domain.dto.OrderEvent;
import com.agibank.payment.domain.entity.Payment;
import com.agibank.payment.domain.enums.PaymentStatus;
import com.agibank.payment.domain.repository.PaymentRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public void processPayment(@NotNull OrderEvent orderEvent) {
        log.info("Processing payment for order ID: {}", orderEvent.getId());
        Payment payment = paymentRepository.findTopByCustomerIdOrderByIdDesc(orderEvent.getCustomerId());
        BigDecimal totalAmount = orderEvent.getTotalAmount().subtract(orderEvent.getTotalAmount());
        
        if (payment.getAmount().compareTo(orderEvent.getTotalAmount()) >= 0 ) {
            log.info("Sufficient funds available for order ID: {}", orderEvent.getId());
            payment = new Payment();
            payment.setOrderId(orderEvent.getId());
            payment.setAmount(orderEvent.getTotalAmount());
            payment.setStatus(PaymentStatus.PENDING);
        } else {
            log.warn("Insufficient funds for order ID: {}", orderEvent.getId());
        }

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed and saved with ID: {} for order ID: {}", savedPayment.getId(), orderEvent.getId());
    }

    public void depositFounds(@NotNull DepositRequest request) {
        log.info("Processing deposit request for customer ID: {}", request.getCustomerId());
        Payment payment = paymentRepository.findTopByCustomerIdOrderByIdDesc(request.getCustomerId());
        if (payment == null) {
            log.info("Creating new payment record for customer ID: {}", request.getCustomerId());
            payment = new Payment(null, null, request.getAmount(), request.getCustomerId(), PaymentStatus.COMPLETED, LocalDateTime.now());
        } else {
            log.info("Updating existing payment record for customer ID: {}", request.getCustomerId());
            payment.setAmount(payment.getAmount().add(request.getAmount()));
        }
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Deposit processed successfully. New balance for customer {}: {}", request.getCustomerId(), savedPayment.getAmount());
    }
}

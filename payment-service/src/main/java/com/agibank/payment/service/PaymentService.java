package com.agibank.payment.service;

import com.agibank.payment.domain.exception.PaymentException;
import com.agibank.payment.domain.model.dto.DepositRequest;
import com.agibank.payment.domain.model.entity.Payment;
import com.agibank.payment.domain.enums.PaymentStatus;
import com.agibank.payment.domain.repository.PaymentRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public void processPayment(@NotNull com.agibank.payment.domain.dto.OrderCreatedEvent orderCreatedEvent) {
        log.info("Processing payment for order ID: {}", orderCreatedEvent.getId());
        Payment payment = paymentRepository.findTopByCustomerIdOrderByIdDesc(orderCreatedEvent.getCustomerId());
        BigDecimal totalAmount = orderCreatedEvent.getTotalAmount().subtract(orderCreatedEvent.getTotalAmount());
        
        if (payment.getAmount().compareTo(orderCreatedEvent.getTotalAmount()) >= 0 ) {
            log.info("Sufficient funds available for order ID: {}", orderCreatedEvent.getId());
            payment = new Payment();
            payment.setOrderId(orderCreatedEvent.getId());
            payment.setAmount(totalAmount);
            payment.setStatus(PaymentStatus.PENDING);
        } else {
            log.warn("Insufficient funds for order ID: {}", orderCreatedEvent.getId());
            throw new PaymentException("Insufficient funds for order ID: " + orderCreatedEvent.getId());
        }

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed and saved with ID: {} for order ID: {}", savedPayment.getId(), orderCreatedEvent.getId());
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

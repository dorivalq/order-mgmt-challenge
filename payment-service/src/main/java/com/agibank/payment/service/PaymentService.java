package com.agibank.payment.service;

import com.agibank.payment.domain.dto.DepositRequest;
import com.agibank.payment.domain.dto.OrderDTO;
import com.agibank.payment.domain.entity.Payment;
import com.agibank.payment.domain.enums.PaymentStatus;
import com.agibank.payment.domain.repository.PaymentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void processPayment(OrderDTO orderEvent) {
        Payment payment = new Payment();
        payment.setOrderId(orderEvent.getId());
        payment.setAmount(orderEvent.getTotalAmount());
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);
        // Implement payment processing logic here
    }

    public void depositFounds(@NotNull @Valid DepositRequest request) {
        Payment payment = paymentRepository.findTopByCustomerIdOrderByIdDesc(request.getCustomerId());
        if (payment == null) {
            payment = new Payment(null, 0L, request.getAmount(), request.getCustomerId(), PaymentStatus.COMPLETED, LocalDateTime.now());
        } else {
            payment.setAmount(payment.getAmount().add(request.getAmount()));
        }
    }
}

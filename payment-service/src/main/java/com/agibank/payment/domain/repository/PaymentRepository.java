package com.agibank.payment.domain.repository;

import com.agibank.payment.domain.entity.Payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);

    Payment findTopByCustomerIdOrderByIdDesc(Long customerId);
}

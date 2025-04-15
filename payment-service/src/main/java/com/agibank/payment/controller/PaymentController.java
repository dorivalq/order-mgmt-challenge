package com.agibank.payment.controller;

import com.agibank.payment.domain.model.dto.DepositRequest;
import com.agibank.payment.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    public PaymentController(PaymentService  paymentService) {
        this.paymentService = paymentService;
    }
    @PostMapping("/deposit")
    public String deposit(@NotNull @Valid @RequestBody DepositRequest request ) {
        paymentService.depositFounds(request);
        return "Payment processed successfully";
    }

}

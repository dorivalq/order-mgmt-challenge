package com.agibank.payment.domain.model.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;

import lombok.Data;

@Data
@AllArgsConstructor
public class DepositRequest {
    private Long customerId;
    private BigDecimal amount;
}

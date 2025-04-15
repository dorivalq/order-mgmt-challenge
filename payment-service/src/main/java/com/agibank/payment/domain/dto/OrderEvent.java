package com.agibank.payment.domain.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderEvent {
    private Long id;
    private Long customerId;
    private BigDecimal totalAmount;
}

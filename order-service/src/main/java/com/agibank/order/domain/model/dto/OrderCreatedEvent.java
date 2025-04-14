package com.agibank.order.domain.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderCreatedEvent {
    private Long orderId;
    private String customerName;
    private BigDecimal totalAmount;
}

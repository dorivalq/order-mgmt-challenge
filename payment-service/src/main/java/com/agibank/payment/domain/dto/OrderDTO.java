package com.agibank.payment.domain.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderDTO {
    private Long id;
    private String customerName;
    private BigDecimal totalAmount;
}

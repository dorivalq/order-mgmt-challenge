package com.agibank.payment.domain.dto;

import com.agibank.payment.domain.enums.PaymentStatus;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentEvent {
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;
    private PaymentStatus status;
}

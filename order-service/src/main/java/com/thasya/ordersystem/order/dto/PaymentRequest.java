package com.thasya.ordersystem.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    private UUID orderId;
    private BigDecimal amount;
    private String accountNumber;
    private String currency;
}
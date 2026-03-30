package com.thasya.paymentsystem.payment.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    private UUID orderId;
    private BigDecimal amount;
    private String accountNumber;
    private String currency;
}
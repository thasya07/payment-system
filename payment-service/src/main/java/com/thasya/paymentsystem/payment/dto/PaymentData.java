package com.thasya.paymentsystem.payment.dto;


import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentData {
    private String transactionId;
    private String orderId;
    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    private String status;       // PENDING / COMPLETED / FAILED
    private boolean processed;
    private LocalDateTime transactionDate;
}

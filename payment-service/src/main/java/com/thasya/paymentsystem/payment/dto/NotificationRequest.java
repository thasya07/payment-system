package com.thasya.paymentsystem.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class NotificationRequest {

    private UUID orderId;
    private String status;
    private String accountNumber;
    private String message;
    private String recipientEmail;
    private BigDecimal amount;

}

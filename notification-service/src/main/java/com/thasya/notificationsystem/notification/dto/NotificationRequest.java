package com.thasya.notificationsystem.notification.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class NotificationRequest {

    private UUID orderId;
    private String status;
    private String accountNumber;
    private BigDecimal amount;
    private String recipientEmail;

}
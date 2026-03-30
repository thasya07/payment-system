package com.thasya.ordersystem.order.dto;

import lombok.Data;

@Data
public class PaymentData {
    private String transactionId;
    private String status;
}
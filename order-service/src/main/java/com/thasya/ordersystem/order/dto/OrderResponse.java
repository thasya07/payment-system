package com.thasya.ordersystem.order.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID orderId;
    private String status;
    private String transactionId;
}
package com.thasya.paymentsystem.payment.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentEvent {
    private UUID paymentId;
    private UUID orderId;
    private String status; // SUCCESS / FAILED
}

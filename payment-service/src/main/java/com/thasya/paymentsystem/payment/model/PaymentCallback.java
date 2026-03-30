package com.thasya.paymentsystem.payment.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_callback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCallback {

    @Id
    @GeneratedValue
    private UUID callbackId;

    private UUID transactionId;

    private UUID orderId;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String status; // PENDING / PROCESSED

    private LocalDateTime receivedAt;

    private LocalDateTime processedAt;
}
package com.thasya.paymentsystem.payment.dto;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCallback {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID callbackId;

    private UUID transactionId;
    private UUID orderId;

    @Column(columnDefinition = "TEXT")
    private String payload;      // JSON dari gateway

    private String status;       // PENDING / PROCESSED / FAILED
    private LocalDateTime receivedAt;
    private LocalDateTime processedAt;
    private String errorMessage;
}
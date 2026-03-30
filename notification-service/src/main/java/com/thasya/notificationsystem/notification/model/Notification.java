package com.thasya.notificationsystem.notification.model;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private String message;

    @Column
    private String status; // PENDING / SENT

    @Column
    private String accountNumber;

    @Column
    private BigDecimal amount;

    @Column
    private String recipientEmail;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime sentAt;
}
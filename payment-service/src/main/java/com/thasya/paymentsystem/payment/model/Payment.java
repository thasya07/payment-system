package com.thasya.paymentsystem.payment.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue
    private UUID transactionId;

    private UUID orderId;

    private String accountNumber;

    private BigDecimal amount;

    private String currency;

    private String status; // PENDING, COMPLETED, FAILED

    private boolean processed;

    private LocalDateTime transactionDate;

    @Column(unique = true)
    private String externalId;
}
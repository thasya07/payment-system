package com.thasya.ordersystem.order.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    private UUID id;

    private BigDecimal amount;

    private String status; // PENDING_PAYMENT, PAID, FAILED

    private LocalDateTime createdAt;
}
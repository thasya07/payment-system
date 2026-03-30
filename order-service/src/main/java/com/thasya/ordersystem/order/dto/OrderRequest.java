package com.thasya.ordersystem.order.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderRequest {
    private BigDecimal amount;
    private String accountNumber;
}
package com.thasya.ordersystem.order.service;

import com.thasya.ordersystem.order.dto.*;
import com.thasya.ordersystem.order.model.Order;
import com.thasya.ordersystem.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    private static final String PAYMENT_URL = "http://localhost:8080/payments/create";

    // 🔥 CREATE ORDER + TRIGGER PAYMENT
    public OrderResponse createOrder(OrderRequest request) {

        UUID orderId = UUID.randomUUID();

        log.info("🟢 CREATE ORDER: orderId={}, amount={}", orderId, request.getAmount());

        // 1. Save Order
        Order order = Order.builder()
                .id(orderId)
                .amount(request.getAmount())
                .status("PENDING_PAYMENT")
                .createdAt(LocalDateTime.now())
                .build();

        orderRepository.save(order);

        // 2. Call Payment Service
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(orderId)
                .amount(request.getAmount())
                .accountNumber(request.getAccountNumber())
                .currency("IDR")
                .build();

        log.info("➡️ CALL PAYMENT SERVICE for orderId={}", orderId);

        PaymentResponse paymentResponse = null;

        try {
            paymentResponse = restTemplate.postForObject(
                    PAYMENT_URL,
                    paymentRequest,
                    PaymentResponse.class
            );
        } catch (Exception e) {
            log.error("❌ ERROR CALL PAYMENT SERVICE", e);
        }

        String transactionId = null;

        if (paymentResponse != null && paymentResponse.getData() != null) {
            transactionId = paymentResponse.getData().getTransactionId();
        }

        log.info("✅ ORDER CREATED: orderId={}, transactionId={}", orderId, transactionId);

        return OrderResponse.builder()
                .orderId(orderId)
                .status(order.getStatus())
                .transactionId(transactionId)
                .build();
    }

    // 🔥 UPDATE ORDER FROM PAYMENT CALLBACK
    public void updateOrderStatus(UUID orderId, String paymentStatus) {

        log.info("📩 UPDATE ORDER FROM PAYMENT: orderId={}, status={}", orderId, paymentStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("❌ ORDER NOT FOUND: {}", orderId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
                });

        if ("COMPLETED".equalsIgnoreCase(paymentStatus)) {
            order.setStatus("PAID");
        } else if ("FAILED".equalsIgnoreCase(paymentStatus)) {
            order.setStatus("FAILED");
        }

        orderRepository.save(order);

        log.info("✅ ORDER UPDATED: orderId={}, newStatus={}", orderId, order.getStatus());
    }
}
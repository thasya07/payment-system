package com.thasya.paymentsystem.payment.service;

import com.thasya.paymentsystem.payment.dto.PaymentData;
import com.thasya.paymentsystem.payment.dto.PaymentRequest;
import com.thasya.paymentsystem.payment.dto.NotificationRequest;
import com.thasya.paymentsystem.payment.model.Payment;
import com.thasya.paymentsystem.payment.model.PaymentCallback;
import com.thasya.paymentsystem.payment.repository.PaymentCallbackRepository;
import com.thasya.paymentsystem.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentCallbackRepository callbackRepository;
    private final RestTemplate restTemplate;

    /**
     * Create a new payment
     */
    @Transactional
    public PaymentData createPayment(PaymentRequest request) {
        UUID transactionId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .orderId(request.getOrderId())
                .accountNumber(request.getAccountNumber())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status("PENDING")
                .processed(false)
                .transactionDate(now)
                .build();

        paymentRepository.save(payment);

        log.info("[CREATE PAYMENT] transactionId={}, orderId={}, amount={}, status={}",
                transactionId, request.getOrderId(), request.getAmount(), payment.getStatus());

        return PaymentData.builder()
                .transactionId(transactionId.toString())
                .orderId(request.getOrderId().toString())
                .accountNumber(request.getAccountNumber())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(payment.getStatus())
                .processed(payment.isProcessed())
                .transactionDate(payment.getTransactionDate())
                .build();
    }

    /**
     * Handle payment callback (idempotent)
     */
    @Transactional
    public boolean processCallback(UUID transactionId, String payload) {
        LocalDateTime now = LocalDateTime.now();

        // Find payment
        Payment payment = paymentRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Save callback
        PaymentCallback callback = PaymentCallback.builder()
                .callbackId(UUID.randomUUID())
                .transactionId(transactionId)
                .orderId(payment.getOrderId())
                .payload(payload)
                .status("RECEIVED")
                .receivedAt(now)
                .build();
        callbackRepository.save(callback);

        log.info("[STEP 0] Callback received for transactionId={}, payload={}", transactionId, payload);

        // Idempotency check
        if (payment.isProcessed()) {
            log.info("[STEP 1] Payment already processed, ignoring callback. transactionId={}, orderId={}",
                    transactionId, payment.getOrderId());
            callback.setStatus("IGNORED");
            callback.setProcessedAt(LocalDateTime.now());
            callbackRepository.save(callback);
            return false;
        }

        log.info("[STEP 2] Determining new status for transactionId={}", transactionId);

        // Determine new status from payload
        String newStatus = "PENDING";

        if (payload != null) {
            if (payload.contains("COMPLETED")) {
                newStatus = "COMPLETED";
            } else if (payload.contains("FAILED")) {
                newStatus = "FAILED";
            } else if (payload.contains("PENDING")) {
                newStatus = "PENDING";
            }
        }

        log.info("[STEP 3] Status determined: {}", newStatus);

        // Update payment
        payment.setStatus(newStatus);
        payment.setProcessed(true);
        paymentRepository.save(payment);
        log.info("[STEP 4] Payment updated. transactionId={}, status={}", transactionId, newStatus);

        // Update callback as processed
        callback.setStatus("PROCESSED");
        callback.setProcessedAt(LocalDateTime.now());
        callbackRepository.save(callback);
        log.info("[STEP 5] Callback processed and saved. callbackId={}", callback.getCallbackId());

        // Notify order service
        log.info("[STEP 6] Notifying Order Service. orderId={}, status={}", payment.getOrderId(), newStatus);
        notifyOrderService(payment.getOrderId(), newStatus);

        // Notify notification service
        log.info("[STEP 7] Notifying Notification Service. orderId={}, amount={}, status={}",
                payment.getOrderId(), payment.getAmount(), payment.getStatus());
        notifyNotificationService(
                payment.getOrderId(),
                payment.getAmount(),
                payment.getAccountNumber(),
                payment.getStatus(),
                payment.getAccountNumber() + "@example.com"
        );

        log.info("[STEP 8] Callback processing completed. transactionId={}", transactionId);

        return true;
    }

    /**
     * Notify Order Service about payment status
     */
    private void notifyOrderService(UUID orderId, String status) {
        String url = String.format(
                "http://localhost:8081/orders/payment-callback?orderId=%s&status=%s",
                orderId, status
        );
        try {
            restTemplate.postForObject(url, null, Void.class);
            log.info("[ORDER SERVICE] Notification sent successfully for orderId={}", orderId);
        } catch (Exception e) {
            log.error("[ORDER SERVICE] Failed to notify Order Service. orderId={}, error={}", orderId, e.getMessage());
        }
    }

    /**
     * Notify Notification Service
     */
    private void notifyNotificationService(UUID orderId, BigDecimal amount, String accountNumber, String status, String email) {
        String url = "http://localhost:8082/notifications";

        NotificationRequest request = new NotificationRequest();
        request.setOrderId(orderId);
        request.setStatus(status);
        request.setAccountNumber(accountNumber);
        request.setAmount(amount);
        request.setRecipientEmail(email);

        try {
            restTemplate.postForObject(url, request, Void.class);
            log.info("[NOTIFICATION SERVICE] Notification sent successfully for orderId={}", orderId);
        } catch (Exception e) {
            log.error("[NOTIFICATION SERVICE] Failed to send notification. orderId={}, error={}", orderId, e.getMessage());
        }
    }
}
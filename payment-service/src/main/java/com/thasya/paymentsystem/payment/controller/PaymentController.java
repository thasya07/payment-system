package com.thasya.paymentsystem.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.thasya.paymentsystem.payment.service.PaymentService;
import com.thasya.paymentsystem.payment.dto.PaymentRequest;
import com.thasya.paymentsystem.payment.dto.PaymentData;
import com.thasya.paymentsystem.payment.dto.ApiResponse;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Create payment
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PaymentData>> createPayment(@RequestBody PaymentRequest request) {
        PaymentData data = paymentService.createPayment(request);

        return ResponseEntity.ok(
                ApiResponse.<PaymentData>builder()
                        .success(true)
                        .message("Payment created")
                        .data(data)
                        .build()
        );
    }

    /**
     * Receive callback from payment gateway
     */
    @PostMapping("/callback")
    public ResponseEntity<ApiResponse<String>> callback(
            @RequestParam UUID transactionId,
            @RequestBody String payload) {

        // Call service yang sudah handle callback + record PaymentCallback
        paymentService.processCallback(transactionId, payload);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Callback processed")
                        .data("OK")
                        .build()
        );
    }
}
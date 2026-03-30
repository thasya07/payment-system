package com.thasya.ordersystem.order.controller;

import com.thasya.ordersystem.order.dto.OrderRequest;
import com.thasya.ordersystem.order.dto.OrderResponse;
import com.thasya.ordersystem.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 🔹 Create Order
    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        log.info("CREATE ORDER REQUEST: {}", request);
        return orderService.createOrder(request);
    }

    // 🔹 Callback dari Payment Service
    @PostMapping("/payment-callback")
    public void paymentCallback(@RequestParam UUID orderId,
                                @RequestParam String status) {

        log.info("🔥 CALLBACK MASUK: orderId={}, status={}", orderId, status);

        orderService.updateOrderStatus(orderId, status);

        log.info("✅ ORDER UPDATED");
    }

    // 🔹 Endpoint test (buat cek service hidup)
    @GetMapping("/test")
    public String test() {
        return "ORDER SERVICE OK";
    }
}
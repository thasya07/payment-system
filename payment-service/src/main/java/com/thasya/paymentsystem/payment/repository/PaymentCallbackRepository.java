package com.thasya.paymentsystem.payment.repository;

import com.thasya.paymentsystem.payment.model.PaymentCallback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PaymentCallbackRepository extends JpaRepository<PaymentCallback, UUID> {
    
    List<PaymentCallback> findByTransactionId(UUID transactionId);
    List<PaymentCallback> findByStatus(String status);
}
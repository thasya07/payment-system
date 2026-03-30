package com.thasya.notificationsystem.notification.service;

import com.thasya.notificationsystem.notification.dto.NotificationRequest;
import com.thasya.notificationsystem.notification.model.Notification;
import com.thasya.notificationsystem.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification sendNotification(NotificationRequest request) {
        String message = String.format(
                "Kamu berhasil mengirimkan pembayaran sebesar %s untuk account %s. Status: %s",
                request.getAmount(), request.getAccountNumber(), request.getStatus()
        );

        Notification notification = Notification.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .accountNumber(request.getAccountNumber())
                .recipientEmail(request.getRecipientEmail())
                .status("SENT")
                .message(message)
                .createdAt(LocalDateTime.now())
                .sentAt(LocalDateTime.now())
                .build();

        return notificationRepository.save(notification);
    }
}
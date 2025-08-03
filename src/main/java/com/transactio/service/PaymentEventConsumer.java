package com.transactio.service;

import com.transactio.dto.PaymentStatusUpdate;
import com.transactio.event.PaymentEvent;
import com.transactio.websocket.PaymentUpdateWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PaymentEventConsumer {

    private final PaymentUpdateWebSocketHandler webSocketHandler;

    public PaymentEventConsumer(PaymentUpdateWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @KafkaListener(
        topics = "${spring.kafka.topic.payment-events}",
        groupId = "${spring.kafka.consumer.group-id.notification}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentEvent(PaymentEvent event) {
        log.info("Notification Service received event: {}", event);

        switch (event.getEventType()) {
            case PAYMENT_CREATED:
                sendPaymentCreatedNotification(event);
                broadcastPaymentStatusUpdate(event);
                break;
            case PAYMENT_COMPLETED:
                sendPaymentCompletedNotification(event);
                broadcastPaymentStatusUpdate(event);
                break;
            case PAYMENT_FAILED:
                sendPaymentFailedNotification(event);
                broadcastPaymentStatusUpdate(event);
                break;
            default:
                log.info("No notification for event type: {}", event.getEventType());
        }
    }

    private void sendPaymentCreatedNotification(PaymentEvent event) {
        // Simulate sending notification
        log.info("📧 Sending notification: Payment {} created for user {} - Amount: {} {}",
            event.getPaymentId(), event.getUserId(), event.getAmount(), event.getCurrency());
    }

    private void sendPaymentCompletedNotification(PaymentEvent event) {
        log.info("✅ Sending notification: Payment {} completed for user {} - Amount: {} {}",
            event.getPaymentId(), event.getUserId(), event.getAmount(), event.getCurrency());
    }

    private void sendPaymentFailedNotification(PaymentEvent event) {
        log.info("❌ Sending notification: Payment {} failed for user {} - Reason: {}",
            event.getPaymentId(), event.getUserId(), event.getMessage());
    }

    private void broadcastPaymentStatusUpdate(PaymentEvent event) {
        PaymentStatusUpdate update = PaymentStatusUpdate.builder()
                .paymentId(event.getPaymentId())
                .userId(event.getUserId())
                .status(event.getStatus())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .timestamp(LocalDateTime.now())
                .message(event.getMessage())
                .build();

        webSocketHandler.broadcastPaymentUpdate(update);
        log.debug("Broadcasted WebSocket update for payment: {}", event.getPaymentId());
    }
}
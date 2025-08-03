package com.transactio.service;

import com.transactio.dto.PaymentStatusUpdate;
import com.transactio.event.PaymentEvent;
import com.transactio.event.PaymentEventType;
import com.transactio.model.PaymentStatus;
import com.transactio.websocket.PaymentUpdateWebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentEventConsumerIntegrationTest {

    @Mock
    private PaymentUpdateWebSocketHandler webSocketHandler;

    private PaymentEventConsumer paymentEventConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentEventConsumer = new PaymentEventConsumer(webSocketHandler);
    }

    @Test
    public void shouldBroadcastWebSocketUpdateWhenPaymentCompleted() {
        PaymentEvent event = createPaymentEvent(PaymentEventType.PAYMENT_COMPLETED, PaymentStatus.COMPLETED);
        
        paymentEventConsumer.handlePaymentEvent(event);
        
        ArgumentCaptor<PaymentStatusUpdate> updateCaptor = ArgumentCaptor.forClass(PaymentStatusUpdate.class);
        verify(webSocketHandler).broadcastPaymentUpdate(updateCaptor.capture());
        
        PaymentStatusUpdate sentUpdate = updateCaptor.getValue();
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), sentUpdate.getPaymentId());
        assertEquals(PaymentStatus.COMPLETED, sentUpdate.getStatus());
        assertEquals(new BigDecimal("100.00"), sentUpdate.getAmount());
        assertEquals("USD", sentUpdate.getCurrency());
        assertNotNull(sentUpdate.getTimestamp());
    }

    @Test
    public void shouldBroadcastWebSocketUpdateWhenPaymentFailed() {
        PaymentEvent event = createPaymentEvent(PaymentEventType.PAYMENT_FAILED, PaymentStatus.FAILED);
        
        paymentEventConsumer.handlePaymentEvent(event);
        
        ArgumentCaptor<PaymentStatusUpdate> updateCaptor = ArgumentCaptor.forClass(PaymentStatusUpdate.class);
        verify(webSocketHandler).broadcastPaymentUpdate(updateCaptor.capture());
        
        PaymentStatusUpdate sentUpdate = updateCaptor.getValue();
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), sentUpdate.getPaymentId());
        assertEquals(PaymentStatus.FAILED, sentUpdate.getStatus());
        assertEquals("Payment processing failed", sentUpdate.getMessage());
    }

    @Test
    public void shouldBroadcastWebSocketUpdateWhenPaymentCreated() {
        PaymentEvent event = createPaymentEvent(PaymentEventType.PAYMENT_CREATED, PaymentStatus.CREATED);
        
        paymentEventConsumer.handlePaymentEvent(event);
        
        ArgumentCaptor<PaymentStatusUpdate> updateCaptor = ArgumentCaptor.forClass(PaymentStatusUpdate.class);
        verify(webSocketHandler).broadcastPaymentUpdate(updateCaptor.capture());
        
        PaymentStatusUpdate sentUpdate = updateCaptor.getValue();
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), sentUpdate.getPaymentId());
        assertEquals(PaymentStatus.CREATED, sentUpdate.getStatus());
    }

    @Test
    public void shouldNotBroadcastWebSocketUpdateForUnhandledEventTypes() {
        PaymentEvent event = createPaymentEvent(PaymentEventType.PAYMENT_PROCESSING, PaymentStatus.PROCESSING);
        
        paymentEventConsumer.handlePaymentEvent(event);
        
        verify(webSocketHandler, never()).broadcastPaymentUpdate(any());
    }

    private PaymentEvent createPaymentEvent(PaymentEventType eventType, PaymentStatus status) {
        return PaymentEvent.builder()
                .paymentId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .userId(UUID.randomUUID())
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .eventType(eventType)
                .status(status)
                .timestamp(LocalDateTime.now())
                .message(eventType == PaymentEventType.PAYMENT_FAILED ? "Payment processing failed" : "Payment processed successfully")
                .build();
    }
}
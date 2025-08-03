package com.transactio.websocket;

import com.transactio.dto.PaymentStatusUpdate;
import com.transactio.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentUpdateWebSocketHandlerTest {

    private PaymentUpdateWebSocketHandler handler;
    private WebSocketSession mockSession1;
    private WebSocketSession mockSession2;

    @BeforeEach
    void setUp() {
        handler = new PaymentUpdateWebSocketHandler();
        mockSession1 = mock(WebSocketSession.class);
        mockSession2 = mock(WebSocketSession.class);
        
        when(mockSession1.getId()).thenReturn("session-1");
        when(mockSession2.getId()).thenReturn("session-2");
        when(mockSession1.isOpen()).thenReturn(true);
        when(mockSession2.isOpen()).thenReturn(true);
    }

    @Test
    public void shouldAddSessionWhenConnectionEstablished() throws Exception {
        handler.afterConnectionEstablished(mockSession1);
        
        PaymentStatusUpdate update = createTestPaymentUpdate();
        handler.broadcastPaymentUpdate(update);
        
        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(mockSession1).sendMessage(messageCaptor.capture());
        
        String sentMessage = messageCaptor.getValue().getPayload();
        assertTrue(sentMessage.contains("COMPLETED"));
        assertTrue(sentMessage.contains("550e8400-e29b-41d4-a716-446655440000"));
    }

    @Test
    public void shouldRemoveSessionWhenConnectionClosed() throws Exception {
        handler.afterConnectionEstablished(mockSession1);
        handler.afterConnectionClosed(mockSession1, null);
        
        PaymentStatusUpdate update = createTestPaymentUpdate();
        handler.broadcastPaymentUpdate(update);
        
        verify(mockSession1, never()).sendMessage(any(TextMessage.class));
    }

    @Test
    public void shouldBroadcastToAllConnectedSessions() throws Exception {
        handler.afterConnectionEstablished(mockSession1);
        handler.afterConnectionEstablished(mockSession2);
        
        PaymentStatusUpdate update = createTestPaymentUpdate();
        handler.broadcastPaymentUpdate(update);
        
        verify(mockSession1).sendMessage(any(TextMessage.class));
        verify(mockSession2).sendMessage(any(TextMessage.class));
    }

    @Test
    public void shouldNotBroadcastWhenNoSessionsConnected() {
        PaymentStatusUpdate update = createTestPaymentUpdate();
        
        assertDoesNotThrow(() -> handler.broadcastPaymentUpdate(update));
    }

    @Test
    public void shouldRemoveSessionOnTransportError() throws Exception {
        handler.afterConnectionEstablished(mockSession1);
        handler.handleTransportError(mockSession1, new RuntimeException("Connection error"));
        
        PaymentStatusUpdate update = createTestPaymentUpdate();
        handler.broadcastPaymentUpdate(update);
        
        verify(mockSession1, never()).sendMessage(any(TextMessage.class));
    }

    @Test
    public void shouldSkipClosedSessionsWhenBroadcasting() throws Exception {
        handler.afterConnectionEstablished(mockSession1);
        handler.afterConnectionEstablished(mockSession2);
        
        when(mockSession1.isOpen()).thenReturn(false);
        when(mockSession2.isOpen()).thenReturn(true);
        
        PaymentStatusUpdate update = createTestPaymentUpdate();
        handler.broadcastPaymentUpdate(update);
        
        verify(mockSession1, never()).sendMessage(any(TextMessage.class));
        verify(mockSession2).sendMessage(any(TextMessage.class));
    }

    private PaymentStatusUpdate createTestPaymentUpdate() {
        return PaymentStatusUpdate.builder()
                .paymentId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .userId(UUID.randomUUID())
                .status(PaymentStatus.COMPLETED)
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .timestamp(LocalDateTime.now())
                .message("Payment completed successfully")
                .build();
    }
}
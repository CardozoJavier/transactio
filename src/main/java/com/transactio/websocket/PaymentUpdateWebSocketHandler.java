package com.transactio.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
@Slf4j
public class PaymentUpdateWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.debug("WebSocket connection established: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        log.debug("WebSocket connection closed: {}", session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error for session {}: {}", session.getId(), exception.getMessage());
        sessions.remove(session.getId());
    }

    public void broadcastPaymentUpdate(Object paymentUpdate) {
        if (sessions.isEmpty()) {
            return;
        }

        try {
            String message = objectMapper.writeValueAsString(paymentUpdate);
            TextMessage textMessage = new TextMessage(message);

            sessions.values().forEach(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    }
                } catch (IOException e) {
                    log.error("Error sending message to session {}: {}", session.getId(), e.getMessage());
                    sessions.remove(session.getId());
                }
            });
        } catch (Exception e) {
            log.error("Error broadcasting payment update: {}", e.getMessage());
        }
    }
}
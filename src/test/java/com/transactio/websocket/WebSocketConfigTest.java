package com.transactio.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebSocketConfigTest {

    @LocalServerPort
    private int port;

    @Test
    public void shouldAllowWebSocketConnectionToPaymentUpdatesEndpoint() throws Exception {
        CountDownLatch connectionLatch = new CountDownLatch(1);
        
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketHandler handler = new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                connectionLatch.countDown();
            }
            
            @Override
            public void handleMessage(WebSocketSession session, org.springframework.web.socket.WebSocketMessage<?> message) {
            }
            
            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
            }
            
            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
            }
            
            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        };

        URI uri = URI.create("ws://localhost:" + port + "/ws/payment-updates");
        WebSocketSession session = client.execute(handler, null, uri).get();
        
        assertTrue(connectionLatch.await(5, TimeUnit.SECONDS), 
            "WebSocket connection should be established within 5 seconds");
        
        session.close();
    }
}
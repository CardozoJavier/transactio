package com.transactio.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final PaymentUpdateWebSocketHandler paymentUpdateHandler;

    public WebSocketConfig(PaymentUpdateWebSocketHandler paymentUpdateHandler) {
        this.paymentUpdateHandler = paymentUpdateHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(paymentUpdateHandler, "/ws/payment-updates")
                .setAllowedOrigins("*");
    }
}
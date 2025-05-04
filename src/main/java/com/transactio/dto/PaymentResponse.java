package com.transactio.dto;

import com.transactio.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private UUID id;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String senderId;
    private String receiverId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
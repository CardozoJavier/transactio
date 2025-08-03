package com.transactio.dto;

import com.transactio.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusUpdate {
    private UUID paymentId;
    private UUID userId;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime timestamp;
    private String message;
}
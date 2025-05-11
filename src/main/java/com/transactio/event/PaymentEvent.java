package com.transactio.event;

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
public class PaymentEvent {
  private UUID paymentId;
  private UUID userId;
  private BigDecimal amount;
  private String currency;
  private PaymentEventType eventType;
  private PaymentStatus status;
  private LocalDateTime timestamp;
  private String message;
}
package com.transactio.event;

public enum PaymentEventType {
  PAYMENT_CREATED,
  PAYMENT_PROCESSING,
  PAYMENT_COMPLETED,
  PAYMENT_FAILED,
  PAYMENT_CANCELLED
}
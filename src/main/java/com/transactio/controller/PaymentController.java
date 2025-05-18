package com.transactio.controller;

import com.transactio.dto.PaymentRequest;
import com.transactio.dto.PaymentResponse;
import com.transactio.model.PaymentStatus;
import com.transactio.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping
  public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
    log.info("Received payment request: {}", request);
    PaymentResponse response = paymentService.createPayment(request);
    log.info("Created payment: {}", response);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID id) {
    log.info("Fetching payment with ID: {}", id);
    PaymentResponse response = paymentService.getPaymentById(id);
    log.info("Retrieved payment: {}", response);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<PaymentResponse>> getAllPayments() {
    log.info("Fetching all payments");
    List<PaymentResponse> payments = paymentService.getAllPayments();
    log.info("Retrieved {} payments", payments.size());
    log.debug("Payment IDs: {}", payments.stream()
        .map(PaymentResponse::getId)
        .collect(Collectors.toList()));
    return ResponseEntity.ok(payments);
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
    log.info("Fetching payments with status: {}", status);
    List<PaymentResponse> payments = paymentService.getPaymentsByStatus(status);
    log.info("Retrieved {} payments with status {}", payments.size(), status);
    log.debug("Payment IDs with status {}: {}", status, payments.stream()
        .map(PaymentResponse::getId)
        .collect(Collectors.toList()));
    return ResponseEntity.ok(payments);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<PaymentResponse>> getPaymentsForUser(@PathVariable String userId) {
    log.info("Fetching payments for user: {}", userId);
    List<PaymentResponse> payments = paymentService.getPaymentsForUser(userId);
    log.info("Retrieved {} payments for user {}", payments.size(), userId);
    log.debug("Payment IDs for user {}: {}", userId, payments.stream()
        .map(PaymentResponse::getId)
        .collect(Collectors.toList()));
    return ResponseEntity.ok(payments);
  }
}
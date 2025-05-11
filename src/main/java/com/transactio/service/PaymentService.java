package com.transactio.service;

import com.transactio.dto.PaymentRequest;
import com.transactio.dto.PaymentResponse;
import com.transactio.event.PaymentEvent;
import com.transactio.event.PaymentEventType;
import com.transactio.model.Payment;
import com.transactio.model.PaymentStatus;
import com.transactio.repository.PaymentRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer eventProducer;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("Creating payment from {} to {} for amount {} {}",
            request.getSenderId(), request.getReceiverId(),
            request.getAmount(), request.getCurrency());

        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setSenderId(request.getSenderId());
        payment.setReceiverId(request.getReceiverId());
        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);

        Payment pending = paymentRepository.save(payment);

        PaymentEvent event = createPaymentEvent(pending, PaymentEventType.PAYMENT_CREATED, "Payment created successfully");
        eventProducer.sendPaymentEvent(event);
        // In a real system, we would have payment processing logic here
        // For now, we'll simulate processing
        processPaymentAsync(pending);
        log.info("Payment processed with ID: {}", pending.getId());

        return mapToResponse(pending);
    }

    public PaymentResponse getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return mapToResponse(payment);
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPaymentsForUser(String userId) {
        return paymentRepository.findBySenderIdOrReceiverId(userId, userId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private void processPaymentAsync(Payment payment) {
        // In a real system, this would involve:
        // 1. Validating sender's balance
        // 2. Checking fraud detection rules
        // 3. Executing the actual transfer
        // 4. Updating account balances
        try {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                payment.setStatus(PaymentStatus.PROCESSING);
                Payment processing = paymentRepository.save(payment);
                eventProducer.sendPaymentEvent(createPaymentEvent(processing, PaymentEventType.PAYMENT_PROCESSING, "Payment is being processed"));

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Complete payment
                payment.setStatus(PaymentStatus.COMPLETED);
                Payment completed = paymentRepository.save(payment);
                eventProducer.sendPaymentEvent(createPaymentEvent(completed, PaymentEventType.PAYMENT_COMPLETED, "Payment completed successfully"));
            }).start();
        } catch (Exception e) {
            log.error("Payment processing failed, paymentId={}, error={}", payment.getId(), e.getMessage(), e);
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            eventProducer.sendPaymentEvent(createPaymentEvent(payment, PaymentEventType.PAYMENT_FAILED, "Payment failed" + e.getMessage()));
        }
    }

    private PaymentResponse mapToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setStatus(payment.getStatus());
        response.setSenderId(payment.getSenderId());
        response.setReceiverId(payment.getReceiverId());
        response.setDescription(payment.getDescription());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        return response;
    }

    private PaymentEvent createPaymentEvent(Payment payment, PaymentEventType eventType, String message) {
        return PaymentEvent.builder()
            .paymentId(payment.getId())
            .userId(UUID.fromString(payment.getSenderId())) // TODO: what should we use? sender or receiver ID?
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .eventType(eventType)
            .status(payment.getStatus())
            .timestamp(LocalDateTime.now())
            .message(message)
            .build();
    }
}
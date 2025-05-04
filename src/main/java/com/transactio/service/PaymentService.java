package com.transactio.service;

import com.transactio.dto.PaymentRequest;
import com.transactio.dto.PaymentResponse;
import com.transactio.model.Payment;
import com.transactio.model.PaymentStatus;
import com.transactio.repository.PaymentRepository;
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

        // In a real system, we would have payment processing logic here
        // For now, we'll simulate processing
        processPayment(payment);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with ID: {}", savedPayment.getId());

        return mapToResponse(savedPayment);
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

    private void processPayment(Payment payment) {
        // Simulate payment processing
        try {
            payment.setStatus(PaymentStatus.PROCESSING);

            // In a real system, this would involve:
            // 1. Validating sender's balance
            // 2. Checking fraud detection rules
            // 3. Executing the actual transfer
            // 4. Updating account balances

            // Simulate processing time
            Thread.sleep(1000);

            // For demo purposes, always succeed
            payment.setStatus(PaymentStatus.COMPLETED);

        } catch (Exception e) {
            log.error("Payment processing failed", e);
            payment.setStatus(PaymentStatus.FAILED);
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
}
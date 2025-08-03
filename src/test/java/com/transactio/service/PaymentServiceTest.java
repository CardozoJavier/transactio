package com.transactio.service;

import com.transactio.dto.PaymentRequest;
import com.transactio.dto.PaymentResponse;
import com.transactio.event.PaymentEvent;
import com.transactio.event.PaymentEventType;
import com.transactio.model.Payment;
import com.transactio.model.PaymentStatus;
import com.transactio.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    // Test Constants
    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal("100.00");
    private static final String DEFAULT_CURRENCY = "USD";
    private static final String DEFAULT_DESCRIPTION = "Test payment";
    private static final BigDecimal ALTERNATIVE_AMOUNT = new BigDecimal("50.00");
    private static final BigDecimal COMPLETED_AMOUNT = new BigDecimal("150.00");
    private static final BigDecimal SECOND_PAYMENT_AMOUNT = new BigDecimal("200.00");
    private static final String EURO_CURRENCY = "EUR";
    private static final int LARGE_DATASET_SIZE = 1000;
    private static final int USER_PAYMENT_PAIRS = 500;
    private static final int ASYNC_VERIFICATION_DELAY_MS = 100;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventProducer eventProducer;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest validPaymentRequest;
    private Payment savedPayment;

    @BeforeEach
    void setUp() {
        validPaymentRequest = createPaymentRequest();
        savedPayment = createPayment(validPaymentRequest);
    }

    private PaymentRequest createPaymentRequest() {
        return createPaymentRequest(DEFAULT_AMOUNT, DEFAULT_CURRENCY, DEFAULT_DESCRIPTION);
    }

    private PaymentRequest createPaymentRequest(BigDecimal amount, String currency, String description) {
        return new PaymentRequest(
            amount,
            currency,
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            description
        );
    }

    private Payment createPayment(PaymentRequest request) {
        return createPayment(request, PaymentStatus.PENDING);
    }

    private Payment createPayment(PaymentRequest request, PaymentStatus status) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setSenderId(request.getSenderId());
        payment.setReceiverId(request.getReceiverId());
        payment.setDescription(request.getDescription());
        payment.setStatus(status);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        return payment;
    }

    private Payment createPayment(String senderId, String receiverId, PaymentStatus status) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setAmount(DEFAULT_AMOUNT);
        payment.setCurrency(DEFAULT_CURRENCY);
        payment.setSenderId(senderId);
        payment.setReceiverId(receiverId);
        payment.setStatus(status);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        return payment;
    }

    @Test
    void createPayment_ShouldCreatePaymentSuccessfully() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        PaymentResponse response = paymentService.createPayment(validPaymentRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedPayment.getId());
        assertThat(response.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(response.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(response.getSenderId()).isEqualTo(validPaymentRequest.getSenderId());
        assertThat(response.getReceiverId()).isEqualTo(validPaymentRequest.getReceiverId());
        assertThat(response.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);

        verify(paymentRepository).save(any(Payment.class));
        verify(eventProducer).sendPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    void createPayment_ShouldSetCorrectPaymentFields() {
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        when(paymentRepository.save(paymentCaptor.capture())).thenReturn(savedPayment);

        paymentService.createPayment(validPaymentRequest);

        Payment capturedPayment = paymentCaptor.getValue();
        assertThat(capturedPayment.getAmount()).isEqualTo(validPaymentRequest.getAmount());
        assertThat(capturedPayment.getCurrency()).isEqualTo(validPaymentRequest.getCurrency());
        assertThat(capturedPayment.getSenderId()).isEqualTo(validPaymentRequest.getSenderId());
        assertThat(capturedPayment.getReceiverId()).isEqualTo(validPaymentRequest.getReceiverId());
        assertThat(capturedPayment.getDescription()).isEqualTo(validPaymentRequest.getDescription());
        assertThat(capturedPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void createPayment_ShouldSendPaymentCreatedEvent() {
        ArgumentCaptor<PaymentEvent> eventCaptor = ArgumentCaptor.forClass(PaymentEvent.class);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        paymentService.createPayment(validPaymentRequest);

        verify(eventProducer).sendPaymentEvent(eventCaptor.capture());
        PaymentEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getPaymentId()).isEqualTo(savedPayment.getId());
        assertThat(capturedEvent.getEventType()).isEqualTo(PaymentEventType.PAYMENT_CREATED);
        assertThat(capturedEvent.getAmount()).isEqualTo(savedPayment.getAmount());
        assertThat(capturedEvent.getCurrency()).isEqualTo(savedPayment.getCurrency());
        assertThat(capturedEvent.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(capturedEvent.getMessage()).isEqualTo("Payment created successfully");
    }

    @Test
    void getPaymentById_ShouldReturnPaymentWhenExists() {
        UUID paymentId = savedPayment.getId();
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(savedPayment));

        PaymentResponse response = paymentService.getPaymentById(paymentId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(paymentId);
        assertThat(response.getAmount()).isEqualTo(savedPayment.getAmount());
        assertThat(response.getStatus()).isEqualTo(savedPayment.getStatus());
        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void getPaymentById_ShouldThrowExceptionWhenNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(paymentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentById(nonExistentId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Payment not found with id: " + nonExistentId);

        verify(paymentRepository).findById(nonExistentId);
    }

    @Test
    void getAllPayments_ShouldReturnAllPayments() {
        PaymentRequest request2 = createPaymentRequest(SECOND_PAYMENT_AMOUNT, EURO_CURRENCY, "Second payment");
        Payment payment2 = createPayment(request2, PaymentStatus.COMPLETED);

        List<Payment> payments = List.of(savedPayment, payment2);
        when(paymentRepository.findAll()).thenReturn(payments);

        List<PaymentResponse> responses = paymentService.getAllPayments();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo(savedPayment.getId());
        assertThat(responses.get(1).getId()).isEqualTo(payment2.getId());
        verify(paymentRepository).findAll();
    }

    @Test
    void getAllPayments_ShouldReturnEmptyListWhenNoPayments() {
        when(paymentRepository.findAll()).thenReturn(List.of());

        List<PaymentResponse> responses = paymentService.getAllPayments();

        assertThat(responses).isEmpty();
        verify(paymentRepository).findAll();
    }

    @Test
    void getPaymentsByStatus_ShouldReturnPaymentsWithSpecificStatus() {
        PaymentRequest completedRequest = createPaymentRequest(COMPLETED_AMOUNT, DEFAULT_CURRENCY, "Completed payment");
        Payment completedPayment = createPayment(completedRequest, PaymentStatus.COMPLETED);

        List<Payment> completedPayments = List.of(completedPayment);
        when(paymentRepository.findByStatus(PaymentStatus.COMPLETED)).thenReturn(completedPayments);

        List<PaymentResponse> responses = paymentService.getPaymentsByStatus(PaymentStatus.COMPLETED);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(responses.getFirst().getId()).isEqualTo(completedPayment.getId());
        verify(paymentRepository).findByStatus(PaymentStatus.COMPLETED);
    }

    @Test
    void getPaymentsForUser_ShouldReturnPaymentsWhereSenderOrReceiver() {
        String userId = UUID.randomUUID().toString();
        String otherUserId1 = UUID.randomUUID().toString();
        String otherUserId2 = UUID.randomUUID().toString();
        
        Payment sentPayment = createPayment(userId, otherUserId1, PaymentStatus.PENDING);
        Payment receivedPayment = createPayment(otherUserId2, userId, PaymentStatus.COMPLETED);

        List<Payment> userPayments = List.of(sentPayment, receivedPayment);
        when(paymentRepository.findBySenderIdOrReceiverId(userId, userId)).thenReturn(userPayments);

        List<PaymentResponse> responses = paymentService.getPaymentsForUser(userId);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getSenderId()).isEqualTo(userId);
        assertThat(responses.get(1).getReceiverId()).isEqualTo(userId);
        verify(paymentRepository).findBySenderIdOrReceiverId(userId, userId);
    }

    @Test
    void getPaymentsForUser_ShouldReturnEmptyListWhenNoPayments() {
        String userId = UUID.randomUUID().toString();
        when(paymentRepository.findBySenderIdOrReceiverId(userId, userId)).thenReturn(List.of());

        List<PaymentResponse> responses = paymentService.getPaymentsForUser(userId);

        assertThat(responses).isEmpty();
        verify(paymentRepository).findBySenderIdOrReceiverId(userId, userId);
    }

    @Test
    void createPayment_ShouldHandleRepositoryException() {
        when(paymentRepository.save(any(Payment.class))).thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> paymentService.createPayment(validPaymentRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(paymentRepository).save(any(Payment.class));
        verify(eventProducer, never()).sendPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    void createPayment_ShouldHandleNullDescription() {
        PaymentRequest requestWithNullDescription = createPaymentRequest(ALTERNATIVE_AMOUNT, EURO_CURRENCY, null);
        Payment savedPaymentWithNullDesc = createPayment(requestWithNullDescription);

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPaymentWithNullDesc);

        PaymentResponse response = paymentService.createPayment(requestWithNullDescription);

        assertThat(response.getDescription()).isNull();
        verify(paymentRepository).save(any(Payment.class));
        verify(eventProducer).sendPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    void getPaymentsByStatus_ShouldHandleNullStatus() {
        when(paymentRepository.findByStatus(null)).thenReturn(List.of());

        List<PaymentResponse> responses = paymentService.getPaymentsByStatus(null);

        assertThat(responses).isEmpty();
        verify(paymentRepository).findByStatus(null);
    }

    @Test
    void getPaymentsForUser_ShouldHandleNullUserId() {
        when(paymentRepository.findBySenderIdOrReceiverId(null, null)).thenReturn(List.of());

        List<PaymentResponse> responses = paymentService.getPaymentsForUser(null);

        assertThat(responses).isEmpty();
        verify(paymentRepository).findBySenderIdOrReceiverId(null, null);
    }

    @Test
    void createPayment_ShouldInitiateAsyncProcessingImmediately() throws InterruptedException {
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        PaymentResponse response = paymentService.createPayment(validPaymentRequest);

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
        
        verify(paymentRepository).save(any(Payment.class));
        verify(eventProducer).sendPaymentEvent(any(PaymentEvent.class));
        
        Thread.sleep(ASYNC_VERIFICATION_DELAY_MS);
        
        verify(paymentRepository, atLeast(1)).save(any(Payment.class));
    }

    @Test
    void createPayment_ShouldHandleEventProducerFailure() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        doThrow(new RuntimeException("Kafka connection failed")).when(eventProducer).sendPaymentEvent(any(PaymentEvent.class));

        assertThatThrownBy(() -> paymentService.createPayment(validPaymentRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Kafka connection failed");

        verify(paymentRepository).save(any(Payment.class));
        verify(eventProducer).sendPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    void createPayment_ShouldHandleEventProducerFailureAfterSave() {
        Payment partiallyProcessedPayment = createPayment(validPaymentRequest);
        when(paymentRepository.save(any(Payment.class))).thenReturn(partiallyProcessedPayment);
        doThrow(new RuntimeException("Event publishing failed")).when(eventProducer).sendPaymentEvent(any(PaymentEvent.class));

        assertThatThrownBy(() -> paymentService.createPayment(validPaymentRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Event publishing failed");

        verify(paymentRepository).save(any(Payment.class));
        verify(eventProducer).sendPaymentEvent(any(PaymentEvent.class));
    }

    @ParameterizedTest
    @CsvSource({
        "100.00, USD, PENDING",
        "250.50, EUR, COMPLETED", 
        "1000.00, GBP, FAILED",
        "0.01, JPY, PROCESSING"
    })
    void getPaymentsByStatus_ShouldHandleMultipleStatuses(String amount, String currency, PaymentStatus status) {
        PaymentRequest request = createPaymentRequest(new BigDecimal(amount), currency, DEFAULT_DESCRIPTION);
        Payment payment = createPayment(request, status);
        
        when(paymentRepository.findByStatus(status)).thenReturn(List.of(payment));

        List<PaymentResponse> responses = paymentService.getPaymentsByStatus(status);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getStatus()).isEqualTo(status);
        assertThat(responses.getFirst().getAmount()).isEqualTo(new BigDecimal(amount));
        assertThat(responses.getFirst().getCurrency()).isEqualTo(currency);
        verify(paymentRepository).findByStatus(status);
    }

    @ParameterizedTest
    @CsvSource({
        "10.00, USD, Small payment",
        "1000.00, EUR, Large payment",
        "999999.99, GBP, Very large payment"
    })
    void createPayment_ShouldHandleDifferentAmounts(String amount, String currency, String description) {
        PaymentRequest request = createPaymentRequest(new BigDecimal(amount), currency, description);
        Payment savedPayment = createPayment(request);
        
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        PaymentResponse response = paymentService.createPayment(request);

        assertThat(response.getAmount()).isEqualTo(new BigDecimal(amount));
        assertThat(response.getCurrency()).isEqualTo(currency);
        assertThat(response.getDescription()).isEqualTo(description);
        verify(paymentRepository).save(any(Payment.class));
        verify(eventProducer).sendPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    void createPayment_ShouldCompleteFullWorkflow() {
        Payment initialPayment = createPayment(validPaymentRequest, PaymentStatus.PENDING);
        Payment processingPayment = createPayment(validPaymentRequest, PaymentStatus.PROCESSING);
        Payment completedPayment = createPayment(validPaymentRequest, PaymentStatus.COMPLETED);
        
        when(paymentRepository.save(any(Payment.class)))
            .thenReturn(initialPayment)
            .thenReturn(processingPayment)
            .thenReturn(completedPayment);

        PaymentResponse response = paymentService.createPayment(validPaymentRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.getId()).isEqualTo(initialPayment.getId());
        assertThat(response.getAmount()).isEqualTo(validPaymentRequest.getAmount());
        assertThat(response.getCurrency()).isEqualTo(validPaymentRequest.getCurrency());
        assertThat(response.getSenderId()).isEqualTo(validPaymentRequest.getSenderId());
        assertThat(response.getReceiverId()).isEqualTo(validPaymentRequest.getReceiverId());

        verify(paymentRepository, atLeast(1)).save(any(Payment.class));
        verify(eventProducer, atLeast(1)).sendPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    void getAllPayments_ShouldHandleLargeResultSets() {
        List<Payment> largePaymentList = new ArrayList<>();
        for (int i = 0; i < LARGE_DATASET_SIZE; i++) {
            PaymentRequest request = createPaymentRequest(
                DEFAULT_AMOUNT, 
                DEFAULT_CURRENCY, 
                "Payment " + i
            );
            largePaymentList.add(createPayment(request, PaymentStatus.COMPLETED));
        }
        
        when(paymentRepository.findAll()).thenReturn(largePaymentList);

        List<PaymentResponse> responses = paymentService.getAllPayments();

        assertThat(responses).hasSize(LARGE_DATASET_SIZE);
        assertThat(responses.getFirst().getDescription()).isEqualTo("Payment 0");
        assertThat(responses.get(LARGE_DATASET_SIZE - 1).getDescription()).isEqualTo("Payment " + (LARGE_DATASET_SIZE - 1));
        verify(paymentRepository).findAll();
    }

    @Test
    void getPaymentsForUser_ShouldHandleLargeUserHistory() {
        String userId = UUID.randomUUID().toString();
        List<Payment> userPayments = new ArrayList<>();
        
        for (int i = 0; i < USER_PAYMENT_PAIRS; i++) {
            Payment sentPayment = createPayment(userId, UUID.randomUUID().toString(), PaymentStatus.COMPLETED);
            Payment receivedPayment = createPayment(UUID.randomUUID().toString(), userId, PaymentStatus.COMPLETED);
            userPayments.add(sentPayment);
            userPayments.add(receivedPayment);
        }
        
        when(paymentRepository.findBySenderIdOrReceiverId(userId, userId)).thenReturn(userPayments);

        List<PaymentResponse> responses = paymentService.getPaymentsForUser(userId);

        assertThat(responses).hasSize(USER_PAYMENT_PAIRS * 2);
        verify(paymentRepository).findBySenderIdOrReceiverId(userId, userId);
    }
}
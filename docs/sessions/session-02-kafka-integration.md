# Step-by-Step Implementation for Kafka Integration

Let's break down the implementation process for adding Kafka to your Transactio project, preserving your existing structure and configuration.

## 1. Create a New Branch

```bash
git checkout -b phase/2-kafka-integration
```

## 2. Convert Properties Files to YAML

1. Rename `application.properties` to `application.yml`
2. Rename `application-docker.properties` to `application-docker.yml`
3. Update the content with the converted YAML from the previous artifact

## 3. Update Dependencies in pom.xml

Add these dependencies to your existing pom.xml:

```xml
<!-- Kafka Dependencies -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka-test</artifactId>
    <scope>test</scope>
</dependency>
```

## 4. Create Event Package and Classes

### Create Event Package Structure

```bash
mkdir -p src/main/java/com/transactio/event
```

### Create PaymentEventType.java

```java
package com.transactio.event;

public enum PaymentEventType {
    PAYMENT_CREATED,
    PAYMENT_PROCESSING,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,
    PAYMENT_CANCELLED
}
```

### Create PaymentEvent.java

```java
package com.transactio.event;

import com.transactio.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent implements Serializable {
    private UUID paymentId;
    private UUID userId;
    private BigDecimal amount;
    private String currency;
    private PaymentEventType eventType;
    private PaymentStatus status;
    private LocalDateTime timestamp;
    private String message;
}
```

## 5. Create Kafka Configuration

Create KafkaConfig.java in your config package:

```java
package com.transactio.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    
    @Value("${spring.kafka.topic.payment-events}")
    private String paymentEventsTopic;
    
    @Bean
    public NewTopic paymentEventsTopic() {
        return TopicBuilder
                .name(paymentEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
```

## 6. Create the Producer Service

Create PaymentEventProducer.java:

```java
package com.transactio.service;

import com.transactio.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventProducer {
    
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    
    @Value("${spring.kafka.topic.payment-events}")
    private String paymentEventsTopic;
    
    public void sendPaymentEvent(PaymentEvent event) {
        CompletableFuture<SendResult<String, PaymentEvent>> future = 
            kafkaTemplate.send(paymentEventsTopic, event.getPaymentId().toString(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent payment event: {} with offset: {}", 
                    event, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send payment event: {}", event, ex);
            }
        });
    }
}
```

## 7. Modify the Existing Payment Service

Update your existing PaymentService.java to include event publishing:

```java
package com.transactio.service;

import com.transactio.event.PaymentEvent;
import com.transactio.event.PaymentEventType;
import com.transactio.model.Payment;
import com.transactio.model.PaymentStatus;
import com.transactio.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer eventProducer;
    
    @Transactional
    public Payment createPayment(Payment payment) {
        payment.setId(UUID.randomUUID());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Publish payment created event
        PaymentEvent event = PaymentEvent.builder()
                .paymentId(savedPayment.getId())
                .userId(savedPayment.getUserId())
                .amount(savedPayment.getAmount())
                .currency(savedPayment.getCurrency())
                .eventType(PaymentEventType.PAYMENT_CREATED)
                .status(savedPayment.getStatus())
                .timestamp(LocalDateTime.now())
                .message("Payment created successfully")
                .build();
        
        eventProducer.sendPaymentEvent(event);
        
        // Simulate asynchronous processing
        processPaymentAsync(savedPayment);
        
        return savedPayment;
    }
    
    public Optional<Payment> getPaymentById(UUID id) {
        return paymentRepository.findById(id);
    }
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }
    
    public List<Payment> getPaymentsByUser(UUID userId) {
        return paymentRepository.findByUserId(userId);
    }
    
    private void processPaymentAsync(Payment payment) {
        // In a real scenario, this would be handled by a separate service
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate processing delay
                
                // Update payment status to processing
                payment.setStatus(PaymentStatus.PROCESSING);
                payment.setUpdatedAt(LocalDateTime.now());
                Payment updatedPayment = paymentRepository.save(payment);
                
                // Publish processing event
                eventProducer.sendPaymentEvent(createPaymentEvent(
                    updatedPayment, PaymentEventType.PAYMENT_PROCESSING, 
                    "Payment is being processed"));
                
                Thread.sleep(3000); // Simulate processing time
                
                // Complete the payment
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setUpdatedAt(LocalDateTime.now());
                Payment completedPayment = paymentRepository.save(payment);
                
                // Publish completed event
                eventProducer.sendPaymentEvent(createPaymentEvent(
                    completedPayment, PaymentEventType.PAYMENT_COMPLETED, 
                    "Payment completed successfully"));
                
            } catch (Exception e) {
                log.error("Error processing payment: {}", payment.getId(), e);
                payment.setStatus(PaymentStatus.FAILED);
                payment.setUpdatedAt(LocalDateTime.now());
                paymentRepository.save(payment);
                
                // Publish failed event
                eventProducer.sendPaymentEvent(createPaymentEvent(
                    payment, PaymentEventType.PAYMENT_FAILED, 
                    "Payment processing failed: " + e.getMessage()));
            }
        }).start();
    }
    
    private PaymentEvent createPaymentEvent(Payment payment, PaymentEventType eventType, String message) {
        return PaymentEvent.builder()
                .paymentId(payment.getId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .eventType(eventType)
                .status(payment.getStatus())
                .timestamp(LocalDateTime.now())
                .message(message)
                .build();
    }
}
```

## 8. Create the Notification Service

Create NotificationService.java:

```java
package com.transactio.service;

import com.transactio.event.PaymentEvent;
import com.transactio.event.PaymentEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {
    
    @KafkaListener(
        topics = "${spring.kafka.topic.payment-events}",
        groupId = "${spring.kafka.consumer.group-id.notification}"
    )
    public void handlePaymentEvent(PaymentEvent event) {
        log.info("Notification Service received event: {}", event);
        
        switch (event.getEventType()) {
            case PAYMENT_CREATED:
                sendPaymentCreatedNotification(event);
                break;
            case PAYMENT_COMPLETED:
                sendPaymentCompletedNotification(event);
                break;
            case PAYMENT_FAILED:
                sendPaymentFailedNotification(event);
                break;
            default:
                log.info("No notification for event type: {}", event.getEventType());
        }
    }
    
    private void sendPaymentCreatedNotification(PaymentEvent event) {
        // Simulate sending notification
        log.info("📧 Sending notification: Payment {} created for user {} - Amount: {} {}", 
            event.getPaymentId(), event.getUserId(), event.getAmount(), event.getCurrency());
    }
    
    private void sendPaymentCompletedNotification(PaymentEvent event) {
        log.info("✅ Sending notification: Payment {} completed for user {} - Amount: {} {}", 
            event.getPaymentId(), event.getUserId(), event.getAmount(), event.getCurrency());
    }
    
    private void sendPaymentFailedNotification(PaymentEvent event) {
        log.info("❌ Sending notification: Payment {} failed for user {} - Reason: {}", 
            event.getPaymentId(), event.getUserId(), event.getMessage());
    }
}
```

## 9. Update docker-compose.yml

Update your docker-compose.yml to include Kafka and Zookeeper:

```yaml
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
    networks:
      - transactio-network

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
    networks:
      - transactio-network

  transactio:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: transactio-payment-service
    depends_on:
      - kafka
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
      - JAVA_OPTS=-Xms256m -Xmx512m
    healthcheck:
      test: ["CMD", "wget", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    networks:
      - transactio-network

networks:
  transactio-network:
    driver: bridge
```

## 10. Add Simple Integration Test

Create a basic integration test:

```java
package com.transactio.integration;

import com.transactio.event.PaymentEvent;
import com.transactio.event.PaymentEventType;
import com.transactio.model.Payment;
import com.transactio.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"payment-events"})
class PaymentKafkaIntegrationTest {
    
    @Autowired
    private PaymentService paymentService;
    
    @Test
    void shouldCreatePaymentAndPublishEvent() throws InterruptedException {
        // Given
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency("USD");
        payment.setDescription("Test payment");
        
        // When
        Payment createdPayment = paymentService.createPayment(payment);
        
        // Then
        assertNotNull(createdPayment.getId());
        
        // Wait for async processing (in a real test, use Awaitility library)
        TimeUnit.SECONDS.sleep(7);
    }
}
```

## 11. Building and Running

```bash
# Update your project
./mvnw clean package

# Start everything with Docker Compose
docker-compose up -d

# Check the logs to see the events flowing
docker-compose logs -f
```

## 12. Test with a Sample Request

Test the payment creation endpoint:

```bash
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "amount": 100.00,
    "currency": "USD",
    "description": "Test payment"
  }'
```

## 13. Update CHANGELOG.md

Add your Kafka integration work to the changelog:

```markdown
## [Unreleased]

### Phase 2: Kafka Integration

#### [1.1.0] - 2025-05-11

##### Added
- Event-driven architecture with Apache Kafka
- Payment event model (PaymentEvent)
- Kafka producer for payment events
- Notification service as Kafka consumer
- Asynchronous payment processing flow
- Multi-container setup with Kafka and Zookeeper
- Integration tests with embedded Kafka
```

## Next Steps

1. Implement additional consumers
2. Add more robust error handling
3. Implement dead letter queues
4. Add monitoring for Kafka
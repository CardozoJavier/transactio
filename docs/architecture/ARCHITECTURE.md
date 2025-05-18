# Transactio Architecture

## Current Phase: Phase 2 - Event-Driven Architecture with Kafka

## System Overview

The system is designed to handle financial transactions with high reliability, scalability, and observability through an event-driven architecture. The system evolves through multiple phases, each adding new capabilities.

## Architecture Diagram

```
Phase 2 Architecture:

┌─────────────────┐         ┌──────────────────┐         ┌────────────┐
│                 │ HTTP    │                  │ JDBC    │            │
│   API Client    │────────▶│ Payment Service  │────────▶│  H2 (In-   │
│                 │         │  (Spring Boot)   │         │  Memory)   │
└─────────────────┘         └──────────────────┘         └────────────┘
                                     │
                                     │ Produces Events
                                     ▼
                            ┌────────────────────────┐
                            │                        │
                            │    Apache Kafka        │
                            │    (Event Broker)      │
                            │                        │
                            └────────────────────────┘
                                     │
                                     │ Consumes Events
                                     ▼
                            ┌────────────────────────┐
                            │                        │
                            │  Notification Service  │
                            │                        │
                            └────────────────────────┘
                                     │
                                     │
                            ┌────────▼────────┐
                            │     Docker      │
                            │   Container     │
                            └─────────────────┘
```

## System Components

| Component | Technology | Status | Description |
|-----------|------------|--------|-------------|
| payment-service | Spring Boot 3.3, Java 21 | ✅ Completed | Core payment processing service |
| database | H2 (Phase 1) | ✅ Completed | In-memory database for development |
| kafka | Apache Kafka 3.5+ | ✅ Completed | Event streaming platform |
| notification-service | Spring Boot 3.3, Java 21 | ✅ Completed | Service that handles payment notifications |
| containerization | Docker | ✅ Completed | Container runtime environment |

## Technology Stack Details

### Core Technologies
- **Java 21**: Latest LTS version with modern features
- **Spring Boot 3.3**: Microservices framework
- **Apache Kafka**: Event streaming platform
- **Maven**: Build and dependency management
- **Docker**: Containerization platform

### Development Tools
- **Git**: Version control
- **Docker Compose**: Multi-container orchestration
- **IntelliJ IDEA / VS Code**: IDE options

## API Endpoints (Phase 2)

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | /api/v1/payments | Create new payment | ✅ Completed |
| GET | /api/v1/payments/{id} | Get payment by ID | ✅ Completed |
| GET | /api/v1/payments | List all payments | ✅ Completed |
| GET | /api/v1/payments/status/{status} | Get payments by status | ✅ Completed |
| GET | /api/v1/payments/user/{userId} | Get payments for user | ✅ Completed |
| GET | /actuator/health | Health check | ✅ Completed |

## Event Model (Phase 2)

### Payment Event
```java
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
```

### Payment Event Types
```java
public enum PaymentEventType {
    PAYMENT_CREATED,
    PAYMENT_PROCESSING,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,
    PAYMENT_CANCELLED
}
```

## Event Flow

1. User creates payment via REST API
2. Payment Service saves payment to database
3. Payment Service publishes PAYMENT_CREATED event to Kafka
4. Payment Service asynchronously processes the payment
5. Payment Service publishes status events (PROCESSING, COMPLETED, FAILED) to Kafka
6. Notification Service consumes payment events
7. Notification Service sends appropriate notifications based on event type

## Configuration Management

### Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| SERVER_PORT | Application port | 8080 |
| SPRING_PROFILES_ACTIVE | Active Spring profile | dev |
| DB_URL | Database connection URL | jdbc:h2:mem:testdb |
| SPRING_KAFKA_BOOTSTRAP_SERVERS | Kafka bootstrap servers | localhost:9092 |

## Security Considerations (Phase 2)

- Basic authentication for API endpoints
- HTTPS configuration (disabled for local development)
- Input validation for payment data
- SQL injection prevention via JPA
- Kafka security: Currently using default (no auth) for development

## Future Architecture (Upcoming Phases)

### Phase 3: Java 21 Features
- Virtual threads for high concurrency
- Record classes for immutable data
- Enhanced pattern matching

### Phase 4: Observability
- Prometheus metrics
- Grafana dashboards
- Distributed tracing
- Centralized logging

### Phase 5: Cloud Native
- AWS Lambda functions
- DynamoDB persistence
- EventBridge integration
- CloudWatch monitoring

## Design Decisions Log

### Decision 1: Use H2 for Phase 1
- **Date**: 2025-01-XX
- **Rationale**: Simplifies initial setup and learning focus
- **Trade-offs**: Not production-ready, will migrate later

### Decision 2: Docker-first Approach
- **Date**: 2025-01-XX
- **Rationale**: Ensures consistent development environment
- **Trade-offs**: Initial learning curve for Docker

### Decision 3: Event-Driven Architecture with Kafka
- **Date**: 2025-05-11
- **Rationale**: Decouples services, enables async processing
- **Trade-offs**: Added complexity, additional infrastructure requirements

## Development Guidelines

### Code Style
- Follow Google Java Style Guide
- Use meaningful variable names
- Document all public APIs

### Git Workflow
- Feature branches from main
- Conventional commits format
- PR reviews before merging

### Testing Strategy
- Unit tests for business logic
- Integration tests for APIs
- Container tests for Docker setup

## Deployment Strategy

### Phase 2: Local Development
- Docker Compose for local deployment
    - Payment Service
    - Apache Kafka
    - Zookeeper
- Development profile configuration
- H2 console access enabled

### Future: Production Deployment
- Kubernetes orchestration
- Blue-green deployment
- Automated CI/CD pipeline
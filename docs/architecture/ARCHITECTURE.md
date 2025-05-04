# Transactio Architecture

## Current Phase: Phase 1 - Docker Basics

## System Overview

The system is designed to handle financial transactions with high reliability, scalability, and observability. The system evolves through multiple phases, each adding new capabilities.

## Architecture Diagram

```
Phase 1 Architecture:

┌─────────────────┐         ┌──────────────────┐         ┌────────────┐
│                 │ HTTP    │                  │ JDBC    │            │
│   API Client    │────────▶│ Payment Service  │────────▶│  H2 (In-   │
│                 │         │  (Spring Boot)   │         │  Memory)   │
└─────────────────┘         └──────────────────┘         └────────────┘
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
| payment-service | Spring Boot 3.3, Java 21 | 🚧 In Progress | Core payment processing service |
| database | H2 (Phase 1) | 🚧 In Progress | In-memory database for development |
| containerization | Docker | 🚧 In Progress | Container runtime environment |

## Technology Stack Details

### Core Technologies
- **Java 21**: Latest LTS version with modern features
- **Spring Boot 3.3**: Microservices framework
- **Maven**: Build and dependency management
- **Docker**: Containerization platform

### Development Tools
- **Git**: Version control
- **Docker Compose**: Multi-container orchestration
- **IntelliJ IDEA / VS Code**: IDE options

## API Endpoints (Phase 1)

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | /api/v1/payments | Create new payment | 🚧 Planned |
| GET | /api/v1/payments/{id} | Get payment by ID | 🚧 Planned |
| GET | /api/v1/payments | List all payments | 🚧 Planned |
| GET | /actuator/health | Health check | 🚧 Planned |

## Data Models (Phase 1)

### Payment Entity
```java
public class Payment {
    private UUID id;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Payment Status Enum
```java
public enum PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED
}
```

## Configuration Management

### Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| SERVER_PORT | Application port | 8080 |
| SPRING_PROFILES_ACTIVE | Active Spring profile | dev |
| DB_URL | Database connection URL | jdbc:h2:mem:testdb |

## Security Considerations (Phase 1)

- Basic authentication for API endpoints
- HTTPS configuration (disabled for local development)
- Input validation for payment data
- SQL injection prevention via JPA

## Future Architecture (Upcoming Phases)

### Phase 2: Event-Driven Architecture
- Apache Kafka integration
- Payment event publishing
- Notification service

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

### Phase 1: Local Development
- Docker Compose for local deployment
- Development profile configuration
- H2 console access enabled

### Future: Production Deployment
- Kubernetes orchestration
- Blue-green deployment
- Automated CI/CD pipeline
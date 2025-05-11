# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Phase 3: Java 21 Features (Planned)
- Virtual threads implementation
- Record classes for DTOs
- Pattern matching enhancements
- Structured concurrency

### Phase 4: Monitoring (Planned)
- Prometheus metrics integration
- Grafana dashboards
- ELK stack for logging
- Distributed tracing with OpenTelemetry

### Phase 5: AWS Integration (Planned)
- Lambda functions for payment processing
- DynamoDB for transaction storage
- EventBridge for event routing
- CloudWatch monitoring

## [1.1.0] - 2025-05-11

### Phase 2: Kafka Integration

#### Added
- Event-driven architecture with Apache Kafka
- Payment event model and types:
  - `PaymentEvent` POJO with event metadata
  - `PaymentEventType` enum for event classification (CREATED, PROCESSING, COMPLETED, FAILED)
- Kafka producer for publishing payment events:
  - `PaymentEventProducer` service to handle event publishing
  - Robust error handling and logging for producer
- Notification service as Kafka consumer:
  - Annotation-based event consumption (@KafkaListener)
  - Event-specific notification handling
- Asynchronous payment processing flow:
  - Payment status transitions through event stream
  - Transactional processing with proper event publication
- Multi-container setup with Kafka and Zookeeper:
  - Updated Docker Compose configuration
  - Dual listener configuration for internal/external access
- Configuration profiles:
  - `dev` profile for local development with IntelliJ
  - `docker` profile for containerized environment
- Integration tests with embedded Kafka
- Kafka topic configuration with 3 partitions

#### Technical Decisions
- Used Spring Kafka for seamless integration with Spring Boot
- Implemented non-blocking event publishing with CompletableFuture
- Configured Kafka for both internal and external access patterns
- Used proper serialization/deserialization for JSON events
- Created dedicated service for notification handling

#### Security
- Secure configuration for development environment
- Prepared for future security enhancements

## [1.0.0] - 2025-01-12

### Phase 1: Docker Basics

#### Added
- Initial Spring Boot payment service setup with Java 21
- Basic payment processing REST endpoints:
  - `POST /api/v1/payments` - Create new payment
  - `GET /api/v1/payments/{id}` - Get payment by ID
  - `GET /api/v1/payments` - List all payments
  - `GET /api/v1/payments/status/{status}` - Get payments by status
  - `GET /api/v1/payments/user/{userId}` - Get payments for user
- Payment domain model with proper validation
- Payment DTOs (PaymentRequest, PaymentResponse)
- Repository layer with Spring Data JPA
- Service layer with simulated payment processing
- Global exception handling
- H2 in-memory database configuration
- Docker configuration with multi-stage Dockerfile
- Docker Compose setup for local development
- Health check endpoint via Spring Boot Actuator
- Application profiles (default and docker)
- Non-root user security in Docker container
- Optimized JVM memory settings for containers
- Project documentation structure:
  - README.md with project overview
  - Architecture documentation
  - Session notes for learning progress
- Git repository structure with branching strategy
- Package structure: `com.transactio`

#### Technical Decisions
- Chose H2 for initial development to simplify setup
- Implemented multi-stage Docker builds to reduce image size
- Used Alpine Linux for smaller runtime images
- Added proper JVM memory configuration for containerized environment
- Structured project for future microservices expansion

#### Security
- Implemented non-root user in Docker containers
- Added basic input validation for payment requests
- Configured Spring Security defaults

#### Changed
- Updated project name from "Fintech Payment Processor" to "Transactio"
- Simplified package structure to `com.transactio`

#### Development Setup
- Maven 3.9.6+ required
- Java 21 required
- Docker Desktop required
- Created development documentation in `/docs` directory

## Development Notes

### Session 2 Summary (2025-05-11)
- Successfully implemented event-driven architecture with Kafka
- Added notification service as event consumer
- Configured proper event publishing and consumption
- Implemented asynchronous payment processing
- Established multi-container environment with Docker Compose
- Tested all event flows successfully
- Created foundation for future consumer services

### Session 1 Summary (2025-01-12)
- Successfully set up project structure
- Implemented core payment service functionality
- Containerized application with Docker
- Established documentation standards
- Tested all endpoints successfully
- Created foundation for event-driven architecture

### Known Issues
- H2 database is in-memory only (data lost on container restart)
- No authentication implemented yet
- Kafka security not enabled for development

### Next Steps
- Implement Java 21 features for better performance
- Add PostgreSQL for data persistence
- Implement fraud detection service as additional consumer
- Add metrics and monitoring
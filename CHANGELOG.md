# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Phase 1: Docker Basics

#### [1.0.0] - 2025-01-12

##### Added
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

##### Technical Decisions
- Chose H2 for initial development to simplify setup
- Implemented multi-stage Docker builds to reduce image size
- Used Alpine Linux for smaller runtime images
- Added proper JVM memory configuration for containerized environment
- Structured project for future microservices expansion

##### Security
- Implemented non-root user in Docker containers
- Added basic input validation for payment requests
- Configured Spring Security defaults

##### Changed
- Updated project name from "Fintech Payment Processor" to "Transactio"
- Simplified package structure to `com.transactio`

##### Development Setup
- Maven 3.9.6+ required
- Java 21 required
- Docker Desktop required
- Created development documentation in `/docs` directory

## Upcoming Phases

### Phase 2: Kafka Integration (Planned)
- Event-driven payment processing
- Kafka producer/consumer implementation
- Payment notification service
- Docker Compose multi-container orchestration

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

## Development Notes

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
- Payment processing is simulated only

### Next Steps
- Implement Kafka for event streaming
- Add PostgreSQL for data persistence
- Create notification service
- Implement proper authentication
# Transactio

A comprehensive payment processing system built to demonstrate modern fintech architecture patterns using Java, Spring Boot, and cloud technologies.

## 🎯 Project Overview

This project serves as a learning platform for exploring:
- Containerization with Docker
- Event-driven architecture with Kafka
- Modern Java features (Java 21)
- Observability and monitoring
- AWS cloud services for fintech

## 🏗️ Architecture

The system evolves through different phases:
1. **Phase 1**: Basic Spring Boot payment service (Dockerized) ✅
2. **Phase 2**: Event-driven architecture with Kafka 🚧
3. **Phase 3**: Java 21 features implementation
4. **Phase 4**: Monitoring and observability
5. **Phase 5**: AWS cloud integration

## 🛠️ Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.4+
- **Database**: H2 (Phase 1), PostgreSQL (Phase 2+)
- **Message Broker**: Apache Kafka (Phase 2)
- **Containerization**: Docker, Docker Compose
- **Cloud**: AWS (SQS, SNS, Lambda, DynamoDB)
- **Monitoring**: Prometheus, Grafana, ELK Stack

## 🚀 Getting Started

### Prerequisites
- Java 21
- Docker Desktop
- Maven 3.9+
- Git

### Local Development Setup

```bash
# Clone the repository
git clone https://github.com/CardozoJavier/transactio
cd transactio

# Build the project
./mvnw clean package

# Run with Docker
docker-compose up -d
```

## 🔄 Current Phase

**Phase 2: Kafka Integration** (In Progress)
- Implementing event-driven payment processing
- Setting up Kafka producers and consumers
- Creating notification service
- Adding PostgreSQL for persistence

### Phase 2 Features
- Event-driven payment processing flow
- Real-time payment status updates
- Payment notification service
- Persistent storage with PostgreSQL
- Enhanced Docker Compose with multi-container orchestration

## 📁 Project Structure

```
transactio/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── transactio/
│   │   │           ├── controller/    # REST endpoints
│   │   │           ├── dto/           # Data transfer objects
│   │   │           ├── event/         # Kafka events & configs
│   │   │           ├── exception/     # Exception handling
│   │   │           ├── model/         # Domain entities
│   │   │           ├── repository/    # Data access
│   │   │           └── service/       # Business logic
│   │   └── resources/
│   └── test/
├── docs/
│   ├── architecture/
│   └── sessions/
├── scripts/
├── docker-compose.yml    # Multi-container setup
├── Dockerfile
└── pom.xml
```

## 📚 Documentation

- [Architecture Overview](docs/architecture/ARCHITECTURE.md)
- [Docker Setup Guide](docs/DOCKER.md)
- [Session Notes](docs/sessions)
- [Changelog](CHANGELOG.md)

## 📝 Session Progress

Latest session: 05/11/2025
- Working on Kafka integration for event-driven architecture
- See [Session Notes](docs/sessions) for detailed progress

## 🎓 Learning Goals

- Master containerization for Java applications ✅
- Implement event-driven microservices 🚧
- Utilize modern Java features
- Build production-grade monitoring
- Deploy fintech solutions to the cloud

## 💻 API Endpoints

### Payment Service
- `POST /api/v1/payments` - Create new payment
- `GET /api/v1/payments/{id}` - Get payment by ID
- `GET /api/v1/payments` - List all payments
- `GET /api/v1/payments/status/{status}` - Get payments by status
- `GET /api/v1/payments/user/{userId}` - Get payments for user

## 📖 Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Documentation](https://docs.docker.com/)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring for Apache Kafka](https://spring.io/projects/spring-kafka)

## 👤 Author

Javier Cardozo
- GitHub: [@cardozojavier](https://github.com/cardozojavier)

## 📄 License

This project is for educational purposes.
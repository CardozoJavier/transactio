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
1. **Phase 1**: Basic Spring Boot payment service (Dockerized)
2. **Phase 2**: Event-driven architecture with Kafka
3. **Phase 3**: Java 21 features implementation
4. **Phase 4**: Monitoring and observability
5. **Phase 5**: AWS cloud integration

## 🛠️ Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.3
- **Database**: H2 (Phase 1), PostgreSQL (Later phases)
- **Message Broker**: Apache Kafka
- **Containerization**: Docker, Docker Compose
- **Cloud**: AWS (SQS, SNS, Lambda, DynamoDB)
- **Monitoring**: Prometheus, Grafana, ELK Stack

## 🚀 Getting Started

### Prerequisites
- Java 21
- Docker Desktop
- Maven 3.8+
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

## 📁 Project Structure

```
transactio/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── docs/
│   ├── architecture/
│   └── sessions/
├── scripts/
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

## 📚 Documentation

- [Architecture Overview](docs/architecture/ARCHITECTURE.md)
- [Docker Setup Guide](docs/DOCKER.md)
- [Session Notes](docs/sessions/)
- [Changelog](CHANGELOG.md)

## 🔄 Current Phase

**Phase 1: Docker Basics** (Done)
- Containerizing Spring Boot application
- Setting up local development environment
- Creating a base payment service

## 📝 Session Progress

Latest session: 04/05/2025
- See [Session Notes](docs/sessions/) for detailed progress

## 🎓 Learning Goals

- Master containerization for Java applications
- Implement event-driven microservices
- Utilize modern Java features
- Build production-grade monitoring
- Deploy fintech solutions to the cloud

## 📖 Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Documentation](https://docs.docker.com/)
- [Kafka Documentation](https://kafka.apache.org/documentation/)

## 👤 Author

Javier Cardozo
- GitHub: [@cardozojavier](https://github.com/cardozojavier)

## 📄 License

This project is for educational purposes.
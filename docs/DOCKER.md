# Docker Setup Guide for Transactio

This guide provides comprehensive instructions for setting up and using Docker with the Transactio payment processing service.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Dockerfile Explained](#dockerfile-explained)
3. [Docker Compose Configuration](#docker-compose-configuration)
4. [Building and Running](#building-and-running)
5. [Accessing the Application](#accessing-the-application)
6. [Common Commands](#common-commands)
7. [Development Workflow](#development-workflow)
8. [Troubleshooting](#troubleshooting)
9. [Best Practices](#best-practices)
10. [Next Steps](#next-steps)

## Prerequisites

Before you begin, ensure you have the following installed:

- Docker Engine (latest version recommended)
- Docker Compose (latest version recommended)
- Java 21 (for local development)
- Maven 3.9+ (for local builds)

To verify your Docker installation:

```bash
docker --version
docker-compose --version
```

## Dockerfile Explained

The Transactio project uses a multi-stage Docker build to optimize container size and performance:

### Stage 1: Build Stage

```dockerfile
# Build stage
FROM maven:3.9.6-amazoncorretto-21 AS builder
WORKDIR /app

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests
```

This stage:
- Uses Maven with Amazon Corretto Java 21
- Leverages Docker caching by downloading dependencies first
- Builds the application JAR file

### Stage 2: Runtime Stage

```dockerfile
# Runtime stage
FROM amazoncorretto:21-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership of the app.jar to spring user
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# JVM memory settings for container environment
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:MaxMetaspaceSize=128m"

# Expose port
EXPOSE 8080

# Set the entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

This stage:
- Uses Alpine Linux for a smaller runtime image
- Creates a non-root user for security
- Configures optimal JVM settings for containerized environments
- Exposes port 8080 for the application

## Docker Compose Configuration

The `docker-compose.yml` file orchestrates the Transactio container:

```yaml
services:
  transactio:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: transactio-payment-service
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - JAVA_OPTS=-Xms256m -Xmx512m -XX:MaxMetaspaceSize=128m
    healthcheck:
      test: ["CMD", "wget", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    restart: unless-stopped
```

Key configurations:
- Maps container port 8080 to host port 8080
- Activates the `docker` Spring profile
- Sets JVM memory settings for the container
- Configures health checks via Spring Boot Actuator
- Implements automatic restart policy

## Building and Running

### Building the Docker Image

To build the Docker image manually:

```bash
# From the project root directory
docker build -t transactio:latest .
```

### Running with Docker Compose

To start the application with Docker Compose:

```bash
# Start in detached mode
docker-compose up -d

# View logs
docker-compose logs -f
```

### Stopping the Application

```bash
# Stop while preserving containers
docker-compose stop

# Stop and remove containers
docker-compose down
```

## Accessing the Application

Once the container is running, you can access:

- **REST API**: http://localhost:8080/api/v1/payments
- **Health Check**: http://localhost:8080/actuator/health
- **H2 Console** (dev only): http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:transactiodb`
  - Username: `sa`
  - Password: (leave empty)

## Common Commands

### View Container Status

```bash
# List running containers
docker ps

# View detailed container info
docker inspect transactio-payment-service

# View container logs
docker logs -f transactio-payment-service
```

### Testing the API

The project includes a test script to verify endpoints:

```bash
# Make the script executable
chmod +x scripts/test-endpoints.sh

# Run the tests
./scripts/test-endpoints.sh
```

## Development Workflow

### Recommended Workflow

1. Develop and test code locally
2. Build with Maven: `mvn clean package`
3. Build Docker image: `docker build -t transactio:latest .`
4. Run with Docker Compose: `docker-compose up -d`
5. Verify functionality with the test script

### Hot Reloading (Future Enhancement)

In future updates, we plan to add development-mode configurations with volume mounts for hot reloading.

## Troubleshooting

### Common Issues and Solutions

#### Container Fails Health Check

```bash
# Check logs for errors
docker logs transactio-payment-service

# Verify the application started correctly
curl http://localhost:8080/actuator/health
```

#### Out of Memory Errors

If you see OOM errors in the logs, adjust the JVM settings:

```yaml
# In docker-compose.yml
environment:
  - JAVA_OPTS=-Xms128m -Xmx384m -XX:MaxMetaspaceSize=96m
```

#### Database Connection Issues

For H2 connection issues, ensure the database URL matches:

```
# Check application-docker.properties
spring.datasource.url=jdbc:h2:mem:transactiodb
```

## Best Practices

### Container Security

- We use a non-root user (`spring`)
- JVM memory limits are explicitly set
- Health checks monitor application status

### Image Optimization

- Multi-stage builds reduce image size
- Dependency caching improves build times
- Alpine base image minimizes footprint

### Resource Management

- Set appropriate memory limits for JVM
- Use health checks to verify application state
- Implement graceful shutdown

## Next Steps

As we move to Phase 2 (Kafka Integration), we'll enhance our Docker setup:

1. Add Kafka and Zookeeper containers
2. Implement container networking
3. Configure volume persistence for PostgreSQL
4. Set up environment-specific configurations
5. Introduce healthcheck dependencies between services

Look for the updated Docker configuration in the Phase 2 branch soon!

---

## Reference Material

- [Official Docker Documentation](https://docs.docker.com/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)

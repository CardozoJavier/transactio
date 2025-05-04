# Session 1: Docker Basics for Java Developers

**Date**: 04/05/2025  
**Phase**: 1 - Docker Basics  
**Duration**: 2 hours

## Session Objectives

- Understand Docker fundamentals for Java applications
- Create a basic Spring Boot payment service
- Containerize the application with Docker
- Set up local development environment with Docker Compose

## Part 1: Theory (40 minutes)

### Why Docker for Java/Spring Boot Applications?

1. **Consistency**: "Works on my machine" problem solved
2. **Isolation**: Dependencies contained within Docker image
3. **Portability**: Same container runs anywhere
4. **Microservices Ready**: Foundation for distributed systems
5. **Dev/Prod Parity**: Same environment across stages

### Docker Concepts for Java Developers

1. **Images vs Containers**
    - Image: Blueprint (like a Java class)
    - Container: Running instance (like a Java object)

2. **Dockerfile**
    - Recipe to build images
    - Multi-stage builds for Java apps
    - Layer caching optimization

3. **Docker Compose**
    - Orchestrate multiple containers
    - Define services, networks, volumes
    - Perfect for local development

4. **Best Practices for Java Apps**
    - Use official OpenJDK images
    - Multi-stage builds to reduce size
    - Proper JVM memory settings
    - Health checks for Spring Boot

## Part 2: Hands-on Practice (80 minutes)

### Step 1: Create Spring Boot Payment Service

First, let's create a Spring Boot project structure.

```bash
# From your project root
mkdir -p src/main/java/com/transactio/payment/controller
mkdir -p src/main/java/com/transactio/payment/service
mkdir -p src/main/java/com/transactio/payment/model
mkdir -p src/main/java/com/transactio/payment/repository
```

Create `pom.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.0</version>
        <relativePath/>
    </parent>

    <groupId>com.transactio</groupId>
    <artifactId>transactio</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>transactio</name>
    <description>Payment Processing Service</description>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 2: Create Payment Service Application

Create basic Spring Boot application files as per the structure above.

### Step 3: Create Dockerfile

Create `Dockerfile` in project root:
```dockerfile
# Build stage
FROM maven:3.9.6-amazoncorretto-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# JVM memory settings for container environment
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Step 4: Create Docker Compose File

Create `docker-compose.yml`:
```yaml
version: '3.8'

services:
  payment-service:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - JAVA_OPTS=-Xms256m -Xmx512m
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

## Next Steps

1. Complete the Spring Boot application implementation
2. Build and run with Docker
3. Test the containerized application
4. Add Docker optimization techniques

## Homework

1. Research Docker multi-stage builds benefits
2. Explore Docker networking basics
3. Read about Docker volumes for data persistence

## Session Summary

- ✅ Set up project structure
- ✅ Created initial documentation
- ✅ Designed basic Dockerfile
- ✅ Prepared Docker Compose configuration

## Next Session: Completing the Payment Service

We'll implement the actual Java code for the payment service and test our Docker configuration.
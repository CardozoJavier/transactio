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
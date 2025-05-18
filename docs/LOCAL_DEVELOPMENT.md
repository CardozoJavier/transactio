# IntelliJ IDEA Run Configuration for Local Development

This guide helps you set up a run configuration for developing the Transactio application locally with IntelliJ IDEA while using Docker for supporting services.

## Prerequisites

1. Make the helper scripts executable:
   ```bash
   chmod +x scripts/start-local-dev.sh
   chmod +x scripts/stop-local-dev.sh
   chmod +x scripts/start-full-docker.sh
   ```

## Starting Supporting Services

Before running the application in IntelliJ, start the supporting services:

```bash
./scripts/start-local-dev.sh
```

This will start PostgreSQL, Zookeeper, and Kafka in Docker containers.

## IntelliJ IDEA Run Configuration

1. Open IntelliJ IDEA and go to `Run` > `Edit Configurations...`
2. Click the `+` button and select `Application`
3. Configure the following:

   - **Name**: `TransactioApplication Local`
   - **Main class**: `com.transactio.TransactioApplication`
   - **VM options**: `-Xms256m -Xmx512m -XX:MaxMetaspaceSize=128m`
   - **Program arguments**: Leave empty
   - **Environment variables**:
     ```
     SPRING_PROFILES_ACTIVE=postgres;
     SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092;
     DB_HOST=localhost;
     DB_PORT=5432;
     DB_NAME=transactiodb;
     DB_USERNAME=postgres;
     DB_PASSWORD=postgres
     ```
   - **Working directory**: `$MODULE_WORKING_DIR$`
   - **Use classpath of module**: `transactio`

4. Click `Apply` and then `OK`

## Running the Application

1. Select the `TransactioApplication Local` configuration from the dropdown in the top-right corner
2. Click the green play button to start the application

## Stopping Services

When you're done developing, stop the Docker services:

```bash
./scripts/stop-local-dev.sh
```

## Full Docker Mode

If you want to run the entire application including the Transactio service in Docker:

```bash
./scripts/start-full-docker.sh
```

## Troubleshooting

### Connection Issues

If you experience connection issues:

1. **PostgreSQL**: Verify it's running with `docker ps | grep postgres`
2. **Kafka**: Check Kafka logs with `docker logs kafka`
3. **Application Logs**: Check for connection errors in the IntelliJ console

### Port Conflicts

If you have port conflicts, modify the Docker Compose file to use different ports, and update your run configuration accordingly.
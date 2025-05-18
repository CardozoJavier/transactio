#!/bin/bash
# Run supporting services without Transactio application
echo "Starting supporting services (PostgreSQL, Kafka, Zookeeper)..."
docker-compose up postgres zookeeper kafka
echo "Services started. You can now run the Transactio application in IntelliJ."
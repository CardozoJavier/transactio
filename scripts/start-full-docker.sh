#!/bin/bash
# Run the Transactio application with all services in Docker
echo "Starting all services including Transactio application..."
docker-compose --profile full up -d
echo "All services started."
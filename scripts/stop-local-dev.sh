#!/bin/bash
# Stop supporting services
echo "Stopping all services..."
docker-compose down
echo "Services stopped."
#!/bin/bash

echo "Testing Transactio API endpoints..."
echo "=================================="

# Base URL
BASE_URL="http://localhost:8080"

# Check health
echo "1. Checking application health..."
curl -s "$BASE_URL/actuator/health" | json_pp
echo -e "\n"

# Create a payment
echo "2. Creating a payment..."
PAYMENT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/v1/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 150.50,
    "currency": "USD",
    "senderId": "user001",
    "receiverId": "user002",
    "description": "Test payment from Docker"
  }')

echo $PAYMENT_RESPONSE | json_pp
PAYMENT_ID=$(echo $PAYMENT_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo -e "\n"

# Get specific payment
echo "3. Getting payment by ID: $PAYMENT_ID"
curl -s "$BASE_URL/api/v1/payments/$PAYMENT_ID" | json_pp
echo -e "\n"

# Get all payments
echo "4. Getting all payments..."
curl -s "$BASE_URL/api/v1/payments" | json_pp
echo -e "\n"

echo "All tests completed!"
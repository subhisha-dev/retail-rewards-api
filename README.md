# retail-rewards-api
Spring Boot REST API to calculate cusotmer reward points based on transactions.
Rule: $2 for every dollar spent over $100, $1 for every dollar spent between $50-$100.

## Tech Stack
Java 21, Spring Boot 3, Junit 5, Maven

## Build and Execution Steps
1. mvn clean install
2. mvn spring-boot:run
3. API runs on 'http:localhost:8080'

##API Endpoints

### 1. Get Rewards for customer
'GET /api/rewards/byMonths/{customerId}?months=3'

**Example:** 'GET /api/awards/1?months=3'

**Response:**

json
{
    "customerId": "1",
    "customerName": "John",
    "periodInMonths": 3,
    "totalTransactions": 4,
    "monthlyRewardPoints": {
    "2026-04": 131.74,
    "2026-05": 25,
    "2026-06": 250
    },
    "totalRewardPoints": 406.74
}


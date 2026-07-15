# retail-rewards-api

Spring Boot REST API to calculate customer reward points based on transactions.

## Rules
- $2 for every dollar spent over $100
- $1 for every dollar spent between $50 and $100 (the portion over $50 up to 100)

## Tech stack
- Java 21
- Spring Boot 3.5.x
- Spring Data JPA (Hibernate)
- H2 (in-memory) for demo (default) — MySQL supported via profile
- Maven, JUnit 5, Mockito

## Quick start
1. Build:

```bash
mvn clean package
```

2. Run (H2 in-memory, default):

```bash
mvn spring-boot:run
```

3. App runs on http://localhost:8095 (see `src/main/resources/application.properties`)

To run with MySQL (requires a running MySQL server and correct credentials in `application-mysql.properties`):

```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

## Configuration and profiles
- `application.properties` — default configuration (H2 in-memory).
- `application-h2.properties` — explicit H2 profile.
- `application-mysql.properties` — MySQL configuration. Activate with `-Dspring.profiles.active=mysql`.

Database initialization
- `schema.sql` and `data.sql` (in `src/main/resources`) are executed automatically by Spring Boot on startup for the embedded H2 profile. `schema.sql` creates the `transactions` table and `data.sql` inserts sample rows used by the demo.

## Design & architecture

This project follows a layered architecture with a clear separation of concerns:

- Controller layer (`com.charter.reward.controller`)
  - HTTP endpoints and request validation (`RewardsController`).

- Service layer (`com.charter.reward.service`)
  - Business logic and response building (`RewardsService`). Calculates monthly and total reward points and constructs `RewardsResponse` objects.

- Data layer / Repository (`com.charter.reward.repository`)
  - `ITransactionDataSource` — interface abstraction that defines the data access contract (findAll, findByCustomerIdAndDateAfter, findByCustomerIdAndDateBetween). This enables easy swapping of data sources (H2, MySQL, remote API, CSV, etc.) without changing service logic.
  - `ITransactionJpaRepository` — Spring Data JPA repository for `TransactionEntity`.
  - `TransactionRepository` — implementation of `ITransactionDataSource` backed by JPA.

- Entities/Models
  - `TransactionEntity` — JPA entity mapped to `transactions` table.
  - `Transaction` — model used within service layer (DTO-like model mapped from `TransactionEntity`).
  - `RewardsResponse` — response DTO returned by controllers. Uses `BigDecimal` for values that represent points.

- Utilities
  - `RewardsCalculator` — encapsulates reward calculation logic using `BigDecimal` for precision.
  - `ValidationUtils` — shared validation logic for controller inputs.

Key design decisions
- Use `BigDecimal` for monetary and reward point calculations to avoid floating-point precision errors.
- Validation happens at the controller (entry point). The service trusts controller-validated inputs.
- Use Spring Data JPA (with an interface abstraction) to keep the data source swappable and the service layer decoupled from persistence details.

## Assumptions
- Transactions contain `customerId`, `customerName`, `transactionAmount` and `transactionDate` for reward-eligible records.
- `transactionAmount` and reward points are represented with `BigDecimal` (scale 2) and rounding mode HALF_UP.
- Date range queries are inclusive: a transaction on `startDate` or `endDate` is included.

## Logging guidance
- `INFO` for startup events and important user-facing actions.
- `DEBUG` for internal processing and verbose traces (per-request internals).
- `TRACE` only for extremely detailed traces when diagnosing issues.

## Endpoints

Base path: `/api/rewards`

### 1) Get rewards for all customers

- Method: GET
- Path: `/api/rewards`
- Description: Returns reward summaries for all customers (aggregated monthly). The period is derived from available transactions when no explicit date range is provided.

Example request

```http
GET http://localhost:8095/api/rewards/allRewards
```

Successful response (200)

```json
[
  {
    "customerId": "1",
    "customerName": "John",
    "periodInMonths": 4,
    "totalTransactions": 5,
    "transactions": [
      {
        "transactionId": "T1",
        "transactionDate": "2026-04-10",
        "transactionAmount": 120.87,
        "rewardPoints": 91.74,
        "year": 2026,
        "month": "APRIL"
      },
      {
        "transactionId": "T2",
        "transactionDate": "2026-04-15",
        "transactionAmount": 90.00,
        "rewardPoints": 40.00,
        "year": 2026,
        "month": "APRIL"
      },
      {
        "transactionId": "T3",
        "transactionDate": "2026-05-20",
        "transactionAmount": 75.00,
        "rewardPoints": 25.00,
        "year": 2026,
        "month": "MAY"
      },
      {
        "transactionId": "T4",
        "transactionDate": "2026-06-10",
        "transactionAmount": 200.00,
        "rewardPoints": 250.00,
        "year": 2026,
        "month": "JUNE"
      },
      {
        "transactionId": "T5",
        "transactionDate": "2026-02-10",
        "transactionAmount": 390.05,
        "rewardPoints": 630.10,
        "year": 2026,
        "month": "FEBRUARY"
      }
    ],
    "monthlyRewardPoints": [
      {
        "year": 2026,
        "month": "APRIL",
        "rewardPoints": 131.74
      },
      {
        "year": 2026,
        "month": "FEBRUARY",
        "rewardPoints": 630.10
      },
      {
        "year": 2026,
        "month": "JUNE",
        "rewardPoints": 250.00
      },
      {
        "year": 2026,
        "month": "MAY",
        "rewardPoints": 25.00
      }
    ],
    "totalRewardPoints": 1036.84,
    "startDate": null,
    "endDate": null
  },
  {
    "customerId": "2",
    "customerName": "Alex",
    "periodInMonths": 2,
    "totalTransactions": 2,
    "transactions": [
      {
        "transactionId": "T6",
        "transactionDate": "2026-05-05",
        "transactionAmount": 50.16,
        "rewardPoints": 0.16,
        "year": 2026,
        "month": "MAY"
      },
      {
        "transactionId": "T7",
        "transactionDate": "2026-06-25",
        "transactionAmount": 200.00,
        "rewardPoints": 250.00,
        "year": 2026,
        "month": "JUNE"
      }
    ],
    "monthlyRewardPoints": [
      {
        "year": 2026,
        "month": "JUNE",
        "rewardPoints": 250.00
      },
      {
        "year": 2026,
        "month": "MAY",
        "rewardPoints": 0.16
      }
    ],
    "totalRewardPoints": 250.16,
    "startDate": null,
    "endDate": null
  },
  {
    "customerId": "3",
    "customerName": "Alice",
    "periodInMonths": 2,
    "totalTransactions": 2,
    "transactions": [
      {
        "transactionId": "T8",
        "transactionDate": "2026-05-18",
        "transactionAmount": 150.00,
        "rewardPoints": 150.00,
        "year": 2026,
        "month": "MAY"
      },
      {
        "transactionId": "T9",
        "transactionDate": "2026-06-26",
        "transactionAmount": 900.00,
        "rewardPoints": 1650.00,
        "year": 2026,
        "month": "JUNE"
      }
    ],
    "monthlyRewardPoints": [
      {
        "year": 2026,
        "month": "JUNE",
        "rewardPoints": 1650.00
      },
      {
        "year": 2026,
        "month": "MAY",
        "rewardPoints": 150.00
      }
    ],
    "totalRewardPoints": 1800.00,
    "startDate": null,
    "endDate": null
  },
  {
    "customerId": "4",
    "customerName": "Peter",
    "periodInMonths": 2,
    "totalTransactions": 2,
    "transactions": [
      {
        "transactionId": "T10",
        "transactionDate": "2026-05-27",
        "transactionAmount": 300.00,
        "rewardPoints": 450.00,
        "year": 2026,
        "month": "MAY"
      },
      {
        "transactionId": "T11",
        "transactionDate": "2026-06-12",
        "transactionAmount": 1000.00,
        "rewardPoints": 1850.00,
        "year": 2026,
        "month": "JUNE"
      }
    ],
    "monthlyRewardPoints": [
      {
        "year": 2026,
        "month": "JUNE",
        "rewardPoints": 1850.00
      },
      {
        "year": 2026,
        "month": "MAY",
        "rewardPoints": 450.00
      }
    ],
    "totalRewardPoints": 2300.00,
    "startDate": null,
    "endDate": null
  }
]
```

### 2) Get rewards for a single customer (all available transactions)

- Method: GET
- Path: `/api/rewards/{customerId}`
- Description: Return aggregated rewards for the specified customer across all available transactions.

Example request

```http
GET http://localhost:8095/api/rewards/1
```

Successful response (200)

```json
{
  "customerId": "1",
  "customerName": "John",
  "periodInMonths": 4,
  "totalTransactions": 5,
  "transactions": [
    {
      "transactionId": "T1",
      "transactionDate": "2026-04-10",
      "transactionAmount": 120.87,
      "rewardPoints": 91.74,
      "year": 2026,
      "month": "APRIL"
    },
    {
      "transactionId": "T2",
      "transactionDate": "2026-04-15",
      "transactionAmount": 90.00,
      "rewardPoints": 40.00,
      "year": 2026,
      "month": "APRIL"
    },
    {
      "transactionId": "T3",
      "transactionDate": "2026-05-20",
      "transactionAmount": 75.00,
      "rewardPoints": 25.00,
      "year": 2026,
      "month": "MAY"
    },
    {
      "transactionId": "T4",
      "transactionDate": "2026-06-10",
      "transactionAmount": 200.00,
      "rewardPoints": 250.00,
      "year": 2026,
      "month": "JUNE"
    },
    {
      "transactionId": "T5",
      "transactionDate": "2026-02-10",
      "transactionAmount": 390.05,
      "rewardPoints": 630.10,
      "year": 2026,
      "month": "FEBRUARY"
    }
  ],
  "monthlyRewardPoints": [
    {
      "year": 2026,
      "month": "APRIL",
      "rewardPoints": 131.74
    },
    {
      "year": 2026,
      "month": "FEBRUARY",
      "rewardPoints": 630.10
    },
    {
      "year": 2026,
      "month": "JUNE",
      "rewardPoints": 250.00
    },
    {
      "year": 2026,
      "month": "MAY",
      "rewardPoints": 25.00
    }
  ],
  "totalRewardPoints": 1036.84,
  "startDate": null,
  "endDate": null
}
```

Errors

- `404` CustomerNotFoundException — when the customer has no reward-eligible transactions.

### 3) Get rewards for a customer for a specific date range

- Method: GET
- Path: `/api/rewards/byPeriod/{customerId}`
- Query params (required): `startDate=YYYY-MM-DD` and `endDate=YYYY-MM-DD`
- Description: Return rewards aggregated per month for the given inclusive date range.

Example request

```http
GET http://localhost:8095/api/rewards/byPeriod/1?startDate=2026-04-09&endDate=2026-06-30
```

Successful response (200)

```json
{
  "customerId": "1",
  "customerName": "John",
  "periodInMonths": 3,
  "totalTransactions": 4,
  "transactions": [
    {
      "transactionId": "T1",
      "transactionDate": "2026-04-10",
      "transactionAmount": 120.87,
      "rewardPoints": 91.74,
      "year": 2026,
      "month": "APRIL"
    },
    {
      "transactionId": "T2",
      "transactionDate": "2026-04-15",
      "transactionAmount": 90.00,
      "rewardPoints": 40.00,
      "year": 2026,
      "month": "APRIL"
    },
    {
      "transactionId": "T3",
      "transactionDate": "2026-05-20",
      "transactionAmount": 75.00,
      "rewardPoints": 25.00,
      "year": 2026,
      "month": "MAY"
    },
    {
      "transactionId": "T4",
      "transactionDate": "2026-06-10",
      "transactionAmount": 200.00,
      "rewardPoints": 250.00,
      "year": 2026,
      "month": "JUNE"
    }
  ],
  "monthlyRewardPoints": [
    {
      "year": 2026,
      "month": "APRIL",
      "rewardPoints": 131.74
    },
    {
      "year": 2026,
      "month": "JUNE",
      "rewardPoints": 250.00
    },
    {
      "year": 2026,
      "month": "MAY",
      "rewardPoints": 25.00
    }
  ],
  "totalRewardPoints": 406.74,
  "startDate": "2026-04-09",
  "endDate": "2026-06-30"
}
```

Errors

- `400` ConstraintViolationException — invalid input (missing/blank customerId, null dates, or endDate before startDate).
- `404` CustomerNotFoundException — no valid transactions for given customer/period.

## Response field descriptions

- `customerId` — string customer identifier
- `customerName` — display name
- `periodInMonths` — number of months the response covers (derived from dates or number of unique months)
- `totalTransactions` — count of reward-eligible transactions included
- `monthlyRewardPoints` — object keyed by `YYYY-MM` where values are `BigDecimal` (scale 2)
- `totalRewardPoints` — total points across all months (BigDecimal)
- `startDate` / `endDate` — provided date range or null when not supplied

## Testing
- Unit and integration tests are located under `src/test`.
- Controller unit tests verify validation and endpoint behavior.
- Service tests focus on business logic and `BigDecimal` arithmetic.

## Performance & next steps
- For larger datasets, move aggregation to the database (JPQL or native SQL) and add pagination.
- Add authentication/authorization for production use.
- Consider caching or pre-aggregated tables if rewards calculation becomes expensive.

## Contributing
- Open an issue or PR for bugs or enhancements. Follow existing project conventions for code style and tests.

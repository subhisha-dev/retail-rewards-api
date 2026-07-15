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

### 2) Get rewards for a single customer (all available transactions for a requested customer)

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

### 3) Get rewards by customer when customer id is null – Error scenario

- Method: GET
- Path: `/api/rewards/{customerId}`
- Description: Return error when customerId is not provided.

```http
GET http://localhost:8095/api/rewards/null
```

Exception Scenarios – 404 Not Found:

```json
{
"timestamp": "2026-07-15T15:55:48.9592845",
"message": "Customer ID:null not found",
"error": "Not Found",
"status": 404
}
```
### 3.1) Get rewards for the customer when invalid customer id is provided

- Method: GET
- Path: `/api/rewards/byPeriod/{customerId}`
- Query params (required): `startDate=YYYY-MM-DD` and `endDate=YYYY-MM-DD`
- Description: Return rewards aggregated per month for the given inclusive date range.

Example request

```http
GET http://localhost:8095/api/rewards/999
```

Failure response (404)

```json
{
  "timestamp": "2026-07-15T16:21:47.0978627",
  "message": "Customer ID:999 not found",
  "error": "Not Found",
  "status": 404
}
```

### 4) Get rewards by customer when customer id has white spaces – Error scenario

- Method: GET
- Path: `/api/rewards/{customerId}`
- Description: Return error when there are white spaces in customer id passed in the request.

```http
GET http://localhost:8095/api/rewards/ 123 
```

Exception Scenarios – 404 Not Found:

```json
{
  "timestamp": "2026-07-15T16:00:21.8997483",
  "message": "Customer ID: 123  not found",
  "error": "Not Found",
  "status": 404
}
```

### 5) Get rewards by customer when customer id has white spaces – Error scenario

- Method: GET
- Path: `/api/rewards/{customerId}`
- Description: Return error when there are white spaces in customer id passed in the request.

```http
GET http://localhost:8095/api/rewards/ 123 
```

Exception Scenarios – 404 Not Found:

```json
{
  "timestamp": "2026-07-15T16:00:21.8997483",
  "message": "Customer ID: 123  not found",
  "error": "Not Found",
  "status": 404
}
```

### 6) Get rewards by customer when customer id is Empty – Error scenario

- Method: GET
- Path: `/api/rewards/{customerId}`
- Description: Return constraint violation error when the customer id passed in the request is empty.

```http
GET http://localhost:8095/api/rewards/
```

Exception Scenarios – 400 Bad Request:

```json
{
  "timestamp": "2026-07-15T16:04:58.4119256",
  "message": "getRewardsByCustomer.customerId: must not be blank",
  "error": "Bad Request",
  "status": 400
}
```


### 7) Get rewards for a customer for a specific date range

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
Exception Scenarios: 

### 7) Get rewards for customer who is not present or null for a specific date range

- Method: GET
- Path: `/api/rewards/byPeriod/{customerId}`
- Query params (required): `startDate=YYYY-MM-DD` and `endDate=YYYY-MM-DD`
- Description: Return rewards aggregated per month for the given inclusive date range.

Example request

```http
GET http://localhost:8095/api/rewards/byPeriod/ ?startDate=2026-04-09&endDate=2026-06-30
```

Failure response (400)

```json
{
  "timestamp": "2026-07-15T16:08:03.7107663",
  "message": "getCustomerRewardsForPeriod.customerId: must not be blank",
  "error": "Bad Request",
  "status": 400
}
```
### 8) Get rewards for customer who is not present or null for a specific date range

- Method: GET
- Path: `/api/rewards/byPeriod/{customerId}`
- Query params (required): `startDate=YYYY-MM-DD` and `endDate=YYYY-MM-DD`
- Description: Return rewards aggregated per month for the given inclusive date range.

Example request

```http
GET http://localhost:8095/api/rewards/byPeriod/1?startDate=null&endDate=2026-06-30
```

Failure response (400)

```json
{
  "timestamp": "2026-07-15T16:15:21.7802331",
  "message": "Invalid 'startDate': 'null'. Expected format: YYYY-MM-DD",
  "error": "Bad Request",
  "status": 400
}
```

### 9) Get rewards for customer when the start date is empty

- Method: GET
- Path: `/api/rewards/byPeriod/{customerId}`
- Query params (required): `startDate=YYYY-MM-DD` and `endDate=YYYY-MM-DD`
- Description: Return rewards aggregated per month for the given inclusive date range.

Example request

```http
GET http://localhost:8095/api/rewards/byPeriod/1?startDate=&endDate=2026-06-29
```

Failure response (400)

```json
{
  "timestamp": "2026-07-15T16:15:40.8296488",
  "message": "Invalid or missing request parameter 'startDate'. Expected format for dates: YYYY-MM-DD",
  "error": "Bad Request",
  "status": 400
}
```

### 10) Get rewards for the customer when the end date is empty

- Method: GET
- Path: `/api/rewards/byPeriod/{customerId}`
- Query params (required): `startDate=YYYY-MM-DD` and `endDate=YYYY-MM-DD`
- Description: Return rewards aggregated per month for the given inclusive date range.

Example request

```http
GET http://localhost:8095/api/rewards/byPeriod/1?startDate=2026-04-09&endDate=
```

Failure response (400)

```json
{
  "timestamp": "2026-07-15T16:17:39.2349964",
  "message": "Invalid or missing request parameter 'endDate'. Expected format for dates: YYYY-MM-DD",
  "error": "Bad Request",
  "status": 400
}
```

### 11) Get rewards for the customer when the start date is missing in the path

- Method: GET
- Path: `/api/rewards/byPeriod/{customerId}`
- Query params (required): `startDate=YYYY-MM-DD` and `endDate=YYYY-MM-DD`
- Description: Return rewards aggregated per month for the given inclusive date range.

Example request

```http
GET http://localhost:8095/api/rewards/byPeriod/1?endDate=2026-06-30
```

Failure response (400)

```json
{
  "timestamp": "2026-07-15T16:19:10.6148324",
  "message": "Invalid or missing request parameter 'startDate'. Expected format for dates: YYYY-MM-DD",
  "error": "Bad Request",
  "status": 400
}
```


### 12) Get rewards for the customer when the request has invalid dates

- Method: GET
- Path: `/api/rewards/byPeriod/{customerId}`
- Query params (required): `startDate=YYYY-MM-DD` and `endDate=YYYY-MM-DD`
- Description: Return rewards aggregated per month for the given inclusive date range.

Example request

```http
GET http://localhost:8095/api/rewards/byPeriod/1?startDate=04-09-2026&endDate=06-30-2026
```

Failure response (400)

```json
{
  "timestamp": "2026-07-15T16:19:26.9159976",
  "message": "Invalid 'startDate': '04-09-2026'. Expected format: YYYY-MM-DD",
  "error": "Bad Request",
  "status": 400
}
```

### 13) Get rewards for the customer with non-existent dates

- Method: GET
- Path: `/api/rewards/byPeriod/{customerId}`
- Query params (required): `startDate=YYYY-MM-DD` and `endDate=YYYY-MM-DD`
- Description: Return rewards aggregated per month for the given inclusive date range.

Example request

```http
GET http://localhost:8095/api/rewards/byPeriod/1?startDate=2026-02-30&endDate=2026-06-30
```

Failure response (400)

```json
{
  "timestamp": "2026-07-15T16:20:29.7131428",
  "message": "Invalid 'startDate': '2026-02-30'. Expected format: YYYY-MM-DD",
  "error": "Bad Request",
  "status": 400
}
```

### 14) Get rewards for the customer with invalid date pattern

- Method: GET
- Path: `/api/rewards/byPeriod/{customerId}`
- Query params (required): `startDate=YYYY-MM-DD` and `endDate=YYYY-MM-DD`
- Description: Return rewards aggregated per month for the given inclusive date range.

Example request

```http
GET http://localhost:8095/api/rewards/byPeriod/1?startDate=abcd-ef-gh&endDate=2026-06-30
```

Failure response (400)

```json
{
  "timestamp": "2026-07-15T16:23:25.1023398",
  "message": "Invalid 'startDate': 'abcd-ef-gh'. Expected format: YYYY-MM-DD",
  "error": "Bad Request",
  "status": 400
}
```


### 15) Get rewards for the customer when the end date is before the start date

- Method: GET
- Path: `/api/rewards/byPeriod/{customerId}`
- Query params (required): `startDate=YYYY-MM-DD` and `endDate=YYYY-MM-DD`
- Description: Return rewards aggregated per month for the given inclusive date range.

Example request

```http
GET http://localhost:8095/api/rewards/byPeriod/1?startDate=2026-06-30&endDate=2026-04-09
```

Failure response (400)

```json
{
  "timestamp": "2026-07-15T16:24:36.5886959",
  "message": "Invalid Input to the controller method",
  "error": "Bad Request",
  "status": 400
}
```

### 15) Get rewards for the customer when an invalid customer id is given within the date range

- Method: GET
- Path: `/api/rewards/byPeriod/{customerId}`
- Query params (required): `startDate=YYYY-MM-DD` and `endDate=YYYY-MM-DD`
- Description: Return rewards aggregated per month for the given inclusive date range.

Example request

```http
GET http://localhost:8095/api/rewards/byPeriod/999?startDate=2026-04-09&endDate=2026-06-30
```

Failure response (404)

```json
{
  "timestamp": "2026-07-15T16:27:23.0538159",
  "message": "No valid transactions found for CustomerID:999 for the provided period",
  "error": "Not Found",
  "status": 404
}
```

### 16) Get rewards for the customer when start date and end date is same

- Method: GET
- Path: `/api/rewards/byPeriod/{customerId}`
- Query params (required): `startDate=YYYY-MM-DD` and `endDate=YYYY-MM-DD`
- Description: Return rewards aggregated per month for the given inclusive date range.

Example request

```http
GET http://localhost:8095/api/rewards/byPeriod/1?startDate=2026-04-10&endDate=2026-04-10
```

Success Response (200)

```json
{
    "customerId": "1",
    "customerName": "John",
    "periodInMonths": 1,
    "totalTransactions": 1,
    "transactions": [
        {
            "transactionId": "T1",
            "transactionDate": "2026-04-10",
            "transactionAmount": 120.87,
            "rewardPoints": 91.74,
            "year": 2026,
            "month": "APRIL"
        }
    ],
    "monthlyRewardPoints": [
        {
            "year": 2026,
            "month": "APRIL",
            "rewardPoints": 91.74
        }
    ],
    "totalRewardPoints": 91.74,
    "startDate": "2026-04-10",
    "endDate": "2026-04-10"
}
```
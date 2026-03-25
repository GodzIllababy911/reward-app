# Reward Points API

## Overview

This project implements a RESTful API to calculate reward points for customers based on their transaction history. The system evaluates transactions over a rolling 3-month period and returns a monthly breakdown along with total reward points.

---

## Problem Statement

A retailer offers a rewards program with the following rules:

* **2 points** for every dollar spent **above $100**
* **1 point** for every dollar spent **between $50 and $100** 
* **0 points** for amounts **≤ $50**

### Example Calculation

A transaction of **$120**:
* Amount ≤ $50 → 0 points
* Amount $50-$100 → 50 points (1 × $50)
* Amount > $100 → 40 points (2 × $20)
* **Total = 90 points**

---

## Features

*  RESTful API endpoints
*  Rolling 3-month reward calculation window
*  Monthly breakdown of earned points
*  Comprehensive error handling
*  Production-ready logging
*  Full test coverage (Unit & Integration)
*  Database persistence with MySQL
*  Input validation and sanitization

---

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Programming language |
| Spring Boot 3.2.5 | Application framework |
| Spring Web MVC | REST API handling |
| Spring Data JPA | Database operations |
| MySQL | Data persistence |
| Maven | Dependency management |
| JUnit 5 & Mockito | Testing framework |
| SLF4J/Logback | Logging |
| Lombok | Code reduction |

---

## Project Architecture

```
src/
 ├── main/
 │   ├── java/com/basic/app/
 │   │   ├── controller/     # REST controllers
 │   │   ├── service/        # Business logic
 │   │   ├── repository/     # Data access layer
 │   │   ├── model/          # Entity classes
 │   │   ├── dto/            # Data transfer objects
 │   │   ├── exception/      # Custom exceptions
 │   │   └── AppApplication.java
 │   └── resources/
 │       ├── application.properties    # Configuration
 │       ├── schema.sql              # Database schema
 │       └── data.sql                # Sample data
 └── test/
     └── java/com/basic/app/
         └── controller/              # Integration tests
         └── service/                 # Unit tests
```

---

## API Endpoints

### Get Customer Reward Points

```
GET /rewards/{customerId}
```

#### Path Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| customerId | Long | Unique identifier of the customer |

#### Responses
| Status Code | Description | Example |
|-------------|-------------|---------|
| 200 | Success | See sample response below |
| 400 | Invalid customer ID | Bad request with error message |
| 404 | Customer or transactions not found | Not found with error message |

#### Sample Request
```
GET http://localhost:8080/rewards/1
```

#### Sample Response (200 OK)
```json
{
  "customerId": 1,
  "monthlyRewards": {
    "2026-03": 90,
    "2026-04": 30,
    "2026-05": 110
  },
  "totalRewards": 230
}
```

#### Sample Error Response (404 Not Found)
```json
{
  "error": "Customer not found with id: 999"
}
```

---

## Business Logic

### Reward Calculation Algorithm

```java
if (amount <= 50) return 0;
if (amount <= 100) return (int) Math.floor(amount - 50);
return (int) Math.floor((amount - 100) * 2 + 50);
```

### Time Window Logic

* **3-month window** calculated from the **most recent transaction date** (not current date)
* Transactions older than 3 months are **excluded**
* Only transactions with **valid dates** and **non-negative amounts** are processed

### Data Processing Flow

1. Validate customer ID
2. Fetch customer and transactions from database
3. Filter valid transactions (non-null dates, non-negative amounts)
4. Calculate 3-month window from latest transaction
5. Filter transactions within time window
6. Calculate points per transaction
7. Group by month and sum points
8. Return response with monthly breakdown and total

---

## Database Schema

### Customers Table
```sql
CREATE TABLE customers (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);
```

### Transactions Table
```sql
CREATE TABLE transactions (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT,
    amount DECIMAL(10, 2),
    transaction_date DATE,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);
```

---

## Error Handling

| Scenario | HTTP Status | Handler |
|----------|-------------|---------|
| Invalid customer ID (non-numeric) | 400 Bad Request | `MethodArgumentTypeMismatchException` |
| Invalid customer ID (negative/zero) | 400 Bad Request | `IllegalArgumentException` |
| Customer not found | 404 Not Found | `ResourceNotFoundException` |
| No transactions for customer | 404 Not Found | `ResourceNotFoundException` |
| No transactions in last 3 months | 404 Not Found | `ResourceNotFoundException` |

---

## Logging Strategy

* **INFO**: Request/response tracking, major business operations
* **WARN**: Expected business exceptions (customer not found, no transactions)
* **ERROR**: Unexpected errors, invalid inputs
* **DEBUG**: Detailed transaction processing, filtering operations
* **TRACE**: Individual point calculations

---

## Testing Strategy

### Unit Tests
- **Service Layer**: Full coverage of reward calculation logic
- **Edge Cases**: Zero amounts, negative amounts, boundary values
- **Exception Scenarios**: All expected exception paths

### Integration Tests
- **Controller Layer**: API endpoint testing with MockMvc
- **Response Validation**: Status codes, content types, JSON structure
- **Error Scenarios**: All error response paths
- **Parameter Validation**: Invalid inputs, type mismatches

### Test Coverage
-  Happy path scenarios
-  Business logic validation
-  Error handling verification
-  Input validation testing

---

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### 1. Clone Repository
```bash
git clone https://github.com/GodzIllababy911/reward-app.git
cd app
```

### 2. Configure Database
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rewards_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. Initialize Database
The application will automatically:
- Create database tables from `schema.sql`
- Populate sample data from `data.sql`

### 4. Build Project
```bash
mvn clean install
```

### 5. Run Application
```bash
mvn spring-boot:run
```

### 6. Verify Installation
```bash
curl http://localhost:8080/rewards/1
```

---

## Running Tests

### Execute All Tests
```bash
mvn test
```

### Execute Specific Test Class
```bash
mvn test -Dtest=RewardControllerTest
```

### Generate Test Reports
```bash
mvn surefire-report:report
```

---

## Performance Considerations

* **Efficient Filtering**: Stream API used for transaction filtering
* **Memory Management**: Lazy loading of transactions
* **Database Optimization**: Indexed foreign keys
* **Logging Levels**: Appropriate log levels to minimize overhead

---

## Security Considerations

* **Input Validation**: All inputs validated at API level
* **SQL Injection Prevention**: Using Spring Data JPA (parameterized queries)
* **Access Control**: Basic authentication can be added if needed
* **Data Sanitization**: Invalid transactions filtered out

---

## Best Practices Implemented

*  **Clean Architecture**: Separation of concerns (Controller → Service → Repository)
*  **Dependency Injection**: Proper Spring DI usage
*  **Exception Handling**: Centralized exception handling
*  **Logging**: Comprehensive logging strategy
*  **Testing**: Unit and integration tests
*  **Documentation**: Complete JavaDocs and README
*  **Code Quality**: Proper naming conventions, no wildcards
*  **Configuration Management**: Externalized configuration

---

## API Documentation Generation

Generate JavaDoc documentation:
```bash
mvn javadoc:javadoc
```
Documentation will be created in `target/site/apidocs/`

---

## Troubleshooting

### Common Issues

1. **Database Connection**: Ensure MySQL is running and credentials are correct
2. **Port Conflicts**: Default port is 8080, change in `application.properties` if needed
3. **Table Creation**: If tables already exist, change `ddl-auto` to `update`

### Debugging Tips

* Enable debug logging: Add `logging.level.com.basic.app=DEBUG` to `application.properties`
* Check application logs in console output
* Verify database connectivity with sample queries

---

## Authors

**Akshat Mishra**

# User Management Application

Spring Boot 2.7 Java 17 application demonstrating user credential management with comprehensive unit test coverage via JaCoCo.

## Project Structure

```
user-app/
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── UserApplication.java (Main Spring Boot class)
│   │   │   ├── controller/
│   │   │   │   └── UserController.java (REST API endpoints)
│   │   │   ├── service/
│   │   │   │   └── UserService.java (Business logic)
│   │   │   ├── entity/
│   │   │   │   └── User.java (JPA Entity)
│   │   │   └── repository/
│   │   │       └── UserRepository.java (Data access layer)
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/
│           ├── controller/
│           │   └── UserControllerTest.java
│           ├── service/
│           │   └── UserServiceTest.java
│           └── repository/
│               └── UserRepositoryTest.java
├── .github/
│   └── workflows/
│       └── test.yml (GitHub Actions CI/CD)
├── pom.xml
└── README.md
```

## Features

- **REST API** for user management (CREATE, READ, UPDATE, DELETE)
- **H2 In-Memory Database** for testing
- **JPA/Hibernate** ORM
- **Input Validation** (name, age constraints)
- **Comprehensive Unit Tests** (80%+ code coverage)
- **JaCoCo Code Coverage** enforcement
- **GitHub Actions** CI/CD pipeline

## User Entity

- **id**: Auto-generated Long primary key
- **name**: String (required, non-empty)
- **age**: Integer (required, 0-150 range)
- **email**: String (optional)
- **phone**: String (optional)

## API Endpoints

### Create User
```
POST /api/users
Content-Type: application/json

{
  "name": "John Doe",
  "age": 30,
  "email": "john@example.com",
  "phone": "1234567890"
}
```

### Get User by ID
```
GET /api/users/{id}
```

### Get All Users
```
GET /api/users
```

### Search by Name
```
GET /api/users/search/name/{name}
```

### Search by Age
```
GET /api/users/search/age/{age}
```

### Update User
```
PUT /api/users/{id}
Content-Type: application/json

{
  "name": "Jane Doe",
  "age": 28
}
```

### Delete User
```
DELETE /api/users/{id}
```

## Build & Run

### Prerequisites
- Java 17+
- Maven 3.6+

### Build
```bash
mvn clean package
```

### Run Tests
```bash
mvn clean test
```

### Generate JaCoCo Report
```bash
mvn clean test jacoco:report
```
Coverage report: `target/site/jacoco/index.html`

### Run Application
```bash
mvn spring-boot:run
```
Application runs on `http://localhost:8080`

## Test Coverage

Target coverage: **80%**

Test classes:
- **UserControllerTest** (14 tests): REST endpoint integration tests
- **UserServiceTest** (26 tests): Business logic unit tests
- **UserRepositoryTest** (18 tests): Data persistence tests

Total: **58+ test cases** covering:
- Happy path scenarios
- Invalid input validation
- Edge cases
- Error conditions
- Database operations

## JaCoCo Configuration

JaCoCo enforces minimum 80% line coverage on all packages (excludes test classes):

```xml
<limits>
  <limit>
    <counter>LINE</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.80</minimum>
  </limit>
</limits>
```

## GitHub Actions

Automated testing pipeline:
- Checkout code
- Setup JDK 17
- Build and test with Maven
- Generate JaCoCo coverage
- Upload to Codecov (optional)

Triggers on: Push to main/develop, Pull requests

## Dependencies

- Spring Boot 2.7.14 (LTS)
- Spring Data JPA
- H2 Database
- Lombok
- JUnit 5 (Jupiter)
- Spring Boot Test
- JaCoCo 0.8.8

## Properties Configuration

Database connection (H2 in-memory):
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

H2 Console: `http://localhost:8080/h2-console`

## Notes

- All tests use Spring Boot Test context with @SpringBootTest
- Database resets between test runs
- No external dependencies required
- Ready for Docker containerization
- Follow Maven standard directory layout
# sample-maven-application

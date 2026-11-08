# Student Management API

A production-style RESTful Student Management API built with **Spring Boot 4**, **Spring Data JPA** and **MySQL**.
Includes DTOs, request validation, global exception handling, soft delete, pagination, Swagger UI and unit tests.

## Tech Stack
- Java 21
- Spring Boot 4.1 (Spring Web MVC, Spring Data JPA, Bean Validation)
- MySQL
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito
- Maven

## Project Structure
```
src/main/java/in/strikes/studentmanagement
├── controller   -> REST endpoints
├── service      -> business logic
├── repository   -> Spring Data JPA repository
├── entity       -> JPA entity
├── dto          -> request / response objects
├── mapper       -> entity <-> DTO conversion
└── exception    -> custom exceptions + global handler
```

## Getting Started

### 1. Prerequisites
- JDK 21
- MySQL running on `localhost:3306`

### 2. Configure database
The database `student_management_db` is created automatically. Set your credentials using environment variables:

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
# optional: export DB_URL=jdbc:mysql://localhost:3306/student_management_db
```

Or create `src/main/resources/application-local.properties` (already gitignored):

```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Run
```bash
./mvnw spring-boot:run
```
App starts at `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### 4. Run tests
```bash
./mvnw test
```

## API Endpoints

| Method | Endpoint                | Description                         | Success |
|--------|-------------------------|-------------------------------------|---------|
| POST   | `/api/students`         | Create a student                    | 201     |
| GET    | `/api/students/{id}`    | Get a student by id                 | 200     |
| GET    | `/api/students`         | List students (paginated + sorting) | 200     |
| PUT    | `/api/students/{id}`    | Update a student                    | 200     |
| DELETE | `/api/students/{id}`    | Soft delete a student               | 204     |

Error codes: `400` validation error, `404` not found, `409` duplicate email / roll number.

### Sample request
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Kavir",
    "age": 22,
    "email": "kavir@example.com",
    "rollNo": 101,
    "subject": "Computer Science"
  }'
```

### Pagination
```
GET /api/students?page=0&size=10&sort=name,asc
```

### Sample validation error
```json
{
  "timestamp": "2026-11-07T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "validationErrors": {
    "email": "Email format is invalid",
    "age": "Age must be at least 1"
  }
}
```

## Features
- Layered architecture (Controller -> Service -> Repository)
- DTOs so internal fields (like `deleted`) are never exposed
- Bean Validation on request bodies
- Centralised error handling with `@RestControllerAdvice`
- Soft delete (records stay in DB, hidden from API)
- Unique email and roll number checks
- Pagination and sorting
- Swagger / OpenAPI documentation
- Unit tests for the service layer (Mockito)

## Author
**Kavir**
- GitHub: [github.com/Kaviir](https://github.com/Kaviir)
- LinkedIn: [linkedin.com/in/kavir-b391a02a6](https://linkedin.com/in/kavir-b391a02a6)

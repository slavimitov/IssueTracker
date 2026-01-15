# Issue Tracker (Mini Jira)

Spring Boot REST API for issue tracking.

## Tech Stack
- Spring Boot 3.4.x
- PostgreSQL
- Flyway (migrations)
- MapStruct (DTO mapping)
- Swagger/OpenAPI

## Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE issuetracker_dev;
```

2. Run the application:
```bash
./mvnw spring-boot:run
```

3. Access Swagger UI: http://localhost:8080/swagger-ui.html

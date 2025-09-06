# Gateway Service (Spring Cloud Gateway)

This module represents the **API Gateway** for the Gym CRM Platform project.  
It acts as a single entry point for all client requests and routes them to the appropriate microservices registered in *
*Eureka**.

## Purpose

- Provides a **unified entry point** for all requests (`http://localhost:8080`).
- Routes requests to:
    - **Core Service** → `lb://gym-crm-core`
    - **Workload Service** → `lb://trainers-workload-service`
- Hides internal service ports (8081, 8082, etc.) from external clients.
- Simplifies communication between clients and microservices.

## How to Run

Start the service as a regular Spring Boot application:

```bash
    mvn spring-boot:run
```

Or build the JAR and run it:

```bash
    mvn clean package
    java -jar target/gateway-service-0.0.1-SNAPSHOT.jar
```

## 🖥️ Routing Examples

- Base URL: `http://localhost:8080/**`
- Core Service: `http://localhost:8080/api/core/v1/login**` → `http://localhost:8081**`
- Workload Service: `http://localhost:8080/api/v1/trainers-workload**` → `http://localhost:8082/**`
- Eureka Dashboard: `http://localhost:8761`
- Get all members: `GET http://localhost:8080/api/members`
- Get member by ID: `GET http://localhost:8080/api/members/{id}`
- Create a new member: `POST http://localhost:8080/api/members`
- Update member: `PUT http://localhost:8080/api/members/{id}`
- Delete member: `DELETE http://localhost:8080/api/members/{id}`
- (and similar endpoints for trainers, classes, etc.)
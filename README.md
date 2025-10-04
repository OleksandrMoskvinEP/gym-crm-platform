Gym CRM Platform

## Prerequisites

To run this application, you should have the following installed:

- **Java Development Kit (JDK)** -- Oracle Open JDK 17.0.12
- **Maven** -- Apache Maven 3.9.9
- **Git** -- 2.49.0.windows.1
- **Docker** + **Docker Compose** — to run PostgreSQL, Prometheus, Grafana, and the application in containers

## Setup Instructions

Run the following script to create the database and add a user:

```sql
CREATE
DATABASE "gym_crm_db";
CREATE
USER gym WITH PASSWORD 'gym';
GRANT ALL PRIVILEGES ON DATABASE
"gym" TO gym;
```

### ⚙️ Running Gym CRM Platform with ActiveMQ
The Gym CRM Platform consists of several microservices that work together to provide
a comprehensive solution for managing gym operations.

#### Services
- **core-service** – main business logic (trainings, users)
- **workload-service** – trainer workload aggregation
- **discovery-service** – Eureka server
- **gateway-service** – API Gateway
- **ActiveMQ** – message broker (Docker)
- **Zipkin** *(optional)* – distributed tracing

---

##  Required Environment Variables

Before running any service you must set up the following variables:

### Database (Postgres)
- `DB_URL` – JDBC connection string
    - Local: `jdbc:postgresql://localhost:5432/gymcrm`
    - Prod (docker-compose): `jdbc:postgresql://postgres:5432/gymcrm`
- `DB_USERNAME` – database username
    - default: `postgres`
- `DB_PASSWORD` – database password
    - default: `postgres`

### ActiveMQ
- `ACTIVEMQ_URL` – broker connection URL
    - Local: `tcp://localhost:61616`
    - Prod (docker-compose): `tcp://activemq:61616`
- `ACTIVEMQ_USER` – broker username (default: `admin`)
- `ACTIVEMQ_PASSWORD` – broker password (default: `admin`)

---

## ▶️ Running Locally

Set the required environment variables in your terminal:
**Windows (PowerShell)**
```bash
   $env:DB_URL="jdbc:postgresql://localhost:5432/gymcrm"
   $env:DB_USERNAME="postgres"
   $env:DB_PASSWORD="postgres"

   $env:ACTIVEMQ_URL="tcp://localhost:61616"
   $env:ACTIVEMQ_USER="admin"
   $env:ACTIVEMQ_PASSWORD="admin"
````
### Start the services in order:

discovery-service

gateway-service

workload-service (profile: local)

core-service (profile: local)

### Access the services:
Management UI: http://localhost:8161

Credentials: admin/admin

Start services in the following order:

discovery-service → http://localhost:8761

gateway-service → http://localhost:8080

workload-service (with local profile)

core-service (with local profile)


## 📚 API Documentation

Interactive and downloadable documentation for the Gym CRM REST API.

### Swagger UI

Use Swagger UI to explore and test the available API endpoints.

- [Swagger UI](http://localhost:8080/swagger-ui/index.html)
- [OpenAPI JSON](http://localhost:8080/v3/api-docs)
- [OpenAPI YAML](http://localhost:8080/v3/api-docs.yaml)

> These URLs assume the application is running locally on port 8080.

---

### OpenAPI Specification (YAML)

This YAML file defines the full REST API contract and is used for client/server code generation.

- [OpenAPI YAML File](gym-crm-core/src/main/resources/openapi/gym.yaml)

> Used with `openapi-generator-maven-plugin` to generate DTOs and API interfaces.

---

### Postman Collection

You can also test the API using Postman.

- [Download Postman Collection](src/main/resources/postman/gym-crm-api.collection.json)

> To import the collection into Postman, open Postman, click `Import`, then select the `.json` file above.
>
---
> ## 📊 Health Monitoring (Spring Boot Actuator)

The application exposes a health endpoint powered by Spring Boot Actuator:

- [🔗Health check you can see here](http://localhost:8080/actuator/health)

### 📋 What it shows:

The `/actuator/health` endpoint provides aggregated status of critical system components:

| Component            | Description                                              |
|----------------------|----------------------------------------------------------|
| `db` / `dataBase`    | Database connection check (e.g. PostgreSQL or H2)        |
| `diskSpace`          | Built-in disk space indicator from Spring Boot           |
| `memory`             | 🛠 Custom indicator for free JVM memory (with threshold) |
| `diskSpaceIndicator` | 🛠 Custom indicator for free disk space (with threshold) |
| `ping`               | Basic always-up check (for liveness probes)              |

### Prometheus

Prometheus collects application metrics via the Spring Boot Actuator endpoint `/actuator/prometheus`.

- **Prometheus UI**: [http://localhost:9090](http://localhost:9090)
- **Application Metrics Endpoint
  **: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)

### Grafana

Grafana is used to visualize metrics collected by Prometheus.

- **Grafana UI**: [http://localhost:3000](http://localhost:3000)  
  Default credentials: `admin` / `admin`
- Dashboard **GYM_CRM** displays runtime statistics such as login success/failure counts, DB errors, request rates, and
  more.

---
> ## Monitoring & Containerized Environment

This project includes full containerized monitoring and infrastructure:

- Spring Boot application
- PostgreSQL database
- Prometheus metrics collection
- Grafana dashboards

All services are launched together using:

```bash
   docker compose up -d
```

## Messaging (ActiveMQ)
The application uses ActiveMQ for messaging. You can access the ActiveMQ web console at:

- **ActiveMQ Web Console**: [http://localhost:8161/admin](http://localhost:8161/admin)  
  Default credentials: `admin` / `admin`
- **Broker URL**: `tcp://localhost:61616`

### To srart ActiveMQ using Docker Compose, run:
```bash
   docker compose -f docker-compose-activemq.yml up -d
```
---

## Running tests

This project separates unit, component, and integration tests. Component and integration tests are implemented using Cucumber and executed via the Failsafe plugin as `*IT.java`.

- Unit tests (Surefire):

```bash
  mvn test
```

- Component tests for core module (Failsafe + Cucumber, tag `@gca-core`):

```bash
   mvn verify -Dcucumber.filter.tags="@gca-core"
```

- Component tests for workload module (Failsafe + Cucumber, tag `@gca-workload`):

```bash
  mvn verify -Dcucumber.filter.tags="@gca-workload"
```

- Cross-module integration tests in `gym-crm-integration-tests` (Failsafe + Cucumber, tag `@integration`):

```bash
  mvn verify -Dcucumber.filter.tags="@integration"
```
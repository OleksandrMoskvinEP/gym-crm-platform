# Discovery Service (Eureka Server)

This module represents the **Eureka Discovery Server** used in the Gym CRM Platform project.  
Eureka provides service registration and discovery, allowing microservices to communicate with each other without hardcoding hostnames or ports.

  Purpose
- Acts as a central **service registry**.
- Each microservice (Core, Workload, Gateway, etc.) registers itself here on startup.
- Eureka periodically checks the availability of services and updates their status (UP/DOWN).
- Gateway uses Eureka to route requests to the available services.

  How to Run
Start the service as a regular Spring Boot application:

```bash
    mvn spring-boot:run
```

### By default, the server runs on port 8761.

Once started, the Eureka dashboard is available at:
 http://localhost:8761

🖥️ Web UI

Through the dashboard you can:

view the list of all registered services,

monitor their status (UP/DOWN),

check the last heartbeat timestamp.
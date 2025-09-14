# 14. Microservices Patterns

## What are Microservices Patterns?
Microservices patterns are proven solutions to common problems in distributed systems. They help you design, build, and operate microservices architectures that are scalable, resilient, and maintainable.

---

## Key Patterns (Detailed Explanations)

### 1. API Gateway
- **Definition:** A single entry point for all client requests to your microservices. It handles routing, authentication, rate limiting, and protocol translation.
- **Why use it?**
    - Simplifies client communication (one endpoint).
    - Centralizes cross-cutting concerns (security, logging, throttling).
    - Can aggregate responses from multiple services.
- **Examples:** Netflix Zuul, Spring Cloud Gateway, Kong, NGINX.
- **Drawbacks:** Can become a bottleneck or single point of failure if not designed for high availability.

### 2. Service Discovery
- **Definition:** Mechanism for services to find and communicate with each other dynamically, without hardcoding network locations.
- **Types:**
    - **Client-side discovery:** Client queries the registry (e.g., Netflix Eureka, Consul).
    - **Server-side discovery:** Load balancer queries the registry and routes requests (e.g., Kubernetes Ingress).
- **Benefits:** Enables dynamic scaling, resilience, and easier deployments.

### 3. Circuit Breaker
- **Definition:** Prevents a service from repeatedly trying to execute an operation that's likely to fail, allowing it to recover gracefully.
- **How it works:**
    - Monitors calls to a remote service.
    - Opens the circuit if failures exceed a threshold, short-circuiting further calls.
    - After a timeout, allows some calls to test if the service has recovered.
- **Examples:** Hystrix, Resilience4j, Sentinel.
- **Benefits:** Prevents cascading failures, improves system stability.

### 4. Bulkhead
- **Definition:** Isolates resources for different parts of the system, so a failure in one does not bring down others (like watertight compartments in a ship).
- **How to implement:** Use separate thread pools or connection pools for different services or features.
- **Benefits:** Limits the impact of failures and resource exhaustion.

### 5. Saga Pattern
- **Definition:** Manages distributed transactions across multiple microservices using a sequence of local transactions and compensating actions.
- **Types:**
    - **Orchestration:** A central coordinator tells each service what to do next.
    - **Choreography:** Services react to events from other services.
- **Use case:** Order processing, payment, inventory management.
- **Drawbacks:** More complex error handling and compensation logic.

### 6. CQRS (Command Query Responsibility Segregation)
- **Definition:** Separates read and write operations into different models, improving scalability and performance.
- **Benefits:**
    - Optimizes queries and commands independently.
    - Enables event sourcing and audit trails.
- **Drawbacks:** Increased complexity, eventual consistency.

### 7. Event Sourcing
- **Definition:** Persist state changes as a sequence of events, rather than just storing the current state.
- **Benefits:**
    - Full audit log of all changes.
    - Enables rebuilding state and temporal queries.
- **Drawbacks:** Event schema evolution, replay complexity.

---

## API Gateway Example
- Handles authentication, routing, and aggregation.
- Example: Netflix Zuul, Spring Cloud Gateway.
- **Sample Spring Cloud Gateway Route:**
    ```yaml
    spring:
      cloud:
        gateway:
          routes:
          - id: user-service
            uri: http://user-service:8080
            predicates:
            - Path=/users/**
    ```

---

## Circuit Breaker Example (Resilience4j)
```java
@CircuitBreaker(name = "myService", fallbackMethod = "fallback")
public String callRemoteService() {
    // call remote service
}
public String fallback(Throwable t) {
    return "Fallback response";
}
```

---

## Saga Pattern Example
- **Orchestration:**
    - Central coordinator (e.g., OrderService) calls each participant and handles failures.
    - Example: Camunda, Axon Framework.
- **Choreography:**
    - Services emit and listen to events (e.g., OrderCreated → PaymentRequested → InventoryReserved).
    - Example: Kafka, RabbitMQ for event bus.

---

## Bulkhead Example
- Use separate thread pools for different service calls:
    ```java
    @Bulkhead(name = "inventoryService", type = Bulkhead.Type.THREADPOOL)
    public String callInventory() {
        // ...
    }
    ```

---

## CQRS Example
- Separate write and read models:
    - Write: `OrderCommandService` handles commands (create, update, delete).
    - Read: `OrderQueryService` handles queries (get by id, list orders).

---

## Event Sourcing Example
- Store events instead of current state:
    ```java
    public class OrderCreatedEvent {
        private String orderId;
        private List<String> items;
        // ...
    }
    // Save event to event store, replay to rebuild state
    ```

---

## Best Practices
- Design for failure (timeouts, retries, fallbacks).
- Use centralized configuration and logging.
- Secure inter-service communication (OAuth2, mTLS).
- Automate deployment and scaling (CI/CD, containers, Kubernetes).
- Monitor and trace requests (distributed tracing, e.g., Zipkin, Jaeger).
- Prefer asynchronous communication for decoupling and resilience.
- Document APIs and events clearly.

---

## Common Interview Questions (with Sample Answers)
- **What is the API Gateway pattern and why is it used?**
    - It provides a single entry point for clients, centralizes cross-cutting concerns, and simplifies client interactions with microservices.
- **How do you handle distributed transactions in microservices?**
    - Use the Saga pattern (orchestration or choreography) to manage distributed transactions with compensating actions instead of 2PC.
- **What is the difference between orchestration and choreography in Sagas?**
    - Orchestration uses a central coordinator; choreography relies on services reacting to events.
- **How do you secure communication between microservices?**
    - Use mTLS, OAuth2, API gateways, and network policies.
- **What is eventual consistency?**
    - Data may not be immediately consistent across services, but will become consistent over time (common in distributed systems).
- **How do you implement service discovery?**
    - Use a registry (Eureka, Consul, Kubernetes) where services register and discover each other dynamically.

---

## Further Reading & Tools
- [Microservices.io Patterns](https://microservices.io/patterns/index.html)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Axon Framework](https://axoniq.io/)
- [Camunda BPM](https://camunda.com/)
- [Eventuate](https://eventuate.io/)

---

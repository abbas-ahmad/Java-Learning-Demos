# 10. Spring Cloud

Spring Cloud provides a suite of tools for building robust, scalable microservices and distributed systems. It addresses common challenges such as service discovery, configuration management, load balancing, fault tolerance, and inter-service communication.

## Key Components & Concepts

### 1. Eureka (Service Discovery)
- **Purpose**: Allows microservices to register themselves and discover other services without hardcoding hostnames.
- **How it works**: Services register with Eureka Server. Clients query Eureka to find service instances.
- **Example**:
    ```java
    @EnableEurekaServer // On the registry server application
    @SpringBootApplication
    public class EurekaServerApp {
        public static void main(String[] args) {
            SpringApplication.run(EurekaServerApp.class, args);
        }
    }
    ```
    ```java
    @EnableEurekaClient // On a client microservice
    @SpringBootApplication
    public class MyServiceApp { ... }
    ```

### 2. Ribbon (Client-Side Load Balancing)
- **Purpose**: Distributes requests across multiple service instances.
- **How it works**: Ribbon intercepts REST calls and chooses a service instance from Eureka.
- **Example**:
    ```java
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    ```
    Now, `RestTemplate` will automatically load-balance requests to services registered in Eureka.

### 3. Feign (Declarative REST Client)
- **Purpose**: Simplifies HTTP API clients by using interfaces and annotations.
- **How it works**: Define an interface, annotate it, and Feign generates the implementation.
- **Example**:
    ```java
    @FeignClient(name = "user-service")
    public interface UserClient {
        @GetMapping("/users/{id}")
        User getUserById(@PathVariable("id") Long id);
    }
    ```

### 4. Hystrix (Circuit Breaker)
- **Purpose**: Prevents cascading failures by stopping calls to a failing service.
- **How it works**: Wraps method calls and opens the circuit if failures exceed a threshold.
- **Example**:
    ```java
    @HystrixCommand(fallbackMethod = "fallbackUser")
    public User getUser(Long id) {
        // call to remote service
    }
    public User fallbackUser(Long id) {
        return new User(); // return default
    }
    ```

### 5. Config Server (Centralized Configuration)
- **Purpose**: Externalizes configuration for all environments and services.
- **How it works**: Stores config in a Git repo or file system, serves it via HTTP.
- **Example**:
    ```java
    @EnableConfigServer
    @SpringBootApplication
    public class ConfigServerApp { ... }
    ```
    - Client microservices use `@EnableConfigClient` and `bootstrap.properties` to fetch config.

### 6. Spring Cloud Bus
- **Purpose**: Propagates configuration changes and events across distributed services.
- **How it works**: Uses a message broker (like RabbitMQ or Kafka) to broadcast events.
- **Example**: When a config changes, a `/actuator/bus-refresh` endpoint can trigger all services to reload config.

### 7. PCF (Pivotal Cloud Foundry)
- **Purpose**: Platform-as-a-Service (PaaS) for deploying Spring Cloud apps.
- **Integration**: Spring Cloud provides connectors and auto-configuration for PCF services.

## Common Annotations
- `@EnableEurekaServer` – Starts a Eureka registry server.
- `@EnableEurekaClient` – Registers a service with Eureka.
- `@EnableFeignClients` – Enables Feign clients.
- `@EnableHystrix` – Enables Hystrix circuit breaker.
- `@EnableConfigServer` – Starts a config server.

## Typical Microservice Architecture with Spring Cloud
```
[Client] -> [API Gateway] -> [Microservices] <-> [Eureka] <-> [Config Server]
                                      |         |           |
                                   [Ribbon]  [Feign]    [Hystrix]
```

---

## Alternative Service Discovery and Load Balancing: Azure APIM & Kubernetes Ingress

In modern cloud-native architectures, organizations may use managed solutions like Azure API Management (APIM) and Kubernetes Ingress instead of Eureka and Ribbon for service discovery and load balancing.

### Azure API Management (APIM)
- **Purpose**: Acts as a secure, scalable API gateway for exposing internal microservices to external consumers.
- **Key Features**:
  - Centralized API gateway for all external traffic
  - Security (OAuth2, JWT, IP filtering, rate limiting)
  - Analytics, monitoring, and developer portal
  - Request/response transformation and versioning
- **How it works**: APIM receives external requests, applies policies (security, throttling, etc.), and forwards them to backend Spring Boot microservices.
- **Typical Use Case**: Exposing REST APIs to partners, mobile apps, or third parties securely.
- **Configuration Example** (APIM policy snippet for JWT validation):
    ```xml
    <inbound>
        <validate-jwt header-name="Authorization" failed-validation-httpcode="401" failed-validation-error-message="Unauthorized">
            <openid-config url="https://login.microsoftonline.com/{tenant}/.well-known/openid-configuration" />
            <required-claims>
                <claim name="aud">api://your-api-id</claim>
            </required-claims>
        </validate-jwt>
        <!-- Other policies -->
    </inbound>
    ```
- **Spring Boot Integration**: No code changes required; APIM routes traffic to your service endpoints (e.g., `https://myapi.azure-api.net/myservice`).

### Kubernetes Ingress
- **Purpose**: Provides HTTP and HTTPS routing to services within a Kubernetes cluster, acting as an internal (and optionally external) load balancer.
- **Key Features**:
  - Path-based and host-based routing
  - SSL/TLS termination
  - Integration with cloud load balancers (e.g., Azure Application Gateway, NGINX)
  - Can expose services internally (cluster-only) or externally
- **How it works**: Ingress controllers watch Ingress resources and configure the underlying proxy (NGINX, Traefik, etc.) to route traffic to the correct service pods.
- **Typical Use Case**: Routing internal/external traffic to Spring Boot microservices running as Kubernetes Deployments.
- **Configuration Example** (Ingress YAML for a Spring Boot service):
    ```yaml
    apiVersion: networking.k8s.io/v1
    kind: Ingress
    metadata:
      name: my-springboot-ingress
      annotations:
        kubernetes.io/ingress.class: nginx
    spec:
      rules:
      - host: myapp.example.com
        http:
          paths:
          - path: /users
            pathType: Prefix
            backend:
              service:
                name: user-service
                port:
                  number: 8080
    ```
- **Spring Boot Integration**: Deploy your app as a Kubernetes Service; Ingress routes traffic to the service.

### Updated Microservice Architecture Example
```
[External Client] -> [Azure APIM] -> [Kubernetes Ingress] -> [Spring Boot Microservices]
```
- APIM handles external API management and security.
- Ingress manages internal routing and load balancing within the cluster.

### Interview Tips
- Be able to compare Eureka/Ribbon with APIM/Ingress and explain why organizations might choose managed/cloud-native solutions.
- Know how to secure and expose Spring Boot services using APIM and Ingress.
- Understand the separation of concerns: APIM for external API management, Ingress for internal/external routing.

---

# 1. Core Spring Concepts

## Important Notes

- **Spring is a modular, open-source Java framework for building enterprise applications.**
  - It provides infrastructure support for developing Java applications, focusing on flexibility, modularity, and testability.

- **Inversion of Control (IoC):**
  - The framework manages object creation and wiring, not the application code.
  - Example:
    ```java
    @Component
    public class UserService {
        // ...
    }
    ```
    Spring creates and manages the `UserService` bean.

- **Dependency Injection (DI):**
  - Main way to achieve IoC in Spring. Supports constructor, setter, and field injection.
  - Example (constructor injection):
    ```java
    @Component
    public class OrderService {
        private final UserService userService;
        public OrderService(UserService userService) {
            this.userService = userService;
        }
    }
    ```

- **Spring Beans:**
  - Objects managed by the Spring container, defined via annotations or XML.
  - Example (Java config):
    ```java
    @Configuration
    public class AppConfig {
        @Bean
        public UserService userService() {
            return new UserService();
        }
    }
    ```

- **Component scanning and stereotype annotations:**
  - Automate bean registration using annotations like `@Component`, `@Service`, `@Repository`, `@Controller`.
  - Example:
    ```java
    @Service
    public class PaymentService {}
    @Repository
    public class UserRepository {}
    @Controller
    public class UserController {}
    ```

- **Bean scopes:**
  - Control the lifecycle and visibility of beans: singleton (default), prototype, request, session, application.
  - Example:
    ```java
    @Component
    @Scope("prototype")
    public class PrototypeBean {}
    ```

- **Proxies:**
  - Used for AOP, transactions, and security, using JDK dynamic proxies or CGLIB.
  - Example (AOP):
    ```java
    @Aspect
    public class LoggingAspect {
        @Before("execution(* com.example.service.*.*(..))")
        public void logBefore() {
            System.out.println("Method called");
        }
    }
    ```

- **Configuration:**
  - Can be done via Java classes, XML, or annotations.
  - Example (Java config):
    ```java
    @Configuration
    public class AppConfig {}
    ```

- **Profiles:**
  - Allow environment-specific bean definitions and properties.
  - Example:
    ```java
    @Service
    @Profile("dev")
    public class DevEmailService implements EmailService {}
    ```

- **@Value and SpEL:**
  - Use `@Value` for dynamic value injection, including property values and SpEL expressions.
  - Example:
    ```java
    @Value("${app.name}")
    private String appName;
    @Value("#{2 * 10}")
    private int calculatedValue;
    ```

- **Understanding these concepts is essential for Java backend interviews and real-world Spring development.**

## Core Concepts

- **Inversion of Control (IoC)**: A design principle where the control of object creation and management is transferred from the application code to the Spring framework.
- **Dependency Injection (DI)**: A specific implementation of IoC, where dependencies are provided to a class rather than the class creating them itself.
- **Spring Beans**: Objects that are instantiated, assembled, and otherwise managed by the Spring IoC container.
- **Component Scanning**: The process by which Spring discovers and registers beans in the application context, based on the specified base packages.
- **Stereotype Annotations**: Annotations that indicate the role of a Spring-managed component (`@Component`, `@Service`, `@Repository`, `@Controller`).
- **Bean Scopes**: Define the lifecycle and visibility of beans in the Spring container (e.g., singleton, prototype).
- **Proxies**: Used in Spring to enable features like AOP, transactions, and security, by creating a surrogate or placeholder for an object.
- **Configuration**: The process of defining beans and their relationships in the Spring container, using XML, Java annotations, or Java configuration classes.
- **Profiles**: Allow the segregation of parts of your application configuration and make it possible to activate them selectively in different environments.
- **SpEL (Spring Expression Language)**: A powerful expression language that supports querying and manipulation of objects at runtime.

## Most Important Interview Questions & Answers

### 1. What is Inversion of Control (IoC) and how is it implemented in Spring?
**Answer:**
Inversion of Control (IoC) is a design principle where the control of object creation and dependency management is transferred from the application code to the Spring framework. In Spring, IoC is implemented through Dependency Injection (DI), where the container injects dependencies into beans via constructor, setter, or field injection. This promotes loose coupling and easier testing.

### 2. What is Dependency Injection (DI)? What are its types in Spring?
**Answer:**
Dependency Injection is a technique where an object's dependencies are provided externally rather than the object creating them itself. Spring supports three types of DI:
- **Constructor Injection** (preferred for mandatory dependencies)
- **Setter Injection** (for optional dependencies)
- **Field Injection** (not recommended for testability)

### 3. What is a Spring Bean? How do you define and manage beans?
**Answer:**
A Spring Bean is an object managed by the Spring IoC container. Beans can be defined using annotations (`@Component`, `@Service`, `@Repository`, `@Controller`), Java configuration (`@Bean` methods in `@Configuration` classes), or XML configuration. The container manages their lifecycle, configuration, and dependencies.

### 4. What are stereotype annotations in Spring?
**Answer:**
Stereotype annotations indicate the role of a Spring-managed component and enable component scanning. Common ones include:
- `@Component`: Generic component
- `@Service`: Service layer
- `@Repository`: Data access layer (enables exception translation)
- `@Controller`: Web controller

### 5. What are bean scopes in Spring?
**Answer:**
Bean scopes define the lifecycle and visibility of beans:
- **Singleton** (default): One instance per container
- **Prototype**: New instance per request
- **Request, Session, Application**: Web-specific scopes

### 6. What are proxies in Spring and why are they used?
**Answer:**
Proxies are surrogate objects created by Spring to add cross-cutting concerns (like AOP, transactions, security) to beans. Spring uses JDK dynamic proxies for interfaces and CGLIB proxies for classes. Proxies allow Spring to intercept method calls and apply additional behavior.

### 7. How does component scanning work in Spring?
**Answer:**
Component scanning automatically detects and registers beans annotated with stereotype annotations in specified packages. This reduces manual bean registration and supports modular design.

### 8. What is the difference between Java-based, XML-based, and annotation-based configuration?
**Answer:**
- **Java-based**: Uses `@Configuration` classes and `@Bean` methods (type-safe, modern approach)
- **XML-based**: Uses XML files to define beans and dependencies (legacy, still supported)
- **Annotation-based**: Uses annotations directly on classes and fields (concise, widely used)

### 9. What are Spring Profiles and how do you use them?
**Answer:**
Profiles allow you to define beans and properties for different environments (dev, test, prod). Use `@Profile` annotation on beans and `application-{profile}.properties` for environment-specific configuration. Activate profiles via `spring.profiles.active` property.

### 10. What is SpEL (Spring Expression Language) and where is it used?
**Answer:**
SpEL is a powerful expression language for querying and manipulating objects at runtime. It is used in annotations like `@Value`, in configuration files, and for conditional bean registration. Example: `@Value("#{2 * 10}")` injects the value 20.

### 11. How does Spring manage the lifecycle of a bean?
**Answer:**
Spring manages bean lifecycle through instantiation, dependency injection, initialization (`@PostConstruct`, `afterPropertiesSet()`), usage, and destruction (`@PreDestroy`, `destroy()`). Custom init and destroy methods can also be defined.

### 12. What is the difference between BeanFactory and ApplicationContext?
**Answer:**
- **BeanFactory** is the basic container in Spring, providing fundamental DI support. It lazily initializes beans and is suitable for lightweight applications.
- **ApplicationContext** is a more advanced container, eagerly initializes beans by default, and provides additional features like internationalization, event propagation, and integration with Spring AOP.

### 13. How do you inject values from properties files into Spring beans?
**Answer:**
- Use the `@Value` annotation to inject values from properties files:
    ```java
    @Value("${app.name}")
    private String appName;
    ```
- For complex configuration, use `@ConfigurationProperties` to bind groups of properties to a POJO.

### 14. How do you handle circular dependencies in Spring?
**Answer:**
- Spring can resolve circular dependencies for singleton beans using setter injection or field injection, but not with constructor injection. If a circular dependency is detected with constructor injection, Spring throws a `BeanCurrentlyInCreationException`.
- To avoid circular dependencies, refactor your code to decouple beans or use setter injection where necessary.

### 15. How do you use environment-specific configuration in a real-world project?
**Answer:**
- Use Spring Profiles to separate configuration for different environments (dev, test, prod). For example, define `application-dev.properties` and `application-prod.properties` for different databases or API endpoints.
- Activate the desired profile using `spring.profiles.active` in environment variables or command-line arguments.
- Example:
    ```java
    @Service
    @Profile("prod")
    public class ProdEmailService implements EmailService {}
    ```

### 16. Real-World Use Case: How would you design a modular, testable service layer in a Spring application?
**Answer:**
- Use interfaces for service contracts and annotate implementations with `@Service`.
- Inject dependencies via constructor injection for immutability and testability.
- Use `@Transactional` at the service layer to manage transactions.
- Separate business logic from data access by using repositories (`@Repository`).
- Example:
    ```java
    public interface OrderService { ... }
    @Service
    public class OrderServiceImpl implements OrderService {
        private final OrderRepository orderRepository;
        public OrderServiceImpl(OrderRepository orderRepository) {
            this.orderRepository = orderRepository;
        }
        // business methods
    }
    ```

### 17. Real-World Use Case: How do you externalize configuration for a microservices application?
**Answer:**
- Use Spring Cloud Config Server to centralize configuration for all services.
- Store configuration in a Git repository and refresh properties at runtime using Spring Cloud Bus.
- Use `@Value` or `@ConfigurationProperties` to inject config values into beans.
- This approach allows dynamic updates and consistent configuration management across environments.

### 18. Real-World Use Case: How do you ensure loose coupling and easy testing in a large Spring project?
**Answer:**
- Rely on interfaces and dependency injection to decouple components.
- Use `@MockBean` or Mockito for mocking dependencies in tests.
- Avoid field injection; prefer constructor injection for easier unit testing.
- Use profiles and property files to swap implementations for different environments (e.g., mock email service in dev, real service in prod).

### 19. Real-World Use Case: How do you handle secrets and sensitive configuration in Spring?
**Answer:**
- Never hardcode secrets in code or properties files.
- Use environment variables, external vaults (e.g., HashiCorp Vault, AWS Secrets Manager), or encrypted property sources.
- Spring Cloud Vault and Spring Cloud Config support secure secret management and dynamic refresh.

### 20. Real-World Use Case: How do you structure a Spring project for maintainability and scalability?
**Answer:**
- Use a layered architecture: controller, service, repository, and domain layers.
- Group related classes by feature/module, not by technical type, for better modularity.
- Use configuration classes to organize bean definitions and externalize settings.
- Apply SOLID principles and keep classes focused on single responsibilities.

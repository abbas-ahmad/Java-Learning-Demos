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

# 📘 Java Notes – Grokking the Spring Boot Interview

## 1. Core Spring Concepts

Spring is a comprehensive framework for building Java applications, especially enterprise and web applications. Understanding its core concepts is essential for backend interviews.

### What is Spring Framework?
- **Spring Framework** is an open-source, lightweight framework for Java development.
- It provides infrastructure support for developing Java applications, focusing on flexibility, modularity, and testability.
- Key modules: Core Container, AOP, Data Access, Transaction Management, MVC, Security, and more.

### Inversion of Control (IoC)
- **IoC** is a design principle where the control of object creation and dependency management is transferred from the application code to the framework (Spring container).
- Promotes loose coupling and easier testing.
- Achieved in Spring via Dependency Injection.

### Dependency Injection (DI)
- **DI** is a technique where an object's dependencies are provided externally rather than the object creating them itself.
- Types of DI in Spring:
    - **Constructor Injection**: Dependencies are provided via constructor parameters. Ensures immutability and is preferred for mandatory dependencies.
    - **Setter Injection**: Dependencies are set via JavaBean setter methods. Useful for optional dependencies.
    - **Field Injection**: Dependencies are injected directly into fields (not recommended for testability).
- **Benefits**: Decouples components, improves testability, and simplifies configuration.

### Spring Beans
- **Bean**: An object managed by the Spring IoC container.
- Beans are defined via annotations (`@Component`, `@Service`, `@Repository`, `@Controller`) or XML configuration.
- The container is responsible for the lifecycle, configuration, and wiring of beans.

### Component Scanning & Stereotype Annotations
- **Component Scanning**: Spring automatically detects and registers beans annotated with stereotype annotations.
    - `@Component`: Generic stereotype for any Spring-managed component.
    - `@Service`: Specialization for service layer beans.
    - `@Repository`: Specialization for data access layer beans; enables exception translation.
    - `@Controller`: Specialization for web controllers in MVC.

### Bean Scopes
- **Singleton** (default): Single instance per Spring container.
- **Prototype**: New instance every time the bean is requested.
- **Request, Session, Application**: Web-specific scopes for per-request, per-session, or per-application beans.

### Proxies in Spring
- **Proxies** are used for features like AOP, transactions, and security.
    - **JDK Dynamic Proxy**: Used when the bean implements an interface.
    - **CGLIB Proxy**: Used when the bean does not implement an interface (creates a subclass at runtime).
- Proxies allow Spring to add cross-cutting concerns (e.g., logging, security) transparently.

### Configuration Metadata
- **Java-based Configuration**: Use `@Configuration` classes and `@Bean` methods to define beans.
- **XML-based Configuration**: Legacy approach, still supported.
- **Annotation-based Configuration**: Use annotations directly on classes.

### Profiles
- **Profiles** allow you to define beans for different environments (dev, test, prod).
- Use `@Profile` annotation and `application-{profile}.properties` files.

### Value Injection & SpEL
- Use `@Value` to inject values from properties files or environment variables.
- **Spring Expression Language (SpEL)** allows dynamic value injection and expression evaluation.

### Summary Table
| Concept                | Description                                                      |
|------------------------|------------------------------------------------------------------|
| IoC                    | Framework manages object creation and wiring                      |
| DI                     | Inject dependencies via constructor, setter, or field             |
| Bean                   | Object managed by Spring container                                |
| Component Scanning     | Auto-detects beans via annotations                               |
| Stereotype Annotations | @Component, @Service, @Repository, @Controller                   |
| Bean Scopes            | singleton, prototype, request, session, application               |
| Proxies                | Used for AOP, transactions, security (JDK/CGLIB)                 |
| Profiles               | Environment-specific beans and properties                         |
| @Value & SpEL          | Inject values and evaluate expressions in beans                   |

---

## 2. Spring Bean Lifecycle
- **Bean**: A managed object inside Spring container.
- **Scopes**:
    - `singleton` (default) – single instance per context.
    - `prototype` – new instance per request.
    - `request`, `session`, `application` – web-specific.
- **Lifecycle Stages**:
    1. Instantiate bean.
    2. Populate dependencies.
    3. Call `@PostConstruct` (if any).
    4. Run `afterPropertiesSet()` (if `InitializingBean`).
    5. Custom init method (if defined).
    6. Use bean.
    7. On shutdown → `@PreDestroy`, `destroy()` (if `DisposableBean`), custom destroy method.
- **Profiles**:
    - Define environment-specific beans using `@Profile("dev")`, `@Profile("test")`.
    - Properties loaded via `application-{profile}.properties`.
- **@Value and SpEL**: Inject literal values or evaluate expressions into beans.

---

## 3. Aspect-Oriented Programming (AOP)
- **Purpose**: Modularize cross-cutting concerns like logging, security, transactions.
- **Core Concepts**:
    - **Aspect**: Module containing cross-cutting concern.
    - **JoinPoint**: Point in execution (method call).
    - **Pointcut**: Expression to select join points.
    - **Advice**: Action at a join point (`@Before`, `@After`, `@Around`).
    - **Weaving**: Linking aspects with code (compile, load, runtime).
- **Advice Types**:
    - Before, AfterReturning, AfterThrowing, After, Around.
- **Limitations**:
    - Final methods cannot be advised.
    - JDK proxies need interfaces.

---

## 4. Spring MVC
- **Model-View-Controller**: Separation of concerns.
- **DispatcherServlet**: Front controller that routes requests to controllers.
- **Annotations**:
    - `@Controller`: Returns views.
    - `@RestController`: Returns JSON/XML (implies `@ResponseBody`).
    - `@RequestMapping`, `@GetMapping`, `@PostMapping`: Map requests.
    - `@RequestParam`: Extract query parameters.
    - `@PathVariable`: Extract values from URL path.
- **ViewResolvers**: Map logical view names to actual implementations (e.g., Thymeleaf, JSP).
- **Model**: Passes data from controller to view.

---

## 5. REST with Spring
- **REST**: Representational State Transfer, stateless communication using HTTP.
- **HTTP Methods**: GET (read), POST (create), PUT (update), DELETE (remove).
- **Idempotency**: GET, PUT, DELETE are idempotent; POST is not.
- **Annotations**:
    - `@RestController`, `@ResponseBody`, `@PathVariable`, `@RequestBody`.
- **Error Handling**: `@ExceptionHandler`, `@ControllerAdvice`.
- **Content Negotiation**: Return JSON/XML based on `Accept` header.
- **Security**: TLS (HTTPS), authentication, authorization.

---

## 6. Spring Boot Basics
- **Why Spring Boot?** Eliminates boilerplate configuration, provides embedded server, and production-ready features.
- **Key Features**:
    - **Auto-Configuration**: Configures beans based on classpath.
    - **Starter Dependencies**: Pre-packaged POMs (e.g., `spring-boot-starter-web`).
    - **Spring Initializr**: Web tool to bootstrap projects.
    - **Embedded Servers**: Tomcat, Jetty, Undertow.
- **Annotations**:
    - `@SpringBootApplication` = `@EnableAutoConfiguration + @Configuration + @ComponentScan`.
- **Properties**:
    - Default file: `application.properties` / `application.yml`.
    - Profiles: `application-dev.properties`.
- **Logging**: Controlled via `application.properties` (`logging.level`).

---

## 7. Spring Boot Testing
- **Starter**: `spring-boot-starter-test` (JUnit, Mockito, AssertJ, Hamcrest).
- **Annotations**:
    - `@SpringBootTest`: Loads full application context.
    - `@WebMvcTest`: Tests only controller layer.
    - `@DataJpaTest`: Tests repository layer with in-memory DB.
- **Mocking**:
    - `@MockBean`: Spring-managed mock.
    - `@Mock`: Mockito standalone mock.
- **Transactional Tests**: Roll back after each test by default.
- **@DirtiesContext**: Forces reloading context between tests.

---

## 8. Data Management & JDBC
- **DataSource**: Connection pool to DB. Configurable in properties.
- **JdbcTemplate**:
    - Simplifies query execution.
    - Handles exceptions and resource management.
    - Uses callbacks (`RowMapper`, `ResultSetExtractor`).
- **Transactions**:
    - **Declarative**: `@Transactional`.
    - **Propagation Types**: REQUIRED, REQUIRES_NEW, SUPPORTS, etc.
    - **Rollback Policy**: Runtime exceptions by default, configurable.
- **TransactionManager**: Abstraction over different transaction APIs (JDBC, JPA).

---

## 9. Spring Data JPA
- **JPA**: Standard ORM API, often with Hibernate provider.
- **Repositories**:
    - `CrudRepository` – basic CRUD.
    - `JpaRepository` – CRUD + pagination/sorting.
    - `PagingAndSortingRepository` – adds paging.
- **Queries**:
    - Derived (e.g., `findByEmail`).
    - `@Query` annotation (JPQL/SQL).
- **Entities**: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`.
- **Lazy vs Eager Loading**: Lazy is preferred for performance.
- **Transactions**: Managed via `PlatformTransactionManager`.

---

## 10. Spring Cloud
- **Purpose**: Tools for microservices and distributed systems.
- **Components**:
    - **Eureka** (Service discovery).
    - **Ribbon** (Client-side load balancing).
    - **Feign** (Declarative REST client).
    - **Hystrix** (Circuit breaker).
    - **Config Server** (Centralized config).
    - **Bus** (Propagates events).
- **Annotations**:
    - `@EnableEurekaClient`, `@EnableFeignClients`, `@EnableHystrix`.
- **PCF (Pivotal Cloud Foundry)**: Cloud platform support.

---

## 11. Spring Security
- **DelegatingFilterProxy**: Routes requests through security filter chain.
- **Filter Chain**: Ordered filters handling authentication, authorization, CSRF, session mgmt.
- **Authentication**: Verify user identity (username/password, tokens, OAuth).
- **Authorization**: Grant or deny access based on roles.
- **Annotations**:
    - `@Secured`, `@RolesAllowed`, `@PreAuthorize`, `@PostAuthorize`.
- **Password Security**: `PasswordEncoder` interface (BCrypt recommended).
- **RBAC**: Implement role-based access control.
- **Custom Filters**: Extend `OncePerRequestFilter` and add to chain.
- **Context**: SecurityContext holds `Authentication` and `Principal`.

---


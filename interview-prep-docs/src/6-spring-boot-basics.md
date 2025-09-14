# 6. Spring Boot Basics

## What is Spring Boot?
Spring Boot is a framework that makes it easy to create stand-alone, production-grade Spring-based applications with minimal configuration. It eliminates boilerplate setup, provides embedded servers, and offers many features out-of-the-box for rapid development.

**Analogy:**
If traditional Spring is like assembling a car from parts, Spring Boot is like buying a ready-to-drive car with all the essentials pre-installed.

## Key Concepts (Crystal Clear Explanations)

### 1. Why Spring Boot?
- **Eliminates Boilerplate:** No need for complex XML or manual bean configuration.
- **Embedded Server:** No need to deploy WAR files; just run your app as a Java application.
- **Production Ready:** Built-in health checks, metrics, and externalized configuration.

### 2. Auto-Configuration
- Spring Boot automatically configures your application based on the libraries on the classpath.
- **Example:**
    - If you add `spring-boot-starter-web`, Spring Boot auto-configures Tomcat, Spring MVC, and JSON converters.

### 3. Starter Dependencies
- Starters are pre-defined dependency sets for common use cases (web, data, security, etc.).
- **Example:**
    - `spring-boot-starter-web` for web apps
    - `spring-boot-starter-data-jpa` for JPA
    - `spring-boot-starter-security` for security
- **build.gradle Example:**
    ```groovy
    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-web'
        implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    }
    ```

### 4. Spring Initializr
- A web tool (https://start.spring.io) to generate a Spring Boot project structure with selected dependencies.
- **How to use:**
    1. Go to https://start.spring.io
    2. Choose project type, language, dependencies
    3. Download and unzip the generated project

### 5. Embedded Servers
- Spring Boot apps run with embedded servers (Tomcat, Jetty, Undertow) by default.
- **Example:**
    - Run your app with `main()` and access it at `http://localhost:8080`.
    - No need for external server setup.

### 6. Main Application Class & Annotations
- The entry point is a class annotated with `@SpringBootApplication`.
- `@SpringBootApplication` combines:
    - `@EnableAutoConfiguration`: Enables auto-configuration
    - `@Configuration`: Marks as a source of bean definitions
    - `@ComponentScan`: Scans for components in the package
- **Example:**
    ```java
    @SpringBootApplication
    public class MyApp {
        public static void main(String[] args) {
            SpringApplication.run(MyApp.class, args);
        }
    }
    ```

### 7. Properties & Profiles
- **application.properties** or **application.yml**: Central place for configuration (port, DB, logging, etc.)
- **Profiles**: Use `application-dev.properties`, `application-prod.properties` for environment-specific settings.
- **Example:**
    ```properties
    # application.properties
    server.port=8081
    spring.datasource.url=jdbc:mysql://localhost:3306/mydb
    spring.profiles.active=dev
    ```

### 8. Logging
- Controlled via `application.properties` (e.g., `logging.level.root=INFO`).
- You can set logging levels for specific packages or classes to control the verbosity of logs.
- **Log Levels:** TRACE < DEBUG < INFO < WARN < ERROR < FATAL < OFF
- **How to configure a particular log level:**
    - To set the log level for a specific package or class, use:
    ```properties
    logging.level.org.springframework.web=DEBUG
    logging.level.com.example.service=TRACE
    logging.level.com.example.repository=ERROR
    ```
    - To set the root log level (applies to all logs unless overridden):
    ```properties
    logging.level.root=INFO
    ```
- **Log Output:**
    - By default, logs are printed to the console. You can also write logs to a file:
    ```properties
    logging.file.name=app.log
    logging.file.path=/var/logs
    ```
- **Example:**
    ```properties
    # application.properties
    logging.level.root=INFO
    logging.level.org.springframework=DEBUG
    logging.level.com.example=TRACE
    logging.file.name=application.log
    ```
    This will print all Spring logs at DEBUG or higher, all com.example logs at TRACE or higher, and all other logs at INFO or higher, writing them to application.log.

### 9. Running a Spring Boot App
- Run with `./gradlew bootRun` or by running the `main()` method.
- **Example:**
    ```shell
    ./gradlew bootRun
    # or
    java -jar build/libs/myapp.jar
    ```

## Typical Interview Questions
- What is Spring Boot and why use it?
- How does auto-configuration work?
- What are starter dependencies?
- How do you configure properties and profiles?
- How do you run a Spring Boot application?
- What does `@SpringBootApplication` do?
- How does embedded server work in Spring Boot?

---

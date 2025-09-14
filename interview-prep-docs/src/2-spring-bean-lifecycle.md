# 2. Spring Bean Lifecycle

## Important Notes

- **Spring Bean**: Any object managed by the Spring IoC container. Beans are created, configured, and managed by Spring, not by direct instantiation in your code.
  - Example:
    ```java
    @Component
    public class MyService {}
    ```

- **Bean Scopes**: Define the lifecycle and visibility of a bean within the container.
  - **singleton** (default): Only one instance per Spring container.
    ```java
    @Component
    // Default is singleton
    public class SingletonBean {}
    ```
  - **prototype**: A new instance is created every time the bean is requested.
    ```java
    @Component
    @Scope("prototype")
    public class PrototypeBean {}
    ```
  - **request**, **session**, **application**: Used in web-aware Spring applications for per-request, per-session, or per-application beans.
    ```java
    @Component
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public class RequestScopedBean {}
    ```

- **Bean Lifecycle Stages**:
  1. **Instantiation**: Spring creates the bean instance (using constructor or factory method).
  2. **Populate Properties**: Dependencies are injected (DI).
  3. **Post-Construction**: `@PostConstruct` annotated method is called (if present).
      ```java
      @PostConstruct
      public void init() {
          // initialization logic
      }
      ```
  4. **InitializingBean**: If bean implements `InitializingBean`, `afterPropertiesSet()` is called.
      ```java
      public class MyBean implements InitializingBean {
          @Override
          public void afterPropertiesSet() {
              // custom init logic
          }
      }
      ```
  5. **Custom Init Method**: If specified in config (`initMethod`), it is called.
      ```java
      @Bean(initMethod = "customInit")
      public MyBean myBean() { return new MyBean(); }
      ```
  6. **Bean in Use**: Bean is ready for use by the application.
  7. **Destruction**: On container shutdown, `@PreDestroy` method is called, then `DisposableBean.destroy()`, then custom destroy method (if any).
      ```java
      @PreDestroy
      public void cleanup() {
          // cleanup logic
      }
      ```

- **Profiles**: Allow you to define beans for specific environments (e.g., dev, test, prod).
  - Example:
    ```java
    @Service
    @Profile("dev")
    public class DevDataService implements DataService {}
    ```
  - Properties for each profile can be loaded from `application-dev.properties`, `application-test.properties`, etc.

- **@Value and SpEL**: Inject literal values, property values, or evaluate expressions into beans.
  - Example:
    ```java
    @Value("${server.port}")
    private int port;
    @Value("#{2 * 5}")
    private int result;
    ```

## Detailed Lifecycle Example

```java
@Component
@Scope("singleton")
public class ExampleBean implements InitializingBean, DisposableBean {
    @PostConstruct
    public void postConstruct() {
        System.out.println("@PostConstruct called");
    }
    @Override
    public void afterPropertiesSet() {
        System.out.println("afterPropertiesSet called");
    }
    public void customInit() {
        System.out.println("Custom init method");
    }
    @PreDestroy
    public void preDestroy() {
        System.out.println("@PreDestroy called");
    }
    @Override
    public void destroy() {
        System.out.println("DisposableBean.destroy called");
    }
}
```

- Registering with custom init/destroy methods:
    ```java
    @Bean(initMethod = "customInit", destroyMethod = "customDestroy")
    public ExampleBean exampleBean() {
        return new ExampleBean();
    }
    ```

---

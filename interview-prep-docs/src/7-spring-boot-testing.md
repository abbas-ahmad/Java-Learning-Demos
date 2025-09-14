# 7. Spring Boot Testing

## What is Spring Boot Testing?
Spring Boot Testing is the practice of writing automated tests for your Spring Boot applications to ensure correctness, reliability, and maintainability. Spring Boot provides powerful testing support with minimal configuration, using JUnit, Mockito, and other libraries.

**Analogy:**
Testing in Spring Boot is like having a safety net for your code: it catches bugs before they reach production, and gives you confidence to make changes.

## Key Concepts (Crystal Clear Explanations)

### 1. Test Starter
- **spring-boot-starter-test**: A dependency that brings in JUnit, Mockito, AssertJ, Hamcrest, and Spring TestContext.
- **How to add:**
    - In `build.gradle`:
    ```groovy
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    ```

### 2. Test Annotations
- `@SpringBootTest`: Loads the full application context. Use for integration tests.
    - **Example:**
    ```java
    @SpringBootTest
    class MyIntegrationTest {
        @Test
        void contextLoads() {}
    }
    ```
- `@WebMvcTest`: Loads only the web layer (controllers, filters, etc.). Use for controller tests.
    - **Example:**
    ```java
    @WebMvcTest(UserController.class)
    class UserControllerTest {
        @Autowired
        private MockMvc mockMvc;
        // ...
    }
    ```
- `@DataJpaTest`: Loads only JPA components (repositories, entities) with an in-memory database. Use for repository tests.
    - **Example:**
    ```java
    @DataJpaTest
    class UserRepositoryTest {
        @Autowired
        private UserRepository userRepository;
        // ...
    }
    ```

### 3. Mocking
- `@MockBean`: Creates a mock and adds it to the Spring context, replacing the real bean. Use for integration tests.
    - **Example:**
    ```java
    @MockBean
    private UserService userService;
    ```
- `@Mock`: Standard Mockito mock, not managed by Spring. Use for unit tests.
    - **Example:**
    ```java
    @Mock
    private UserRepository userRepository;
    ```

### 4. Transactional Tests
- By default, tests annotated with `@DataJpaTest` or `@Transactional` roll back transactions after each test, keeping the database clean.
    - **Example:**
    ```java
    @Transactional
    @Test
    void testSomething() {
        // changes are rolled back after test
    }
    ```

### 5. @DirtiesContext
- Forces Spring to reload the application context between tests. Use when a test changes the context in a way that could affect others.
    - **Example:**
    ```java
    @DirtiesContext
    class SomeTest {
        // ...
    }
    ```

### 6. MockMvc
- Allows you to test your controllers by simulating HTTP requests and asserting responses, without starting a real server.
    - **Example:**
    ```java
    @WebMvcTest(UserController.class)
    class UserControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Test
        void testGetUser() throws Exception {
            mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        }
    }
    ```

### 7. Test Slices
- Spring Boot provides test slices to load only parts of the application context for faster, focused tests:
    - `@WebMvcTest` (web layer), `@DataJpaTest` (JPA), `@JsonTest` (JSON serialization), etc.

### 8. Best Practices
- Use test slices for fast, focused tests.
- Use `@SpringBootTest` for full integration tests only when needed.
- Mock external dependencies.
- Name tests clearly and keep them independent.

## Typical Interview Questions
- What does `@SpringBootTest` do?
- Difference between `@WebMvcTest` and `@SpringBootTest`?
- How do you mock beans in Spring Boot tests?
- How do you test controllers without starting a server?
- How does transaction management work in tests?
- What is `@DirtiesContext` and when should you use it?

---

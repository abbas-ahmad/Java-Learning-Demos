# 5. REST with Spring

## What is REST?
REST (Representational State Transfer) is an architectural style for designing networked applications. It uses HTTP methods to perform operations on resources, and is stateless (each request contains all the information needed to process it).

**Analogy:**
Think of REST like a waiter in a restaurant: you (the client) make a request (order), the waiter (server) brings you the food (resource), and each order is independent.

## Key Concepts (Crystal Clear Explanations)

### 1. HTTP Methods
- **GET**: Retrieve data (read-only, safe, idempotent)
- **POST**: Create new data (not idempotent)
- **PUT**: Update/replace data (idempotent)
- **DELETE**: Remove data (idempotent)

**Example:**
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) { return userService.findById(id); }
    @PostMapping
    public User createUser(@RequestBody User user) { return userService.save(user); }
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) { return userService.update(id, user); }
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) { userService.delete(id); }
}
```

### 2. Idempotency
- **Idempotent**: Multiple identical requests have the same effect as a single request (GET, PUT, DELETE).
- **Non-idempotent**: Each request changes the state (POST).

### 3. Annotations
- `@RestController`: Marks the class as a REST controller (returns JSON/XML by default).
- `@ResponseBody`: Binds the return value to the HTTP response body.
- `@PathVariable`: Binds a method parameter to a URI template variable.
- `@RequestBody`: Binds the HTTP request body to a method parameter.

**Example:**
```java
@RestController
public class ProductController {
    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable Long id) { return productService.findById(id); }
    @PostMapping("/products")
    public Product addProduct(@RequestBody Product product) { return productService.save(product); }
}
```

### 4. Error Handling
- Use `@ExceptionHandler` in a controller or `@ControllerAdvice` globally to handle exceptions and return custom error responses.

**Example:**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
```

### 5. Content Negotiation
- Spring automatically returns JSON or XML based on the `Accept` header in the request.
- You can force a specific type using `produces`:

**Example:**
```java
@GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
public User getUser(@PathVariable Long id) { return userService.findById(id); }
```

### 6. Security
- Use HTTPS (TLS) for secure communication.
- Use authentication (e.g., Basic Auth, JWT, OAuth2) and authorization (roles/permissions).

**Example:**
```java
// With Spring Security
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            .and()
            .httpBasic();
    }
}
```

## Typical Interview Questions
- What is REST and what are its core principles?
- Explain idempotency in REST APIs.
- How do you map HTTP methods to CRUD operations?
- How do you handle errors in REST APIs?
- How does Spring handle content negotiation?
- How do you secure REST APIs in Spring?
- What is the difference between `@RestController` and `@Controller`?

---

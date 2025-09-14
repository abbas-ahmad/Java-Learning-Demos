# 4. Spring MVC

## What is Spring MVC?
Spring MVC (Model-View-Controller) is a web framework in the Spring ecosystem for building web applications. It separates the application into three main components:
- **Model**: Represents the data and business logic.
- **View**: The UI (HTML, JSP, Thymeleaf, etc.) shown to the user.
- **Controller**: Handles user requests, interacts with the model, and returns a view.

**Analogy:**
Think of Spring MVC as a restaurant:
- The **Controller** is the waiter who takes your order (request) and brings you food (response).
- The **Model** is the kitchen where the food (data) is prepared.
- The **View** is the plate on which the food is served to you (the user).

## Key Concepts (Crystal Clear Explanations)

### 1. DispatcherServlet
- The **DispatcherServlet** is the front controller in Spring MVC. It receives all incoming HTTP requests and routes them to the appropriate controller.
- **How it works:**
    1. Client sends a request (e.g., `/users/1`).
    2. DispatcherServlet receives the request.
    3. It finds the right controller and method to handle the request.
    4. It passes data to the controller, gets the result, and returns a view or response.

**Example:**
```java
// web.xml (if using XML config)
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```
With Spring Boot, DispatcherServlet is auto-configured.

### 2. Controller
- A **Controller** is a Java class annotated with `@Controller` or `@RestController` that handles web requests.
- `@Controller` returns a view (HTML, JSP, etc.).
- `@RestController` returns data (JSON, XML) directly (for REST APIs).

**Example:**
```java
@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Welcome!");
        return "home"; // returns view name
    }
}

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
```

### 3. Request Mapping Annotations
- `@RequestMapping`: Maps HTTP requests to handler methods (can specify path, method, params, etc.).
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`: Shortcuts for common HTTP methods.
- `@RequestParam`: Extracts query parameters from the URL.
- `@PathVariable`: Extracts values from the URL path.

**Example:**
```java
@GetMapping("/search")
public String search(@RequestParam String q, Model model) {
    model.addAttribute("results", service.search(q));
    return "results";
}

@GetMapping("/users/{id}")
public String getUser(@PathVariable Long id, Model model) {
    model.addAttribute("user", userService.findById(id));
    return "user";
}
```

### 4. Model
- The **Model** is used to pass data from the controller to the view.
- In handler methods, you can use the `Model` object to add attributes.

**Example:**
```java
@GetMapping("/profile")
public String profile(Model model) {
    model.addAttribute("user", userService.getCurrentUser());
    return "profile";
}
```

### 5. ViewResolver
- The **ViewResolver** maps logical view names returned by controllers to actual view files (e.g., Thymeleaf, JSP).
- With Spring Boot and Thymeleaf:
    - Returning `"home"` from a controller will render `src/main/resources/templates/home.html`.

**Example:**
```java
// application.properties
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
```

### 6. REST APIs with Spring MVC
- Use `@RestController` to build RESTful APIs.
- Methods return data objects, which Spring serializes to JSON or XML.

**Example:**
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.save(product);
    }
}
```

### 7. Exception Handling
- Use `@ExceptionHandler` in controllers or `@ControllerAdvice` globally to handle errors.

**Example:**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(Model model) {
        model.addAttribute("error", "User not found");
        return "error";
    }
}
```

## Typical Interview Questions
- What is DispatcherServlet and how does it work?
- Difference between `@Controller` and `@RestController`?
- How do you map requests to controller methods?
- How do you pass data from controller to view?
- How does Spring MVC handle exceptions?
- How do you build REST APIs with Spring MVC?

---

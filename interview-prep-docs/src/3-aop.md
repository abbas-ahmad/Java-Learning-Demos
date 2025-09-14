# 3. Aspect-Oriented Programming (AOP)

## What is AOP?
Aspect-Oriented Programming (AOP) is a programming paradigm that allows you to modularize cross-cutting concerns—functionality that cuts across multiple types or objects, such as logging, security, or transaction management. Instead of scattering and tangling this code throughout your business logic, AOP lets you define it in one place (an aspect) and apply it declaratively.

## Why Use AOP?
- **Separation of Concerns**: Keeps business logic clean by moving repetitive, cross-cutting code (like logging, security, transactions) into reusable aspects.
- **Maintainability**: Changes to cross-cutting logic are made in one place, not throughout the codebase.
- **Reusability**: Aspects can be applied to multiple classes/methods without code duplication.

## Key Concepts

### 1. Aspect
An **Aspect** is a class where you put code for a cross-cutting concern (like logging, security, or transactions). Think of it as a special helper that can add extra behavior to your main business code, without changing the business code itself.

**Analogy:**
If your application is a movie, an aspect is like a subtitle track: it adds information (like translations or commentary) without changing the movie itself.

**Example:**
```java
@Aspect
@Component
public class LoggingAspect { /* ... */ }
```

### 2. Join Point
A **Join Point** is any point during the execution of your program where an aspect's advice can be applied. In Spring AOP, join points are always method executions (i.e., when a method is called on a Spring bean).

**Think of a join point as a hook in your code where extra behavior can be inserted.**

**Analogy:**
Imagine a train (your application) stopping at various stations (join points). At each station, you can choose to perform an action (advice), like checking tickets (security), cleaning (logging), or refueling (transactions).

**Examples:**
- Calling a method on a service bean: `userService.createUser()` → This method call is a join point.
- Any public method in a Spring-managed bean can be a join point.

**In Spring AOP, you cannot advise field access, constructor calls, or exception handlers—only method executions.**

**Code Example:**
```java
// This is a join point: method execution
userService.createUser("John");
// You can apply advice before, after, or around this method execution.
```

**Visual:**
| Code                | Is it a Join Point in Spring AOP? |
|---------------------|:----------------------------------:|
| userService.save()  | Yes                                |
| new UserService()   | No                                 |
| userService.field   | No                                 |
| userService.get()   | Yes                                |

### 3. Pointcut
A **Pointcut** is a rule (expression) that tells Spring AOP *where* to apply your aspect's advice. It matches one or more join points.

**Analogy:**
If join points are all the stations on a train line, a pointcut is a filter that selects only the stations where you want to stop and do something.

**Example:**
```java
@Pointcut("execution(* com.example.service.*.*(..))")
public void serviceMethods() {}
```
This pointcut matches all method executions in the `com.example.service` package.

### 4. Advice
**Advice** is the actual code you want to run at a join point. It defines *what* you want to do and *when* (before, after, or around a method execution).

**Analogy:**
If a join point is a train station, advice is the action you take at that station (e.g., making an announcement, cleaning, or checking tickets).

**Types of Advice:**
- `@Before`: Runs before the method.
- `@After`: Runs after the method (no matter what).
- `@AfterReturning`: Runs after the method returns successfully.
- `@AfterThrowing`: Runs if the method throws an exception.
- `@Around`: Runs before and after the method, and can even prevent the method from running.

**Example:**
```java
@Before("serviceMethods()")
public void before(JoinPoint jp) {
    System.out.println("Before: " + jp.getSignature());
}
```

### 5. Weaving
**Weaving** is the process of connecting aspects to your main code, so that advice runs at the right join points. In Spring, weaving happens at runtime using proxies.

**Analogy:**
If your application is a shirt, weaving is like stitching a colored thread (aspect) into the fabric (your code) at specific places (join points).

**Code Example:**
Suppose you have an aspect and a service bean:

```java
@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* com.example.service.UserService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Before method: " + joinPoint.getSignature());
    }
}

@Service
public class UserService {
    public void createUser(String name) {
        System.out.println("Creating user: " + name);
    }
}
```

You enable weaving (proxying) in your configuration:

```java
@Configuration
@EnableAspectJAutoProxy
@ComponentScan("com.example")
public class AppConfig {}
```

When you run:
```java
UserService userService = context.getBean(UserService.class);
userService.createUser("John");
```
Spring weaves the aspect at runtime, so the output is:
```
Before method: void com.example.service.UserService.createUser(String)
Creating user: John
```

**Summary:**
Weaving is automatic in Spring AOP. When you use `@EnableAspectJAutoProxy` and define aspects, Spring creates proxies that weave your advice into the target beans at runtime.

### 6. Proxy
A **Proxy** is a wrapper object that Spring creates around your bean to apply advice. When you call a method on the bean, the proxy intercepts the call and runs any advice before/after the actual method.

**Analogy:**
If your bean is a singer, the proxy is like a sound engineer who can add effects (advice) to the singer's voice before the audience hears it.

**Example:**
You call `userService.save()`, but Spring actually routes the call through a proxy, which runs any advice, then calls the real method.

## Visual Analogy
Imagine you want to add security checks to every service method. Without AOP, you’d add code to every method. With AOP, you write the check once in an aspect, and Spring automatically applies it wherever you specify.

## Typical Use Cases
- Logging and auditing
- Transaction management
- Security checks
- Performance monitoring
- Caching

## Limitations
- Only public methods can be advised by default.
- Final methods/classes cannot be proxied.
- JDK proxies require interfaces; CGLIB proxies subclass the target class.

## Summary Table
| Concept      | Description                                      |
|--------------|--------------------------------------------------|
| Aspect       | Module for cross-cutting concern                 |
| JoinPoint    | Point in execution (e.g., method call)           |
| Pointcut     | Expression to select join points                 |
| Advice       | Code to run at join point                        |
| Weaving      | Linking aspects with code                        |
| Proxy        | Object created by Spring to apply advice         |

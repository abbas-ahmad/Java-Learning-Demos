# Code-First Development Guide for URL Shortener Project

This document provides a step-by-step guide to developing the URL Shortener project using a code-first approach (writing implementation before tests). It explains the process, best practices, and how to ensure code quality and maintainability even when not following TDD. This guide is suitable for interview demonstrations and real-world development when you need to move fast or are more comfortable with code-first workflows.

---

## What is Code-First Development?

**Code-First Development** means you start by writing the implementation code for your features, and then write tests to verify correctness and catch regressions. This is the most common approach in many teams, especially when requirements are clear or you need to quickly prototype a solution.

---

## Code-First Workflow: Step by Step

### 1. **Understand the Requirements**
- Clarify what the URL shortener must do (e.g., shorten URLs, resolve short codes, handle expiry).
- Break down requirements into features (e.g., "shorten a URL", "retrieve original URL", "handle duplicate URLs").
- Think about the main classes and responsibilities (service, repository, code generator, model, validator).

### 2. **Design the Solution**
- Sketch out the architecture (see ONBOARDING.md for reference diagrams).
- Decide on interfaces and main classes:
  - `URLShortenerService` (business logic)
  - `ShortCodeGenerator` (code generation)
  - `URLRepository` (storage)
  - `URLMapping` (data model)
  - `UrlValidator` (utility)
- Consider SOLID principles for maintainability and extensibility.

### 3. **Implement Core Features**
- Start with the main business logic in `URLShortenerServiceImpl`:
  - Shorten a URL (generate code, store mapping)
  - Retrieve original URL (lookup by code)
- Implement supporting classes:
  - `InMemoryURLRepository` for storage
  - `RandomShortCodeGenerator` for code generation
  - `UrlValidator` for input validation
  - `URLMapping` for data representation
- Use dependency injection for flexibility and testability.

### 4. **Add Error Handling and Edge Cases**
- Handle duplicate URLs (return existing code if not expired)
- Handle code collisions (retry code generation)
- Handle invalid URLs (throw exceptions)
- Handle expiry (do not return expired mappings)
- Ensure thread safety in repository (use `ConcurrentHashMap` and locks)

### 5. **Refactor for Clean Code and SOLID**
- Review your code for clarity, duplication, and single responsibility.
- Extract interfaces and utility classes as needed.
- Make sure each class has a clear, focused responsibility.
- Use dependency injection for all dependencies.
- Document design decisions in ONBOARDING.md or DESIGN.md.

### 6. **Write Unit and Integration Tests**
- After implementation, write tests for each feature and edge case:
  - Shorten a URL returns a code
  - Duplicate URLs return the same code
  - Invalid URLs throw exceptions
  - Code collisions are handled
  - Expiry logic works
  - Thread safety (concurrency tests)
- Use JUnit and Mockito for testing (see existing test classes for examples).
- Cover both positive and negative scenarios.

### 7. **Run and Fix Tests**
- Run all tests to verify correctness.
- Fix any bugs or issues found by tests.
- Refactor code and tests as needed for clarity and maintainability.

### 8. **Document and Review**
- Update ONBOARDING.md with any new design decisions or architecture changes.
- Add TODOs for future improvements in TODO.md.
- Review code for maintainability, extensibility, and adherence to best practices.

---

## Example: Code-First Implementation Flow for `shortenUrl()`

This section provides a detailed, step-by-step example of how you might implement the `shortenUrl()` feature using a code-first approach, including design, implementation, and testing.

### 1. **Design the Method Signature and Responsibilities**
- Decide what the method should do: take a long URL (and optionally expiry), return a short code.
- Consider edge cases: duplicate URLs, invalid URLs, code collisions, expiry.
- Plan dependencies: repository for storage, generator for code creation, validator for input.

### 2. **Write the Initial Implementation**
- Implement the basic logic in `URLShortenerServiceImpl`:
  - Validate the input URL.
  - Check if the URL is already shortened and not expired.
  - If not, generate a new short code.
  - Check for code collisions and retry if needed.
  - Save the mapping in the repository.
  - Return the short code.

Example (simplified):
```java
public String shortenUrl(String longUrl, LocalDateTime expiresAt) {
    UrlValidator.validate(longUrl);
    URLMapping existing = repository.findByLongUrl(longUrl);
    if (existing != null && !existing.isExpired()) {
        return existing.getShortCode();
    }
    String shortCode = generator.generateShortCode(longUrl);
    int maxAttempts = 10;
    int attempts = 1;
    while (repository.findByShortCode(shortCode) != null && attempts < maxAttempts) {
        shortCode = generator.generateShortCode(longUrl);
        attempts++;
    }
    if (repository.findByShortCode(shortCode) != null) {
        throw new IllegalStateException("Failed to generate a unique short code after " + maxAttempts + " attempts");
    }
    URLMapping mapping = new URLMapping(shortCode, longUrl, LocalDateTime.now(), expiresAt);
    repository.save(mapping);
    return shortCode;
}
```

### 3. **Handle Edge Cases and Error Handling**
- Throw `IllegalArgumentException` for invalid URLs.
- Throw `IllegalStateException` if unable to generate a unique code after max attempts.
- Ensure expired mappings are not reused.
- Use dependency injection for repository and generator.

### 4. **Refactor for Clean Code and SOLID**
- Extract code generation and validation to their own classes/interfaces if not already done.
- Ensure each class has a single responsibility.
- Use interfaces for repository and generator for easier testing and extension.
- Document any design decisions in ONBOARDING.md or DESIGN.md.

### 5. **Write Unit and Integration Tests**
- After implementation, write tests to cover:
  - Shortening a valid URL returns a code.
  - Shortening the same URL returns the same code if not expired.
  - Shortening an expired URL creates a new code.
  - Invalid URLs throw exceptions.
  - Code collisions are handled (retries).
  - Repository is updated correctly.
  - Thread safety (if applicable).

Example test (JUnit + Mockito):
```java
@Test
void testShortenUrlReturnsShortCode() {
    String longUrl = "https://example.com/abc";
    String shortCode = "xyz123";
    when(generator.generateShortCode(longUrl)).thenReturn(shortCode);
    when(repository.findByLongUrl(longUrl)).thenReturn(null);
    String result = service.shortenUrl(longUrl);
    assertEquals(shortCode, result);
    verify(repository).save(any(URLMapping.class));
}
```

### 6. **Run and Fix Tests**
- Run all tests to verify correctness.
- Fix any bugs or issues found by tests.
- Refactor code and tests as needed for clarity and maintainability.

### 7. **Document and Review**
- Update ONBOARDING.md with any new design decisions or architecture changes.
- Add TODOs for future improvements in TODO.md.
- Review code for maintainability, extensibility, and adherence to best practices.

---

## Best Practices for Code-First Development
- **Think before you code:** Plan your architecture and responsibilities.
- **Write clear, maintainable code:** Use meaningful names, keep methods small, and classes focused.
- **Refactor often:** Don’t be afraid to improve your code after it works.
- **Write comprehensive tests:** Even if tests come after code, they are essential for quality.
- **Document your design:** Keep ONBOARDING.md and TODO.md up to date.
- **Review for SOLID:** Make sure your design is extensible and maintainable.

---

## Summary Table
| Step         | Action                | Outcome         |
|--------------|----------------------|-----------------|
| Design       | Plan architecture     | Clear structure |
| Implement    | Write code            | Features work   |
| Refactor     | Clean up code         | Maintainable    |
| Test         | Write/run tests       | Quality assured |
| Document     | Update docs           | Easy onboarding |

---

## Final Thoughts
- Code-first is fast and familiar, but requires discipline to ensure quality.
- Always write tests, even if after the code.
- Refactor and document as you go.
- In interviews, explain your process: "I implement the feature, then write tests to cover all scenarios, refactor for clarity, and document my design."

---

## References
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://site.mockito.org/)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)

---

Happy coding!

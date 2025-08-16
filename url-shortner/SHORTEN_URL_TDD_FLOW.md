# TDD Process and Implementation Evolution for `shortenUrl()`

This document details the step-by-step process of developing the `shortenUrl()` method in the URL Shortener project using Test-Driven Development (TDD). It covers the initial requirements, incremental test and implementation steps, how design decisions are made, and how the method evolved to its current robust form while following SOLID principles.

---

## 1. Initial Requirement & Design Thinking
- **Goal:** Given a long URL, return a unique short code.
- **Assumptions:** No expiry, no collision handling, no validation, no duplicate check.
- **Design Thought:** Start with a single class, no abstractions yet. Focus on behavior.

### Step 1: Write the First Test (Red)
```java
@Test
void testShortenUrlReturnsShortCode() {
    String longUrl = "https://example.com";
    String code = service.shortenUrl(longUrl);
    assertNotNull(code);
}
```

### Step 2: Minimal Implementation (Green)
```java
public String shortenUrl(String longUrl) {
    return "abc123"; // Hardcoded for now
}
```

### Step 3: Refactor (Refactor)
- No refactor needed yet. Prepare for next requirement.

---

## 2. Add Uniqueness and Storage (Evolving Design)
- **Goal:** Each long URL gets a unique code, and we can retrieve the original URL.
- **Design Thought:** Need to store mappings. Add a map. Still a single class.

### Step 1: Add Test for Uniqueness
```java
@Test
void testShortenUrlReturnsUniqueCodes() {
    String url1 = "https://a.com";
    String url2 = "https://b.com";
    assertNotEquals(service.shortenUrl(url1), service.shortenUrl(url2));
}
```

### Step 2: Implement Storage and Code Generation
```java
private final Map<String, String> urlToCode = new HashMap<>();
private final Map<String, String> codeToUrl = new HashMap<>();
public String shortenUrl(String longUrl) {
    String code = generateCode(longUrl); // e.g., hash or random
    urlToCode.put(longUrl, code);
    codeToUrl.put(code, longUrl);
    return code;
}
```

### Step 3: Refactor
- If code generation logic grows, consider extracting a method or class.

---

## 3. Handle Duplicate URLs (Applying SRP)
- **Goal:** Shortening the same URL returns the same code.
- **Design Thought:** Logic for checking duplicates is growing. Still manageable, but keep SRP in mind.

### Step 1: Add Test for Duplicate URLs
```java
@Test
void testShortenUrlReturnsSameCodeForSameUrl() {
    String url = "https://example.com";
    String code1 = service.shortenUrl(url);
    String code2 = service.shortenUrl(url);
    assertEquals(code1, code2);
}
```

### Step 2: Update Implementation
```java
public String shortenUrl(String longUrl) {
    if (urlToCode.containsKey(longUrl)) return urlToCode.get(longUrl);
    String code = generateCode(longUrl);
    urlToCode.put(longUrl, code);
    codeToUrl.put(code, longUrl);
    return code;
}
```

### Step 3: Refactor
- If storage logic grows, extract a repository class (SRP, DIP).

---

## 4. Add URL Validation (Open/Closed, SRP)
- **Goal:** Only valid HTTP/HTTPS URLs are accepted.
- **Design Thought:** Validation logic is a separate concern. Extract to a utility class.

### Step 1: Add Validation Test
```java
@Test
void testShortenUrlThrowsForInvalidUrl() {
    String invalidUrl = "htp://bad";
    assertThrows(IllegalArgumentException.class, () -> service.shortenUrl(invalidUrl));
}
```

### Step 2: Add Validation Logic
```java
public String shortenUrl(String longUrl) {
    UrlValidator.validate(longUrl);
    // ...existing code...
}
```

### Step 3: Refactor
- Extract `UrlValidator` utility. Now validation can be extended without modifying service (OCP).

---

## 5. Add Expiry Support (Liskov, OCP)
- **Goal:** Support optional expiry for mappings.
- **Design Thought:** Data model is growing. Extract a `URLMapping` class. Add expiry logic there.

### Step 1: Add Expiry Test
```java
@Test
void testShortenUrlWithExpiry() {
    String url = "https://example.com";
    LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);
    String code = service.shortenUrl(url, expiresAt);
    assertNotNull(code);
    // Retrieve and check expiry
    URLMapping mapping = repository.findByShortCode(code);
    assertNotNull(mapping);
    assertEquals(expiresAt, mapping.getExpiresAt());
    assertFalse(mapping.isExpired());
}
```

### Step 2: Update Implementation
- Add `expiresAt` to mapping and check for expiry on lookup.
- Refactor to use `URLMapping` class.
- Update repository and service to handle expiry.

### Step 3: Refactor
- Ensure `URLMapping` encapsulates expiry logic (`isExpired()` method).
- Service checks expiry before returning existing mapping.
- Document this design change in ONBOARDING.md or DESIGN.md.

---

## 6. Handle Code Collisions (OCP, DIP)
- **Goal:** Ensure generated short codes are unique, retry if collision.
- **Design Thought:** Code generation is a separate concern. Extract `ShortCodeGenerator` interface. Use dependency injection.

### Step 1: Add Collision Test
```java
@Test
void testShortenUrlRetriesOnCollision() {
    String longUrl = "https://example.com/collision";
    String duplicateCode = "dup123";
    String uniqueCode = "uniq456";
    when(generator.generateShortCode(longUrl)).thenReturn(duplicateCode, uniqueCode);
    when(repository.findByLongUrl(longUrl)).thenReturn(null);
    when(repository.findByShortCode(duplicateCode)).thenReturn(new URLMapping(duplicateCode, "other", LocalDateTime.now()));
    when(repository.findByShortCode(uniqueCode)).thenReturn(null);
    String result = service.shortenUrl(longUrl);
    assertEquals(uniqueCode, result);
    verify(generator, times(2)).generateShortCode(longUrl);
}
```

### Step 2: Implement Retry Logic
- In the service, after generating a code, check for collision in the repository.
- If a collision is found, retry up to a maximum number of attempts.
- If still not unique, throw an exception.

### Step 3: Refactor
- Extract `ShortCodeGenerator` interface and implementations.
- Extract `URLRepository` interface and in-memory implementation.
- Service now depends on abstractions (DIP).
- Document this design change.

---

## 7. Add Robustness, Thread Safety, and Finalize Design (SRP, DIP, OCP, LSP)
- **Goal:** Ensure the implementation is robust, thread-safe, and extensible.
- **Design Thought:** Review for SOLID adherence, refactor for clarity, and document design.

### Step 1: Add/Expand Concurrency Tests
```java
@Test
void shouldHandleConcurrentSavesAndLookups() throws InterruptedException {
    // ...see InMemoryURLRepositoryConcurrencyTest.java for full test...
}
```
- Ensure repository is thread-safe and atomic.

### Step 2: Final Implementation Review
- Service uses dependency injection for repository and generator.
- All business logic is in the service, not in the repository or generator.
- Repository is responsible only for storage/retrieval.
- Generator is responsible only for code generation.
- All classes have a single responsibility.
- All dependencies are abstractions (interfaces).
- Expiry logic is encapsulated in the model.
- All edge cases (invalid input, expiry, collisions, max attempts) are covered by tests.

### Step 3: Document and Clean Up
- Update ONBOARDING.md or DESIGN.md with final architecture and design decisions.
- Add TODOs for future improvements (e.g., distributed storage, analytics).

---

## 8. Design Decisions & SOLID Application Table
| Step | Feature Added                | Test Example                      | Implementation Change         | Design Decision / SOLID Principle |
|------|-----------------------------|-----------------------------------|------------------------------|-----------------------------------|
| 1    | Basic code generation       | Returns non-null code             | Hardcoded return             | No abstraction yet                |
| 2    | Uniqueness & storage        | Unique codes for different URLs   | Add maps, generator          | Storage logic, SRP                |
| 3    | Duplicate handling          | Same code for same URL            | Check map before generate    | SRP, consider repository          |
| 4    | URL validation              | Throws for invalid URL            | Add validator                | SRP, OCP (utility)                |
| 5    | Expiry support              | Expiry test                       | Add expiresAt to mapping     | LSP, OCP, new model class         |
| 6    | Collision handling          | Retries on collision              | Retry loop                   | OCP, DIP, extract generator       |
| 7    | Atomic storage, robustness  | All above                         | Final implementation         | DIP, SRP, LSP, OCP                |

---

## 9. Key Takeaways: TDD + SOLID
- TDD helps evolve the method from a simple stub to a robust, production-ready implementation.
- Each feature is driven by a test, ensuring correctness and maintainability.
- **Design evolves with tests:**
  - Refactor after each green test to apply SOLID.
  - Extract interfaces and classes as needed.
  - Use dependency injection for testability and flexibility.
  - Document major design changes in ONBOARDING.md or DESIGN.md.
- The current implementation is the result of incremental, test-driven improvements and design refactoring.

---

## 10. Example: TDD-Driven Design Evolution (Summary)
1. **Test:** Write a failing test for a new behavior.
2. **Code:** Write minimal code to pass the test.
3. **Refactor:** Improve code, apply SOLID, extract abstractions, document design.
4. **Repeat:** Add next test, evolve design.

---

For more details, see the test classes and service implementation in the codebase, and the ONBOARDING.md for architecture and design documentation.

# Test-Driven Development (TDD) Guide for URL Shortener Project

This document provides a step-by-step guide to developing the URL Shortener project using Test-Driven Development (TDD), with a focus on how design decisions are made and how to follow SOLID principles throughout the process. This guide is suitable for interview demonstrations and real-world development.

---

## What is TDD?

**Test-Driven Development (TDD)** is a software development process where you:
1. **Write a failing test** for a new feature or improvement.
2. **Write the minimal code** to make the test pass.
3. **Refactor** the code for clarity, maintainability, and performance.
4. **Repeat** for each new feature or requirement.

TDD ensures your code is well-tested, modular, and meets requirements from the start. Importantly, TDD is not just about testing—it's about evolving your design incrementally, guided by real requirements.

---

## TDD Workflow: Step by Step (with Design Focus)

### 1. **Understand the Requirements**
- Clarify what the URL shortener must do (e.g., shorten URLs, resolve short codes, handle expiry).
- Break down requirements into small, testable units (e.g., "shorten a URL", "retrieve original URL", "handle duplicate URLs").
- **Think about responsibilities:** What classes or modules might be needed? (e.g., service, repository, code generator)

### 2. **Write the First Test (Red Phase)**
- Start with the simplest, most fundamental behavior (e.g., shortening a URL returns a short code).
- Write a unit test that describes the expected behavior, even though the implementation does not exist yet.
- Example (JUnit):
  ```java
  @Test
  void testShortenUrlReturnsShortCode() {
      String longUrl = "https://example.com";
      String shortCode = service.shortenUrl(longUrl);
      assertNotNull(shortCode);
  }
  ```
- Run the test. It should **fail** (red), because the feature is not implemented.

### 3. **Implement the Minimal Code (Green Phase)**
- Write just enough code to make the test pass.
- Do not worry about edge cases or optimizations yet.
- Example:
  ```java
  public String shortenUrl(String longUrl) {
      return "abc123"; // Hardcoded for now
  }
  ```
- Run the test. It should **pass** (green).

### 4. **Refactor (Refactor Phase)**
- Clean up the code, remove duplication, improve naming, and prepare for the next test.
- **Apply SOLID principles:**
  - **Single Responsibility:** Is this class/method doing only one thing?
  - **Open/Closed:** Can you add new behavior without modifying existing code?
  - **Liskov Substitution:** Are abstractions substitutable?
  - **Interface Segregation:** Are interfaces focused?
  - **Dependency Inversion:** Are you depending on abstractions, not concretions?
- Ensure all tests still pass after refactoring.
- **Document design decisions** if you introduce new abstractions or refactor responsibilities.

### 5. **Add More Tests (Repeat)**
- Add tests for new requirements, edge cases, and error handling:
  - Shortening the same URL returns the same code.
  - Resolving a short code returns the original URL.
  - Handling invalid URLs.
  - Handling code collisions.
  - Expiry logic.
- For each new test:
  - Write the test (it fails).
  - Implement the minimal code to pass the test.
  - Refactor and review design (apply SOLID, extract interfaces, decouple, etc.).
  - **If a test is hard to write, consider if your design needs to change!**

### 6. **Evolve the Design with TDD**
- As you add tests, your design will evolve to be more modular and robust.
- **Let tests reveal design needs:**
  - If you need to mock a dependency, extract an interface.
  - If a class is doing too much, split it.
  - If logic is duplicated, refactor to a utility or helper.
- **Document major design changes** (e.g., new interfaces, refactored classes) in your onboarding or DESIGN.md.

### 7. **Review and Refactor for SOLID**
- After a feature is complete, review the code for SOLID adherence.
- Refactor as needed, using your tests as a safety net.
- Track technical debt or future refactoring ideas in TODO.md.

---

## Example: TDD-Driven Design Evolution for `shortenUrl()`

1. **First Test:**
   - Write a test for `shortenUrl()` returning a code.
   - Implement a simple method in a single class.
2. **Second Test:**
   - Test for duplicate URLs returning the same code.
   - Realize you need a way to store mappings, so you introduce a repository abstraction.
3. **Third Test:**
   - Test for code collisions.
   - Abstract code generation into a `ShortCodeGenerator` interface.
4. **Fourth Test:**
   - Test for expiry logic.
   - Add expiry to the `URLMapping` model.
5. **Fifth Test:**
   - Test for invalid URLs.
   - Introduce a `UrlValidator` utility.

At each step, you refactor to keep classes focused, introduce interfaces, and decouple components—following SOLID.

---

## TDD Mindset & Best Practices
- **Think in terms of behavior:** What should the system do?
- **Write tests first:** Let tests drive your design.
- **Keep tests small and focused:** One behavior per test.
- **Refactor often:** Clean code is easier to maintain and extend.
- **Use mocks/stubs:** Isolate units and test only the code under test.
- **Test edge cases:** Invalid input, duplicates, expiry, etc.
- **Trust your tests:** They give you confidence to refactor and extend.
- **Document design decisions:** Update ONBOARDING.md or DESIGN.md as your design evolves.

---

## Example: TDD for `shortenUrl()` (with Design Decisions)

1. **Test:**
   ```java
   @Test
   void testShortenUrlReturnsShortCode() {
       String longUrl = "https://example.com";
       String code = service.shortenUrl(longUrl);
       assertNotNull(code);
   }
   ```
2. **Code:**
   ```java
   public String shortenUrl(String longUrl) {
       return "abc123";
   }
   ```
   - *Design note:* No abstraction yet, just a stub.
3. **Test:**
   ```java
   @Test
   void testShortenUrlReturnsSameCodeForSameUrl() {
       String longUrl = "https://example.com";
       String code1 = service.shortenUrl(longUrl);
       String code2 = service.shortenUrl(longUrl);
       assertEquals(code1, code2);
   }
   ```
4. **Code:**
   ```java
   private final Map<String, String> urlToCode = new HashMap<>();
   public String shortenUrl(String longUrl) {
       if (urlToCode.containsKey(longUrl)) return urlToCode.get(longUrl);
       String code = generateCode(longUrl);
       urlToCode.put(longUrl, code);
       return code;
   }
   ```
   - *Design note:* Now we need storage. If this grows, extract a repository.
5. **Test:**
   - Add test for code collisions or expiry. If mocking is needed, extract interfaces (e.g., `ShortCodeGenerator`).
6. **Refactor:**
   - Extract `URLRepository` and `ShortCodeGenerator` interfaces.
   - Use dependency injection for testability and SOLID.
   - Document these abstractions in ONBOARDING.md.

---

## Summary Table
| Step         | Action                | Outcome         | Design Decision/Refactor         |
|--------------|----------------------|-----------------|----------------------------------|
| Red          | Write failing test    | Test fails      |                                  |
| Green        | Write minimal code    | Test passes     |                                  |
| Refactor     | Clean up code         | All tests pass  | Apply SOLID, extract interfaces  |
| Repeat       | Add next test         | Evolve design   | Document design changes          |

---

## Final Thoughts
- TDD is iterative: small steps, frequent feedback.
- Your tests are your specification and safety net.
- **Design evolves with tests:** Use refactoring to apply SOLID and keep code maintainable.
- In interviews, **explain your thought process**: "I start with a test for the required behavior, then implement just enough code to pass, then refactor. If the design needs to change, I extract interfaces or refactor classes, always guided by tests."
- For production, TDD leads to better-tested, more maintainable code.
- **Document your design decisions** as you go.

---

## References
- [TDD by Example (Kent Beck)](https://www.amazon.com/Test-Driven-Development-Kent-Beck/dp/0321146530)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://site.mockito.org/)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)

---

Happy TDD-ing!

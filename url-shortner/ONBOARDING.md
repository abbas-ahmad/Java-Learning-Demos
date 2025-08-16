# URL Shortener Project: Onboarding & Architecture Guide

## Overview
This project is a simple, extensible URL shortener service implemented in Java. It provides functionality to generate short codes for long URLs, store and retrieve mappings, and handle expiration. The codebase is modular, testable, and ready for extension or integration with persistent storage.

---

## High-Level Architecture

```
+-------------------+         +-------------------+         +-------------------+
|                   |         |                   |         |                   |
|  ShortCodeGenerator+--------> URLShortenerService+--------> URLRepository      |
|                   |         |                   |         |                   |
+-------------------+         +-------------------+         +-------------------+
         ^                                                         ^
         |                                                         |
         |                                                         |
         |         +-------------------+         +-----------------+
         |         |                   |         |                 |
         +---------+  URLMapping       |         |  Util/Validator |
                   |                   |         |                 |
                   +-------------------+         +-----------------+
```

- **ShortCodeGenerator**: Generates unique short codes for URLs.
- **URLShortenerService**: Main business logic for shortening and resolving URLs.
- **URLRepository**: Stores and retrieves URL mappings (in-memory by default).
- **URLMapping**: Data model for a short code, long URL, and metadata.
- **Util/Validator**: Utility classes for input validation, etc.

---

## Detailed Flow

### 1. Shortening a URL

1. **Input**: User provides a long URL (and optionally, an expiry time).
2. **Validation**: The URL is validated for correctness.
3. **Check for Existing Mapping**: The repository is queried to see if the URL is already shortened and not expired.
4. **Short Code Generation**: If not found, a new short code is generated using the `ShortCodeGenerator`.
5. **Collision Check**: The repository checks if the generated code is unique. If not, retries up to a max attempt.
6. **Mapping Storage**: The new mapping is saved in the repository.
7. **Output**: The short code is returned to the user.

**Sequence Diagram:**

```
User
 |
 | 1. shortenUrl(longUrl, [expiresAt])
 v
URLShortenerService
 |--2. validate(longUrl)--------------------->UrlValidator
 |<------------------------------------------|
 |--3. findByLongUrl(longUrl)--------------->URLRepository
 |<------------------------------------------|
 |--4. generateShortCode(longUrl)----------->ShortCodeGenerator
 |<------------------------------------------|
 |--5. findByShortCode(code)---------------->URLRepository
 |<------------------------------------------|
 |--6. save(mapping)------------------------>URLRepository
 |<------------------------------------------|
 | 7. return shortCode
 v
User
```

### 2. Resolving a Short Code

1. **Input**: User provides a short code.
2. **Lookup**: The repository is queried for the mapping.
3. **Expiry Check**: If the mapping is expired, null is returned.
4. **Output**: The original long URL is returned if found and not expired.

---

## Key Classes & Responsibilities

### `URLShortenerService`
- Orchestrates the shortening and resolving process.
- Handles validation, collision checks, and expiry logic.

### `ShortCodeGenerator`
- Interface for generating short codes.
- Implementations:
  - `RandomShortCodeGenerator`: Uses secure random and Base62.
  - `SimpleShortCodeGenerator`: (Example) Simple deterministic logic.

### `URLRepository`
- Interface for storing and retrieving mappings.
- Implementation:
  - `InMemoryURLRepository`: Thread-safe, atomic, in-memory storage using `ConcurrentHashMap` and explicit locking for atomicity.

### `URLMapping`
- Data class for short code, long URL, creation time, and optional expiry.
- Provides expiry check logic.

### `UrlValidator`
- Utility for validating input URLs.

---

## Thread Safety & Concurrency
- The repository uses `ConcurrentHashMap` for concurrent access.
- The `save()` method is made atomic using an explicit `ReentrantLock` to ensure both maps are updated together.
- Test cases verify thread safety under concurrent access.

---

## Extending the Project
- **Persistence**: Implement `URLRepository` for a database or distributed cache.
- **Custom Short Codes**: Add support for user-supplied codes.
- **Analytics**: Track usage statistics in the repository or a new service.
- **API Layer**: Expose endpoints using Spring Boot or another framework.

---

## Testing
- Uses JUnit 5 for unit and integration tests.
- Mockito is used for mocking dependencies in service tests.
- Concurrency tests ensure thread safety of the repository.

---

## Build & Run
- Standard Gradle Java project.
- To run tests:
  ```
  ./gradlew test
  ```
- To build:
  ```
  ./gradlew build
  ```

---

## Example Usage

```java
URLShortenerService service = new URLShortenerServiceImpl(
    new InMemoryURLRepository(),
    new RandomShortCodeGenerator(7)
);
String code = service.shortenUrl("https://example.com");
String url = service.getOriginalUrl(code);
```

---

## Diagrams

### Component Diagram

```
+-------------------+
|  User/API Layer   |
+-------------------+
          |
          v
+-------------------+
| URLShortenerService|
+-------------------+
     |           |
     v           v
ShortCodeGen   URLRepository
     |           |
     v           v
  (Random)   (InMemory)
```

---

## Contribution & TODOs
See `TODO.md` for ideas and open tasks.

---

## Contact
For questions or contributions, open an issue or pull request on the repository.


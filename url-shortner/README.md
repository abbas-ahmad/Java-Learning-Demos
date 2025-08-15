# URL Shortener

A simple Java-based URL shortener service. It provides functionality to shorten long URLs and retrieve the original URL using a short code. The implementation uses in-memory storage and a simple counter-based short code generator.

## Features
- Shorten long URLs to unique short codes
- Retrieve original URLs from short codes
- In-memory storage (for development/testing)
- Thread-safe code generation

## Project Structure
- `model/` - Data models (e.g., URLMapping)
- `repository/` - Storage interfaces and in-memory implementation
- `service/` - Business logic and code generation
- `test/` - Unit tests

## Getting Started
1. Clone the repository
2. Build with Gradle: `./gradlew build`
3. Run tests: `./gradlew test`

## Limitations
- Not suitable for distributed or production use without further improvements
- No persistent storage

See [TODO.md](TODO.md) for planned improvements.


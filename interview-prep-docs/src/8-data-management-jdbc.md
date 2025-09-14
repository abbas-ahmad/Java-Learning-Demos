# 8. Data Management & JDBC

## What is Data Management in Spring?
Data management in Spring refers to how your application connects to databases, executes queries, manages transactions, and handles data access logic. Spring provides powerful abstractions to simplify JDBC (Java Database Connectivity) and transaction management.

**Analogy:**
Think of data management as a restaurant's kitchen operations:
- The **DataSource** is like the kitchen's pantry (connection pool).
- **JdbcTemplate** is the chef who prepares dishes (queries) efficiently.
- **Transactions** are like making sure a full meal is served correctly or not at all (all-or-nothing).

## Key Concepts (Crystal Clear Explanations)

### 1. DataSource
- A **DataSource** is a pool of database connections managed by Spring.
- Configured in `application.properties`:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/mydb
    spring.datasource.username=root
    spring.datasource.password=secret
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    ```
- Spring Boot auto-configures a connection pool (HikariCP by default).

### 2. JdbcTemplate
- **JdbcTemplate** is a helper class that simplifies JDBC operations (query, update, etc.).
- Handles resource management (connections, statements, result sets) and exception translation.
- Uses callbacks like `RowMapper` to map rows to objects.
- **Example:**
    ```java
    @Repository
    public class UserRepository {
        @Autowired
        private JdbcTemplate jdbcTemplate;

        public List<User> findAll() {
            return jdbcTemplate.query("SELECT * FROM users", (rs, rowNum) ->
                new User(rs.getLong("id"), rs.getString("name")));
        }
    }
    ```

### 3. Transactions
- **Transaction**: A sequence of operations performed as a single logical unit of work. Either all succeed or all fail (atomicity).
- **Declarative Transactions**: Use `@Transactional` to manage transactions automatically.
    - **Example:**
    ```java
    @Service
    public class UserService {
        @Transactional
        public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
            // withdraw from one user, deposit to another
        }
    }
    ```
- **Propagation Types:**
    - `REQUIRED`: Join existing or create new transaction (default).
    - `REQUIRES_NEW`: Always start a new transaction.
    - `SUPPORTS`: Join if exists, else run non-transactionally.
    - `NOT_SUPPORTED`: Run non-transactionally, suspending any existing transaction.
    - `MANDATORY`: Must run within an existing transaction; throws an exception if none exists.
    - `NEVER`: Must run outside of a transaction; throws an exception if a transaction exists.
    - `NESTED`: Runs within a nested transaction if a current transaction exists; otherwise, behaves like REQUIRED.
- **Rollback Policy:**
    - By default, rolls back on unchecked (runtime) exceptions.
    - Can be customized: `@Transactional(rollbackFor = Exception.class)`

### 3a. Transaction Propagation Types (Detailed Explanation & Use Cases)

**Propagation** defines how transactions relate to each other when a transactional method is called from another transactional method. Spring provides several propagation types:

| Propagation Type | Description | Typical Use Case |
|------------------|-------------|------------------|
| REQUIRED (default) | Joins the current transaction if one exists; otherwise, creates a new one. | Most business methods. Ensures all operations are in a single transaction. |
| REQUIRES_NEW | Suspends the current transaction and always starts a new one. | Logging/audit actions that must commit even if the main transaction rolls back. |
| SUPPORTS | Joins the current transaction if one exists; otherwise, runs non-transactionally. | Read-only operations that can participate in a transaction if present. |
| NOT_SUPPORTED | Runs non-transactionally, suspending any existing transaction. | Non-critical operations that should not be part of a transaction (e.g., sending emails). |
| MANDATORY | Must run within an existing transaction; throws an exception if none exists. | Methods that must always be called within a transaction (e.g., low-level data access). |
| NEVER | Must run outside of a transaction; throws an exception if a transaction exists. | Operations that must not be transactional (e.g., certain cache operations). |
| NESTED | Runs within a nested transaction if a current transaction exists; otherwise, behaves like REQUIRED. | Savepoints for partial rollbacks within a larger transaction (requires JDBC savepoint support). |

**Example:**
```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void logAuditEvent(AuditEvent event) {
    // This will commit even if the caller's transaction rolls back
}
```

### Comparing REQUIRED, REQUIRES_NEW, and NESTED Propagation Types

| Propagation Type | Transaction Behavior | Use Case | Example |
|------------------|---------------------|----------|---------|
| REQUIRED (default) | Joins the current transaction if one exists; otherwise, creates a new one. | Most business methods. Ensures all operations are in a single transaction. | `@Transactional(propagation = Propagation.REQUIRED)` |
| REQUIRES_NEW | Suspends the current transaction and always starts a new one. The inner transaction is independent and will commit/rollback separately. | Logging/audit actions that must commit even if the main transaction rolls back. | `@Transactional(propagation = Propagation.REQUIRES_NEW)` |
| NESTED | Runs within a nested transaction if a current transaction exists; otherwise, behaves like REQUIRED. Uses savepoints, so you can roll back the nested transaction without affecting the outer transaction. Requires a JDBC driver that supports savepoints. | Partial rollbacks within a larger transaction (e.g., try a sub-operation, roll it back if it fails, but keep the main transaction alive). | `@Transactional(propagation = Propagation.NESTED)` |

**Key Differences:**
- **REQUIRED**: Joins or creates a transaction. If the outer transaction rolls back, all inner operations roll back too.
- **REQUIRES_NEW**: Always creates a new, independent transaction. The outer transaction is suspended. If the inner transaction commits, its changes are permanent even if the outer transaction rolls back.
- **NESTED**: Creates a savepoint within the existing transaction. If the nested transaction rolls back, only changes after the savepoint are undone; the outer transaction can still commit. If the outer transaction rolls back, everything is rolled back.

**Visual Example:**
Suppose you have an outer service method that calls an inner method:

```java
@Service
public class OuterService {
    @Autowired
    private InnerService innerService;

    @Transactional // REQUIRED by default
    public void outerMethod() {
        // ... some DB operations
        try {
            innerService.innerMethod();
        } catch (Exception e) {
            // handle exception
        }
        // ... more DB operations
    }
}

@Service
public class InnerService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerMethod() {
        // ... DB operations
    }
}
```
- With **REQUIRES_NEW**, `innerMethod` runs in its own transaction. If `outerMethod` rolls back, changes in `innerMethod` remain committed.
- With **NESTED**, if `innerMethod` fails, only its changes are rolled back (to the savepoint), but the outer transaction can continue and commit other changes.
- With **REQUIRED**, both methods participate in the same transaction. If either fails and rolls back, all changes are rolled back.

### 3b. Transaction Isolation Levels (Detailed Explanation & Use Cases)

**Isolation** determines how/when the changes made by one transaction become visible to others. It helps prevent concurrency issues like dirty reads, non-repeatable reads, and phantom reads.

| Isolation Level | Description | Prevents | Typical Use Case |
|-----------------|-------------|----------|------------------|
| DEFAULT | Uses the database's default isolation level. | Depends on DB | Most cases unless you have specific needs. |
| READ_UNCOMMITTED | Allows dirty reads (uncommitted changes from other transactions). | None | Rarely used; for performance over consistency. |
| READ_COMMITTED | Prevents dirty reads; allows non-repeatable reads and phantom reads. | Dirty reads | Most common; balances consistency and performance. |
| REPEATABLE_READ | Prevents dirty and non-repeatable reads; allows phantom reads. | Dirty, non-repeatable reads | Financial apps needing repeatable reads. |
| SERIALIZABLE | Strictest; prevents dirty, non-repeatable, and phantom reads. | All | Critical data integrity, but slowest. |

**Example:**
```java
@Transactional(isolation = Isolation.REPEATABLE_READ)
public void processOrder(Long orderId) {
    // Ensures data read in this transaction won't change until it completes
}
```

### 4. TransactionManager
- **TransactionManager** is an abstraction for managing transactions across different APIs (JDBC, JPA, etc.).
- Spring Boot auto-configures the right one based on your dependencies.
- **Example:**
    - For JDBC: `DataSourceTransactionManager`
    - For JPA: `JpaTransactionManager`

### 5. Exception Translation
- Spring translates SQLExceptions into its own `DataAccessException` hierarchy, making error handling consistent and easier.
- **Example:**
    ```java
    try {
        jdbcTemplate.queryForObject(...);
    } catch (DataAccessException ex) {
        // handle DB error
    }
    ```

### 3c. Summary Table: Transaction Isolation Levels

| Isolation Level      | Dirty Read | Non-Repeatable Read | Phantom Read |
|---------------------|:----------:|:-------------------:|:------------:|
| READ_UNCOMMITTED    |    Yes     |        Yes          |     Yes      |
| READ_COMMITTED      |    No      |        Yes          |     Yes      |
| REPEATABLE_READ     |    No      |        No           |     Yes      |
| SERIALIZABLE        |    No      |        No           |     No       |

---



## Typical Interview Questions
- What is a DataSource and how do you configure it in Spring Boot?
- How does JdbcTemplate simplify JDBC code?
- How do you manage transactions in Spring?
- What is the role of @Transactional?
- What are transaction propagation types?
- How does Spring handle SQL exceptions?
- What is a TransactionManager?

---

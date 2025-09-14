# 9. Spring Data JPA

Spring Data JPA is a powerful abstraction over JPA (Java Persistence API) that makes it easy to implement data access layers in Spring applications. It reduces boilerplate code and provides a rich set of features for working with relational databases.

---

## 1. What is JPA?
- **JPA (Java Persistence API)** is a standard specification for object-relational mapping (ORM) in Java.
- It allows you to map Java objects (entities) to database tables.
- **Hibernate** is the most popular JPA implementation.

**Example:**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    // getters and setters
}
```

---

## 2. Spring Data JPA Repositories
Spring Data JPA provides several repository interfaces to simplify data access:

| Interface                   | Description                                 |
|-----------------------------|---------------------------------------------|
| CrudRepository<T, ID>       | Basic CRUD operations                       |
| JpaRepository<T, ID>        | CRUD + pagination and sorting               |
| PagingAndSortingRepository<T, ID> | Adds paging and sorting support      |

**Example:**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query methods can be added here
}
```

---

## 3. Query Methods
Spring Data JPA supports multiple ways to define queries:

### a) Derived Query Methods
- Method names are parsed to generate SQL automatically.
- **Example:**
    ```java
    List<User> findByEmail(String email);
    List<User> findByNameAndEmail(String name, String email);
    ```

### b) @Query Annotation
- Use JPQL or native SQL for custom queries.
- **Example:**
    ```java
    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findUserByEmail(@Param("email") String email);
    
    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    User findByEmailNative(String email);
    ```

---

## 4. Entities
- Entities are Java classes mapped to database tables using JPA annotations.
- **Key Annotations:**
    - `@Entity`: Marks the class as a JPA entity.
    - `@Table`: Specifies the table name (optional if same as class).
    - `@Id`: Marks the primary key field.
    - `@GeneratedValue`: Specifies how the primary key is generated.
    - `@Column`: Maps a field to a column (optional if names match).

**Example:**
```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private Double price;
    // getters and setters
}
```

---

## 5. Lazy vs Eager Loading
- **Lazy Loading**: Data is loaded only when accessed. Default for collections.
    - Efficient for performance, avoids unnecessary data fetching.
- **Eager Loading**: Data is loaded immediately with the parent entity.
    - Can lead to performance issues if not used carefully.

**Example:**
```java
@Entity
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(fetch = FetchType.LAZY)
    private List<OrderItem> items; // Loaded only when accessed
}
```

---

## 6. Transactions
- Spring manages transactions using `@Transactional` and a `PlatformTransactionManager`.
- Ensures data consistency and rollback on errors.

**Example:**
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void updateUserEmail(Long userId, String newEmail) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setEmail(newEmail);
        userRepository.save(user);
    }
}
```

---

## 7. Typical Interview Questions
- What is JPA and how does it relate to Hibernate?
- How do you define a JPA entity?
- What is the difference between CrudRepository and JpaRepository?
- How do you write custom queries in Spring Data JPA?
- What is lazy loading and when would you use it?
- How does Spring manage transactions in JPA?

---

## 8. Summary Table
| Concept         | Annotation/Interface         | Purpose/Example                                  |
|-----------------|-----------------------------|--------------------------------------------------|
| Entity          | @Entity, @Table             | Maps Java class to DB table                      |
| Primary Key     | @Id, @GeneratedValue        | Uniquely identifies entity                       |
| Repository      | JpaRepository, CrudRepository| Data access layer abstraction                    |
| Query           | Derived, @Query             | Find by method name or custom JPQL/SQL           |
| Loading         | FetchType.LAZY/EAGER        | Controls when related data is loaded             |
| Transaction     | @Transactional              | Ensures atomicity and consistency                |

---

This should make Spring Data JPA concepts crystal clear for interviews, with code snippets and explanations for each key area.

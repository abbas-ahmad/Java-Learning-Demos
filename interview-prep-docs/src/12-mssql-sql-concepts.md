# Microsoft SQL Server & SQL Concepts

This document covers essential Microsoft SQL Server and general SQL concepts, focusing on query writing, optimization, and interview-relevant topics for backend developers.

---

## 1. Introduction to Microsoft SQL Server
- **Microsoft SQL Server** is a relational database management system (RDBMS) developed by Microsoft.
- Used for storing, retrieving, and managing data in enterprise applications.
- Supports T-SQL (Transact-SQL), Microsoft's extension to SQL.

---

## 2. SQL Basics
- **SQL (Structured Query Language)** is used to interact with relational databases.
- **Key SQL Statements:**
    - `SELECT`: Retrieve data from tables.
    - `INSERT`: Add new records.
    - `UPDATE`: Modify existing records.
    - `DELETE`: Remove records.
    - `CREATE`: Create database objects (tables, views, etc.).
    - `ALTER`: Modify database objects.
    - `DROP`: Delete database objects.

**Example:**
```sql
SELECT name, age FROM Employees WHERE department = 'IT';
```

---

## 3. Data Types
- Common SQL Server data types:
    - `INT`, `BIGINT`, `SMALLINT`, `TINYINT`
    - `VARCHAR(n)`, `NVARCHAR(n)`, `CHAR(n)`, `TEXT`
    - `DATE`, `DATETIME`, `TIME`
    - `BIT` (boolean)
    - `DECIMAL`, `NUMERIC`, `FLOAT`, `REAL`

---

## 4. Table Design & Constraints
- **Primary Key**: Uniquely identifies each row.
- **Foreign Key**: Enforces referential integrity between tables.
- **Unique**: Ensures all values in a column are unique.
- **Check**: Restricts values in a column.
- **Default**: Sets a default value for a column.

**Example:**
```sql
CREATE TABLE Employees (
    EmployeeID INT PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL,
    DepartmentID INT,
    Salary DECIMAL(10,2),
    CONSTRAINT FK_Department FOREIGN KEY (DepartmentID) REFERENCES Departments(DepartmentID)
);
```

---

## 5. Querying Data
### a) Filtering
- Use `WHERE` to filter rows.
- Use `AND`, `OR`, `NOT` for complex conditions.

**Example:**
```sql
SELECT * FROM Employees WHERE Salary > 50000 AND DepartmentID = 2;
```

### b) Sorting
- Use `ORDER BY` to sort results.

**Example:**
```sql
SELECT * FROM Employees ORDER BY Salary DESC;
```

### c) Limiting Results
- Use `TOP` to limit rows in SQL Server.

**Example:**
```sql
SELECT TOP 5 * FROM Employees ORDER BY HireDate DESC;
```

### d) Aggregation
- Use `COUNT`, `SUM`, `AVG`, `MIN`, `MAX` with `GROUP BY`.

**Example:**
```sql
SELECT DepartmentID, AVG(Salary) AS AvgSalary FROM Employees GROUP BY DepartmentID;
```

### e) Joins
- Combine data from multiple tables.
    - `INNER JOIN`: Only matching rows.
    - `LEFT JOIN`: All from left, matching from right.
    - `RIGHT JOIN`: All from right, matching from left.
    - `FULL OUTER JOIN`: All rows from both tables.

**Example:**
```sql
SELECT e.Name, d.DepartmentName
FROM Employees e
INNER JOIN Departments d ON e.DepartmentID = d.DepartmentID;
```

---

## 6. Subqueries & CTEs
- **Subquery**: Query inside another query.
- **CTE (Common Table Expression)**: Temporary result set for complex queries.

**Example (CTE):**
```sql
WITH HighEarners AS (
    SELECT Name, Salary FROM Employees WHERE Salary > 100000
)
SELECT * FROM HighEarners;
```

---

## 7. Indexes
- **Clustered Index**: Sorts and stores data rows in the table based on key.
- **Non-Clustered Index**: Separate structure for fast lookups.
- **Best Practices:**
    - Use indexes on columns used in `WHERE`, `JOIN`, `ORDER BY`.
    - Avoid over-indexing (can slow down writes).

**Example:**
```sql
CREATE INDEX idx_salary ON Employees(Salary);
```

---

## 8. Views
- **View**: Virtual table based on a query.
- Useful for abstraction, security, and simplifying complex queries.

**Example:**
```sql
CREATE VIEW IT_Employees AS
SELECT * FROM Employees WHERE DepartmentID = 2;
```

---

## 9. Stored Procedures & Functions
- **Stored Procedure**: Precompiled set of SQL statements.
- **Function**: Returns a value, can be used in queries.

**Example (Stored Procedure):**
```sql
CREATE PROCEDURE GetEmployeeById @EmpId INT
AS
BEGIN
    SELECT * FROM Employees WHERE EmployeeID = @EmpId;
END
```

**Example (Function):**
```sql
CREATE FUNCTION GetTotalSalary(@DeptId INT)
RETURNS DECIMAL(10,2)
AS
BEGIN
    DECLARE @Total DECIMAL(10,2)
    SELECT @Total = SUM(Salary) FROM Employees WHERE DepartmentID = @DeptId
    RETURN @Total
END
```

---

## 10. Transactions
- **Transaction**: Group of SQL statements executed as a single unit.
- Use `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`.

**Example:**
```sql
BEGIN TRANSACTION
    UPDATE Accounts SET Balance = Balance - 100 WHERE AccountID = 1;
    UPDATE Accounts SET Balance = Balance + 100 WHERE AccountID = 2;
IF @@ERROR <> 0
    ROLLBACK
ELSE
    COMMIT
```

---

## 11. Error Handling
- Use `TRY...CATCH` for error handling in T-SQL.

**Example:**
```sql
BEGIN TRY
    -- SQL statements
END TRY
BEGIN CATCH
    SELECT ERROR_MESSAGE() AS ErrorMessage;
END CATCH
```

---

## 12. Performance Tuning
- Analyze query plans with `SET SHOWPLAN_ALL ON` or SQL Server Management Studio (SSMS).
- Use indexes wisely.
- Avoid `SELECT *` in production queries.
- Use proper data types and normalization.
- Archive or partition large tables.

---

## 13. Security
- Use roles and permissions to control access.
- Avoid dynamic SQL to prevent SQL injection.
- Use parameterized queries in application code.

---

## 14. Interview Tips & Common Questions
- Explain the difference between clustered and non-clustered indexes.
- Write a query to find the second highest salary.
- How do you prevent SQL injection?
- What is a CTE and when would you use it?
- How do you optimize a slow query?
- What is the difference between `INNER JOIN` and `LEFT JOIN`?
- How do you handle transactions and rollbacks?

---

## 15. Important Interview Questions & Answers

### 1. What is the difference between clustered and non-clustered indexes?
**Answer:**
- A **clustered index** determines the physical order of data in a table. Each table can have only one clustered index. The data rows are stored in order of the clustered index key.
- A **non-clustered index** is a separate structure from the data rows. It contains a copy of indexed columns and a pointer to the actual data row. Tables can have multiple non-clustered indexes.

### 2. Write a query to find the second highest salary from the Employees table.
**Answer:**
```sql
SELECT MAX(Salary) AS SecondHighestSalary
FROM Employees
WHERE Salary < (SELECT MAX(Salary) FROM Employees);
```

### 3. How do you prevent SQL injection?
**Answer:**
- Use parameterized queries or prepared statements instead of string concatenation.
- Validate and sanitize user input.
- Use ORM frameworks or stored procedures where possible.

**Example (Parameterized Query in Java):**
```java
String sql = "SELECT * FROM Employees WHERE name = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1, userInput);
```

### 4. What is a CTE and when would you use it?
**Answer:**
- A **CTE (Common Table Expression)** is a temporary result set defined within the execution scope of a single query. It improves readability and is useful for recursive queries or breaking down complex queries.

**Example:**
```sql
WITH DepartmentCount AS (
    SELECT DepartmentID, COUNT(*) AS EmpCount
    FROM Employees
    GROUP BY DepartmentID
)
SELECT * FROM DepartmentCount WHERE EmpCount > 10;
```

### 5. How do you optimize a slow query?
**Answer:**
- Analyze the execution plan to identify bottlenecks.
- Add appropriate indexes on columns used in WHERE, JOIN, and ORDER BY clauses.
- Avoid SELECT *; select only needed columns.
- Use joins instead of subqueries where possible.
- Archive or partition large tables.

### 6. What is the difference between INNER JOIN and LEFT JOIN?
**Answer:**
- **INNER JOIN** returns only rows with matching values in both tables.
- **LEFT JOIN** returns all rows from the left table and matching rows from the right table; if no match, NULLs are returned for right table columns.

### 7. How do you handle transactions and rollbacks?
**Answer:**
- Use `BEGIN TRANSACTION` to start, `COMMIT` to save, and `ROLLBACK` to undo changes if an error occurs.
- In application code, use try-catch blocks to handle exceptions and roll back as needed.

**Example:**
```sql
BEGIN TRANSACTION
    -- SQL statements
    IF @@ERROR <> 0
        ROLLBACK
    ELSE
        COMMIT
```

### 8. What is normalization? Why is it important?
**Answer:**
- **Normalization** is the process of organizing data to reduce redundancy and improve data integrity. It involves dividing tables and defining relationships. It helps avoid anomalies and ensures efficient updates.

### 9. What is denormalization? When would you use it?
**Answer:**
- **Denormalization** is the process of combining tables to reduce joins and improve read performance, at the cost of some redundancy. Used in reporting or analytics scenarios where read speed is critical.

### 10. What is the difference between DELETE and TRUNCATE?
**Answer:**
- **DELETE** removes rows one at a time and can have a WHERE clause; it logs each row deletion and can be rolled back.
- **TRUNCATE** removes all rows from a table, is faster, cannot have a WHERE clause, and cannot be rolled back in some databases.

### 11. How do you find duplicate records in a table?
**Answer:**
```sql
SELECT column1, COUNT(*)
FROM table_name
GROUP BY column1
HAVING COUNT(*) > 1;
```

### 12. What is an execution plan and how do you use it?
**Answer:**
- An **execution plan** shows how SQL Server will execute a query, including index usage and join methods. Use it to identify slow operations and optimize queries.
- In SSMS, click "Display Estimated Execution Plan" before running a query.

### 13. What is ACID in databases?
**Answer:**
- **Atomicity**: All operations in a transaction succeed or none do.
- **Consistency**: Transactions bring the database from one valid state to another.
- **Isolation**: Transactions do not interfere with each other.
- **Durability**: Once committed, changes are permanent.

### 14. What is a stored procedure? What are its advantages?
**Answer:**
- A **stored procedure** is a precompiled set of SQL statements stored in the database. Advantages: improved performance, reusability, security, and easier maintenance.

### 15. How do you handle NULL values in SQL?
**Answer:**
- Use `IS NULL` or `IS NOT NULL` in WHERE clauses.
- Use `COALESCE()` or `ISNULL()` to provide default values.

**Example:**
```sql
SELECT ISNULL(Phone, 'N/A') FROM Employees;
```

---

This section provides concise answers to common SQL Server interview questions, with code examples and explanations to help you prepare effectively.

---

This document provides a comprehensive overview of Microsoft SQL Server and SQL concepts, with a focus on query writing and interview preparation. For deeper dives, see the official [Microsoft SQL Server documentation](https://docs.microsoft.com/en-us/sql/sql-server/).

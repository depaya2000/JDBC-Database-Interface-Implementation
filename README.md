# JDBC Database System Project

This project is based on a system description provided as coursework and focuses on full-stack database handling using Java (JDBC) and Microsoft SQL Server. The task involves modeling the database schema, implementing a set of provided Java interfaces, and verifying functionality through unit tests.

## 📁 Project Structure

The project is divided into three main parts:

### 1. Modeling the System

- The system description was used to design the database schema.
- Data modeling was done using **Erwin Data Modeler**.
- The schema was exported and deployed directly into **Microsoft SQL Server**.
- Tables, relationships, and field definitions were created based on the interpretation of the specification.

### 2. Interface Implementation

- Java interfaces for key entities were provided as part of the assignment.
- Implementations are written using **JDBC**, adhering strictly to SQL Server syntax.
- For more complex operations, **stored procedures** in T-SQL were created and invoked via callable statements.

### 3. Testing

- Tests were written to verify interface methods.
- Special attention was given to the behavior of `Statement.execute()`:
  - Returns `true` for queries that yield a `ResultSet` (e.g., `SELECT`)
  - Returns `false` for updates or inserts (e.g., `INSERT`, `UPDATE`, `DELETE`)
- All methods were tested in isolation using mock data.

## 💻 Technologies Used

- Java (JDK 8 or above)
- JDBC API
- Microsoft SQL Server
- Transact-SQL (T-SQL)
- IntelliJ IDEA / Eclipse
- Erwin Data Modeler (for ER design)

## ⚙️ Setup Instructions

1. Clone the repository.
2. Set up your local SQL Server and execute the provided `.sql` schema file.
3. Open the project in IntelliJ or Eclipse.
4. Configure database connection in `db.properties` or in the Java code.
5. Run tests or main application classes to verify functionality.

## 📌 Notes

- The actual database schema is not graded, only the correctness of the interface logic.
- This project does **not** include the original task statement due to availability, but all functionality is reverse-documented in comments and code structure.

## 🪪 License

This project is shared for educational purposes and is not licensed for commercial use.

# Employee Management System

A backend Employee Management System built with Java and Spring Boot. This project demonstrates REST API development, relational database management, authentication, role-based authorization, and automated testing.

## Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.3.0
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA, Hibernate
- **Security:** Spring Security, BCrypt password hashing
- **Build Tool:** Maven
- **API Testing:** Postman
- **Automated Testing:** JUnit 5, Mockito, Spring MockMvc
- **IDE:** Eclipse

## Features

- CRUD operations for employees and departments
- RESTful APIs for managing employee and department records
- Employee–Department relationship using a foreign key
- Input validation and error handling
- Duplicate employee email validation
- Protection against deleting departments that have associated employees
- User registration with server-assigned `USER` role
- Session-based login and logout
- Password hashing using BCrypt
- Role-based authorization for `USER` and `ADMIN`
- Unit, controller, integration, and security tests
- Separate test configuration and database

## Project Structure

The application follows a layered architecture:

- **Controller:** Handles HTTP requests and responses
- **Service:** Contains business logic
- **Repository:** Performs database operations
- **Entity:** Represents database tables
- **DTO:** Handles request data and validation
- **Security configuration:** Defines authentication and authorization rules

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/login` | Log in using form parameters |
| POST | `/logout` | Log out and invalidate the session |

### Departments

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/departments` | Get all departments |
| GET | `/api/departments/{id}` | Get a department by ID |
| POST | `/api/departments` | Create a department |
| PUT | `/api/departments/{id}` | Update a department |
| DELETE | `/api/departments/{id}` | Delete a department |

### Employees

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/employees` | Get all employees |
| GET | `/api/employees/{id}` | Get an employee by ID |
| POST | `/api/employees` | Create an employee |
| PUT | `/api/employees/{id}` | Update an employee |
| DELETE | `/api/employees/{id}` | Delete an employee |

## Authorization

- Public access is allowed for user registration.
- Authenticated `USER` and `ADMIN` accounts can access GET endpoints under `/api/**`.
- Write operations under `/api/**` require the `ADMIN` role.
- Passwords are stored using BCrypt hashing.
- Session-based authentication is used for login and logout.

## Getting Started

### Prerequisites

- JDK 21
- Maven
- PostgreSQL
- Postman (optional, for manual API testing)

### Setup

1. Clone this repository.
2. Create a PostgreSQL database for the application.
3. Configure your database connection URL, username, and password in the application configuration.
4. Build the project:

   ```bash
   mvn clean package
   ```

5. Run the application:

   ```bash
   mvn spring-boot:run
   ```

6. Test the API endpoints using Postman.

**Note:** Configure your local database credentials securely. Do not commit passwords or other secrets to GitHub.

## Testing

The project includes:

- Service-layer unit tests
- Controller tests using MockMvc
- Integration tests for employee and department operations
- Security tests for registration, login, authorization, and logout
- Validation and exception-handling tests

The test profile is configured separately from the development database. Before running tests, verify that the `test` profile points to the dedicated test database.

Run the tests using:

```bash
mvn test
```

## Project Scope

This is a backend learning project focused on Spring Boot, REST APIs, database relationships, Spring Security, and automated testing. It does not currently include a React frontend.

## License

No license has been specified yet.

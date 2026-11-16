# Student Skill & Internship Management System

A Java 17, Spring Boot and MySQL web application for student profiles, technical skills, internship listings and application review.

## Technology
- Java 17
- Spring Boot 3.2.3 / Spring MVC
- Maven
- MySQL 8+
- HTML, CSS and JavaScript served from Spring Boot static resources

## Features
### Student
- Register and log in
- View and update profile details, including CGPA
- Add and remove skills with proficiency levels
- Browse open internships
- Submit applications and track their status

### Administrator
- View student and application information
- Create, update and close internship listings
- Review applications and record decisions and feedback

> Feature availability should be confirmed by running the application and completing the test checklist below. This README describes the current web implementation; it is not a Swing desktop application.

## Project layout
```text
src/main/java/com/college/internship/
  Main.java
  web/                 REST and page controllers
  service/             Business logic
  dao/                 JDBC data access
  database/            MySQL connection and initialization
  model/               Domain models
  util/                Configuration utilities
src/main/resources/
  application.properties
  static/              HTML, CSS and JavaScript
  database/schema.sql
database/schema.sql    Database schema and demo seed data
```

## Requirements
- JDK 17
- Maven 3.8+
- MySQL 8+

## Database configuration
Create a MySQL database/user with permission to create and update the application's schema. Configure credentials through environment variables (recommended):

PowerShell:
```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/student_skill_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USER = "your_mysql_user"
$env:DB_PASSWORD = "your_mysql_password"
```

The application also supports a local `src/main/resources/db.properties` file. That file is intentionally ignored by Git; do not commit credentials. The database initializer loads `database/schema.sql` from the classpath. Review the SQL before running it against any database containing valuable data.

For a fresh local database, you can also import the schema manually:
```bash
mysql -u your_mysql_user -p < database/schema.sql
```

The SQL file includes demonstration accounts and sample records. Change demo credentials and remove or replace demo data before any public deployment.

## Build and run
From the repository root:
```bash
mvn clean verify
mvn spring-boot:run
```

Or run the packaged application:
```bash
mvn clean package
java -jar target/student-skill-internship-system-1.0.0.jar
```

Open:
- `http://localhost:8080/login`
- `http://localhost:8080/register`

The server uses the `PORT` environment variable when supplied and defaults to 8080.

## API overview
- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/students/{id}`
- `PUT /api/students/{id}`
- `GET /api/skills`
- `GET /api/internships/open`
- `GET /api/applications/student/{studentId}`
- `POST /api/applications`

See the controller classes for the complete endpoint definitions and request shapes.

## Deployment notes
The repository includes a Dockerfile and Render Blueprint configuration. A production deployment also requires a reachable managed MySQL database and the `DB_URL`, `DB_USER` and `DB_PASSWORD` environment variables. Do not use a laptop-local MySQL server for a hosted deployment. Review the database initialization strategy and authentication/authorization controls before exposing the service publicly.

## Manual smoke-test checklist
- [ ] Login page and registration page load
- [ ] Valid and invalid login behave correctly
- [ ] Student profile loads and saves
- [ ] Skills list loads; adding/removing a skill persists
- [ ] Open internships load and search works
- [ ] Application submission persists and duplicate applications are rejected
- [ ] Student sees their application status
- [ ] Admin can manage internships and review applications
- [ ] Logout ends the current client session

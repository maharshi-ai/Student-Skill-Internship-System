# Student Skill & Internship Management System

A complete, modern Java 17 desktop application built with **Java Swing**, **JDBC**, and **MySQL** for managing student profiles, technical skills, internship listings, and application workflows.

---

## 🌟 Key Features

### 👨‍🎓 Student Module
* **Account Registration & Login**: Secure authentication with salted SHA-256 password hashing.
* **Profile Management**: View and edit department, phone, CGPA, graduation year, and bio.
* **Skill Management**: Add technical skills with proficiency levels (*Beginner*, *Intermediate*, *Advanced*) and remove skills.
* **Browse Internships**: Search and filter active internship listings by role title, company, location, or requirements.
* **Apply for Internships**: Submit internship applications with customized cover notes.
* **Track Application Status**: Monitor real-time status (*Pending*, *Accepted*, *Rejected*) and view admin review feedback.

### 👨‍💼 Placement Admin Module
* **Admin Authentication**: Dedicated admin login console.
* **Internship Listing CRUD**: Create, edit, update status (*OPEN/CLOSED*), and delete internship positions.
* **Registered Students Roster**: Inspect all registered students, their CGPA, department, and technical skill portfolios.
* **Application Review Console**: Review incoming student applications, cover notes, and CGPA.
* **Decision Management**: Accept or reject applications and attach personalized feedback notes.

---

## 🛠️ Technology Stack

* **Programming Language**: Java 17 (LTS)
* **GUI Framework**: Java Swing with [FlatLaf 3.4](https://www.formdev.com/flatlaf/) modern look-and-feel
* **Database**: MySQL 8.0+
* **Database Connectivity**: JDBC with `PreparedStatement` & `Transaction Management`
* **Dependency & Build Tool**: Apache Maven
* **Architecture**: Model-View-Controller (MVC) / DAO Pattern

---

## 📂 Project Package Structure

```
com.college.internship
├── Main.java                      # Application entry point
├── model/                         # OOP Domain Entities
│   ├── User.java                  # Abstract base class for User
│   ├── Student.java               # Extends User with student properties
│   ├── Admin.java                 # Extends User with admin properties
│   ├── Skill.java                 # Technical skill entity
│   ├── StudentSkill.java          # Student-Skill proficiency association
│   ├── Internship.java             # Internship listing entity
│   └── Application.java            # Application workflow entity
├── dao/                           # Data Access Object Pattern (Interfaces & JDBC Impl)
│   ├── UserDao.java & UserDaoImpl.java
│   ├── StudentDao.java & StudentDaoImpl.java
│   ├── SkillDao.java & SkillDaoImpl.java
│   ├── InternshipDao.java & InternshipDaoImpl.java
│   └── ApplicationDao.java & ApplicationDaoImpl.java
├── service/                       # Business Logic Layer
│   ├── AuthService.java           # Login, registration, password hashing
│   ├── StudentService.java       # Profile & skill logic
│   ├── InternshipService.java     # Internship CRUD & search
│   └── ApplicationService.java    # Application workflow logic
├── database/                      # Database Managers
│   ├── DatabaseConnection.java    # JDBC Connection singleton
│   └── DatabaseInitializer.java   # Schema execution manager
├── exception/                     # Custom Application Exceptions
│   ├── DatabaseException.java
│   ├── AuthenticationException.java
│   └── ValidationException.java
├── util/                          # Utility Classes
│   ├── PasswordUtil.java          # SHA-256 + Salt password hashing
│   └── ConfigUtil.java            # Property & environment loader
└── ui/                            # Java Swing Graphical User Interface
    ├── Theme.java                 # Palette & visual styles
    ├── Components.java            # Custom rounded buttons, cards, badges, tables
    ├── LoginFrame.java            # Authentication screen
    ├── RegisterFrame.java         # Student registration form
    ├── StudentDashboardFrame.java # Student main workspace
    └── AdminDashboardFrame.java   # Admin management workspace
```

---

## 🏛️ OOP Concepts Demonstrated

1. **Encapsulation**:
   * All domain models (`User`, `Student`, `Admin`, `Internship`, `Application`, `Skill`) keep fields `private` and expose controlled access via getters, setters, and constructors.
   * Field validation is enforced inside service layers before setting state.

2. **Inheritance**:
   * `User.java` is an abstract base class containing common user attributes (`id`, `name`, `email`, `passwordHash`, `salt`, `role`).
   * `Student.java` and `Admin.java` extend `User.java` to add role-specific attributes (`cgpa`, `department`, `adminDepartment`).

3. **Polymorphism**:
   * Method overriding: `Student` and `Admin` override `getDashboardTitle()` and `getRoleName()` from `User`.
   * Dynamic dispatch: `User.getWelcomeMessage()` returns role-specific welcome text dynamically at runtime.

4. **Abstraction**:
   * `User` class is abstract, preventing direct instantiation of a generic user.
   * DAO Interfaces (`UserDao`, `StudentDao`, `SkillDao`, `InternshipDao`, `ApplicationDao`) decouple database persistence logic from business logic.

5. **Interfaces**:
   * Used for all Data Access Objects (`UserDao`, `StudentDao`, etc.), allowing alternate implementations (e.g. In-Memory or PostgreSQL) without touching business logic.

6. **Exception Handling**:
   * Custom exception hierarchy (`DatabaseException`, `AuthenticationException`, `ValidationException`) handles invalid user inputs, duplicate emails, and database downtime gracefully with user-friendly GUI alerts.

7. **Collections**:
   * `List<StudentSkill>`, `List<Internship>`, `List<Application>`, `List<Skill>` are used throughout DAOs, services, and Swing `JTable` rendering models.

---

## 🗄️ Database Setup Instructions

### 1. Start MySQL Server
Ensure MySQL server is running on `localhost:3306`.

### 2. Import Schema SQL
Run the provided `database/schema.sql` script to create the `student_skill_management` database, tables, foreign keys, and seed demo records:

```bash
mysql -u root -p < database/schema.sql
```

Alternatively, open MySQL Workbench or phpMyAdmin and execute the script contents of [database/schema.sql](file:///c:/Users/Maharshi/OneDrive/Desktop/Student-Skill-Internship-System/database/schema.sql).

### 3. Configure Credentials
Edit `src/main/resources/db.properties` if your MySQL username/password differ from default (`root` / `root`):

```properties
db.url=jdbc:mysql://localhost:3306/student_skill_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

*Note: Environment variables `DB_USER` and `DB_PASSWORD` can also be set to override `db.properties` securely.*

---

## 🚀 How to Build and Run

### Step 1: Build Project
Clean and compile the project using Maven:

```bash
mvn clean package
```

### Step 2: Run Application
Execute using the Maven Exec Plugin:

```bash
mvn exec:java
```

Or run the compiled JAR directly:

```bash
java -jar target/student-skill-internship-system-1.0.0.jar
```

---

## 🔑 Pre-Seeded Test Credentials

| Role | Email | Password |
| :--- | :--- | :--- |
| **Placement Admin** | `admin@college.edu` | `admin123` |
| **Student** | `student@college.edu` | `student123` |

*Tip: The Login screen includes one-click **"Demo Admin"** and **"Demo Student"** buttons for rapid testing.*

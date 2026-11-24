# SkillBridge — Student Skill & Internship Management System

A Java 17 + Spring Boot web application for browsing internships, submitting student applications, and viewing an admin application overview.

## Features
- Internship catalogue with seeded sample opportunities
- Student application form with validation
- Admin overview of submitted applications
- Add/publish internship form
- Responsive charcoal-and-copper interface
- H2 database for a zero-configuration demo

## Run locally
Requirements: JDK 17+ and Maven 3.8+.

```bash
mvn spring-boot:run
```
Open http://localhost:8080

Build:
```bash
mvn clean package
java -jar target/skill-internship-management-1.0.0.jar
```

## Deploy on Render
1. In Render choose **New + → Web Service**, connect this repository.
2. Select **Docker** as the runtime (the repository includes a Dockerfile).
3. Deploy and open the generated `onrender.com` URL.

## Demo notes
No login is configured in this academic demo. The admin section is a demonstration view, not access-controlled. H2 is in-memory, so data resets when the service restarts/redeploys. Do not use real personal data in this demo.
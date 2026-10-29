-- ====================================================================
-- STUDENT SKILL & INTERNSHIP MANAGEMENT SYSTEM
-- MySQL Database Schema & Seed Data Script
-- ====================================================================

CREATE DATABASE IF NOT EXISTS student_skill_management;
USE student_skill_management;

-- --------------------------------------------------------------------
-- Table: users (Base table for Students and Admins)
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(256) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    role ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- Table: student_profiles (Extended details for Student role)
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS student_profiles (
    user_id INT PRIMARY KEY,
    department VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    cgpa DECIMAL(3, 2) NOT NULL,
    graduation_year INT NOT NULL,
    bio TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- Table: skills (Master list of technical skills)
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL DEFAULT 'General'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- Table: student_skills (Junction table linking students to skills)
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS student_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    skill_id INT NOT NULL,
    proficiency_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') NOT NULL DEFAULT 'INTERMEDIATE',
    UNIQUE KEY unique_student_skill (student_id, skill_id),
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- Table: internships (Internship listings posted by Admin)
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS internships (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    company VARCHAR(150) NOT NULL,
    location VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    requirements TEXT NOT NULL,
    stipend VARCHAR(50) NOT NULL,
    deadline DATE NOT NULL,
    status ENUM('OPEN', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- Table: applications (Internship applications submitted by Students)
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    internship_id INT NOT NULL,
    cover_letter TEXT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    admin_feedback TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_student_internship (student_id, internship_id),
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (internship_id) REFERENCES internships(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- SAMPLE SEED DATA
-- ====================================================================

-- Default Password Hashes generated using SHA-256 with Salt
-- Default Admin: admin@college.edu / password: admin123
-- Salt: a1b2c3d4e5f67890 | Hash for 'admin123'
-- Default Student: student@college.edu / password: student123
-- Salt: a1b2c3d4e5f67890 | Hash for 'student123'
INSERT INTO users (id, name, email, password_hash, salt, role) VALUES
(1, 'System Administrator', 'admin@college.edu', '4b43b8feb9f9ea668e96682130c2d73c9139dc9673b473955a7f305c870d44c9', 'a1b2c3d4e5f67890', 'ADMIN'),
(2, 'John Doe', 'student@college.edu', '5d9e74661cefb2e115ae0525e968f6058a0cbda5d2932a824e86ffe088e473bf', 'a1b2c3d4e5f67890', 'STUDENT')
ON DUPLICATE KEY UPDATE name=VALUES(name), password_hash=VALUES(password_hash), salt=VALUES(salt);

INSERT INTO student_profiles (user_id, department, phone, cgpa, graduation_year, bio) VALUES
(2, 'Computer Science & Engineering', '+1 555-0199', 8.85, 2025, 'Passionate software developer interested in Java backend systems, cloud architectures, and web APIs.')
ON DUPLICATE KEY UPDATE department=VALUES(department);

-- Master Skills List
INSERT INTO skills (id, name, category) VALUES
(1, 'Java Programming', 'Programming Languages'),
(2, 'Python', 'Programming Languages'),
(3, 'MySQL / SQL', 'Database'),
(4, 'React.js', 'Frontend Development'),
(5, 'Spring Boot', 'Backend Frameworks'),
(6, 'Data Structures & Algorithms', 'Computer Science Fundamentals'),
(7, 'Machine Learning', 'Data Science & AI'),
(8, 'UI/UX Design', 'Design'),
(9, 'Docker & Kubernetes', 'DevOps & Cloud'),
(10, 'Node.js & Express', 'Web Development')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Student Skills Mapping
INSERT INTO student_skills (student_id, skill_id, proficiency_level) VALUES
(2, 1, 'ADVANCED'),
(2, 3, 'INTERMEDIATE'),
(2, 6, 'ADVANCED')
ON DUPLICATE KEY UPDATE proficiency_level=VALUES(proficiency_level);

-- Sample Internship Listings
INSERT INTO internships (id, title, company, location, description, requirements, stipend, deadline, status) VALUES
(1, 'Software Engineering Intern', 'TechCorp Solutions', 'Bangalore, India (Hybrid)', 
 'Work alongside senior engineers building scalable Java microservices and cloud infrastructure.', 
 'Strong foundation in Java 17, Object-Oriented Design, Data Structures, and SQL.', '$800 / month', '2026-11-30', 'OPEN'),

(2, 'Frontend Development Intern', 'WebCraft Studios', 'Remote', 
 'Design and implement responsive user interfaces using modern JavaScript frameworks and standard CSS visual design systems.', 
 'Experience with HTML5, CSS3, JavaScript, React.js, and RESTful API integration.', '$650 / month', '2026-12-15', 'OPEN'),

(3, 'Data Science & AI Intern', 'DataSphere Analytics', 'Hyderabad, India', 
 'Analyze large datasets, develop predictive ML models, and build automated data pipelines for business intelligence.', 
 'Proficiency in Python, Pandas, NumPy, Scikit-Learn, SQL, and basic Machine Learning algorithms.', '$900 / month', '2026-11-20', 'OPEN'),

(4, 'Cloud & DevOps Intern', 'CloudScale Technologies', 'Pune, India', 
 'Assist in containerizing microservices, setting up CI/CD pipelines, and monitoring AWS cloud deployments.', 
 'Knowledge of Docker, Linux shell scripting, Git, and basic AWS / GCP services.', '$750 / month', '2026-12-01', 'OPEN'),

(5, 'Full-Stack Developer Intern', 'InnoWave Labs', 'Remote', 
 'Contribute to both web frontend and backend services for an enterprise analytics platform.', 
 'Knowledge of Node.js, Express, React, and MongoDB/MySQL database design.', '$700 / month', '2026-11-15', 'OPEN')
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- Sample Application
INSERT INTO applications (id, student_id, internship_id, cover_letter, status, admin_feedback) VALUES
(1, 2, 1, 'I am excited to apply for the Software Engineering Intern position. I have a strong background in Java 17, Data Structures, and Database design with an 8.85 CGPA.', 'PENDING', NULL)
ON DUPLICATE KEY UPDATE cover_letter=VALUES(cover_letter);

package com.college.internship.service;

import com.college.internship.dao.StudentDao;
import com.college.internship.dao.StudentDaoImpl;
import com.college.internship.dao.UserDao;
import com.college.internship.dao.UserDaoImpl;
import com.college.internship.exception.AuthenticationException;
import com.college.internship.exception.DatabaseException;
import com.college.internship.exception.ValidationException;
import com.college.internship.model.Student;
import com.college.internship.model.User;
import com.college.internship.util.PasswordUtil;

import java.util.Optional;

/**
 * Service class handling Authentication and Account Registration logic.
 */
public class AuthService {
    private final UserDao userDao;
    private final StudentDao studentDao;

    public AuthService() {
        this.userDao = new UserDaoImpl();
        this.studentDao = new StudentDaoImpl();
    }

    public AuthService(UserDao userDao, StudentDao studentDao) {
        this.userDao = userDao;
        this.studentDao = studentDao;
    }

    /**
     * Authenticates a user given email and unhashed password.
     */
    public User login(String email, String password) throws AuthenticationException, DatabaseException {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isEmpty() || password == null || password.isEmpty()) {
            throw new AuthenticationException("Email and Password are required fields.");
        }

        Optional<User> userOpt = userDao.findByEmail(normalizedEmail);
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Invalid email or password.");
        }

        User user = userOpt.get();
        boolean isValid = PasswordUtil.verifyPassword(password, user.getPasswordHash(), user.getSalt());

        if (!isValid) {
            throw new AuthenticationException("Invalid email or password.");
        }

        return user;
    }

    /**
     * Registers a new Student user into the system.
     */
    public Student registerStudent(String name, String email, String password, String department,
                                   String phone, double cgpa, int graduationYear, String bio)
            throws ValidationException, DatabaseException {

        // Validations
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Full Name is required.");
        }
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isEmpty() || !normalizedEmail.contains("@")) {
            throw new ValidationException("Valid email address is required.");
        }
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
        if (department == null || department.trim().isEmpty()) {
            throw new ValidationException("Department / Branch is required.");
        }
        if (cgpa < 0.0 || cgpa > 10.0) {
            throw new ValidationException("CGPA must be between 0.0 and 10.0.");
        }

        if (userDao.emailExists(normalizedEmail)) {
            throw new ValidationException("Email address is already registered.");
        }

        // Security: Generate unique salt & password hash
        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(password, salt);

        Student student = new Student();
        student.setName(name.trim());
        student.setEmail(normalizedEmail);
        student.setPasswordHash(passwordHash);
        student.setSalt(salt);
        student.setDepartment(department.trim());
        student.setPhone(phone != null ? phone.trim() : "");
        student.setCgpa(cgpa);
        student.setGraduationYear(graduationYear);
        student.setBio(bio != null ? bio.trim() : "");

        int generatedUserId = userDao.saveUser(student);
        student.setId(generatedUserId);
        studentDao.saveProfile(student);

        return student;
    }

    private String normalizeEmail(String email) {
        if (email == null) return "";
        String trimmed = email.trim().toLowerCase();
        if (trimmed.startsWith("mailto:")) {
            trimmed = trimmed.substring(7).trim();
        }
        return trimmed;
    }
}

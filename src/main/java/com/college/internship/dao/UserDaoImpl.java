package com.college.internship.dao;

import com.college.internship.database.DatabaseConnection;
import com.college.internship.exception.DatabaseException;
import com.college.internship.model.Admin;
import com.college.internship.model.Student;
import com.college.internship.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * JDBC Implementation of UserDao interface.
 * Demonstrates PreparedStatements, Encapsulation, and Polymorphism.
 */
public class UserDaoImpl implements UserDao {

    @Override
    public Optional<User> findByEmail(String email) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(conn, rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by email: " + email, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(conn, rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public int saveUser(User user) throws DatabaseException {
        String sql = "INSERT INTO users (name, email, password_hash, salt, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getSalt());
            stmt.setString(5, user.getRole().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    user.setId(generatedId);
                    return generatedId;
                } else {
                    throw new DatabaseException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving user to database.", e);
        }
    }

    @Override
    public boolean emailExists(String email) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking email existence.", e);
        }
        return false;
    }

    private User mapResultSetToUser(Connection conn, ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password_hash");
        String salt = rs.getString("salt");
        String roleStr = rs.getString("role");
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdAtTs != null ? createdAtTs.toLocalDateTime() : LocalDateTime.now();

        User.Role role = User.Role.valueOf(roleStr);

        if (role == User.Role.STUDENT) {
            Student student = new Student(id, name, email, passwordHash, salt, createdAt, "", "", 0.0, 2025, "");
            loadStudentProfile(conn, student);
            return student;
        } else {
            return new Admin(id, name, email, passwordHash, salt, createdAt, "Training & Placement Cell");
        }
    }

    private void loadStudentProfile(Connection conn, Student student) {
        String sql = "SELECT * FROM student_profiles WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, student.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    student.setDepartment(rs.getString("department"));
                    student.setPhone(rs.getString("phone"));
                    student.setCgpa(rs.getDouble("cgpa"));
                    student.setGraduationYear(rs.getInt("graduation_year"));
                    student.setBio(rs.getString("bio"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Warning loading student profile: " + e.getMessage());
        }
    }
}

package com.college.internship.dao;

import com.college.internship.database.DatabaseConnection;
import com.college.internship.exception.DatabaseException;
import com.college.internship.model.Student;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC Implementation of StudentDao.
 */
public class StudentDaoImpl implements StudentDao {

    @Override
    public void saveProfile(Student student) throws DatabaseException {
        String sql = "INSERT INTO student_profiles (user_id, department, phone, cgpa, graduation_year, bio) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, student.getId());
            stmt.setString(2, student.getDepartment());
            stmt.setString(3, student.getPhone());
            stmt.setDouble(4, student.getCgpa());
            stmt.setInt(5, student.getGraduationYear());
            stmt.setString(6, student.getBio());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error saving student profile.", e);
        }
    }

    @Override
    public void updateProfile(Student student) throws DatabaseException {
        String userSql = "UPDATE users SET name = ? WHERE id = ?";
        String profileSql = "UPDATE student_profiles SET department = ?, phone = ?, cgpa = ?, graduation_year = ?, bio = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement userStmt = conn.prepareStatement(userSql);
                 PreparedStatement profileStmt = conn.prepareStatement(profileSql)) {

                userStmt.setString(1, student.getName());
                userStmt.setInt(2, student.getId());
                userStmt.executeUpdate();

                profileStmt.setString(1, student.getDepartment());
                profileStmt.setString(2, student.getPhone());
                profileStmt.setDouble(3, student.getCgpa());
                profileStmt.setInt(4, student.getGraduationYear());
                profileStmt.setString(5, student.getBio());
                profileStmt.setInt(6, student.getId());
                profileStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabaseException("Error updating student profile.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database transaction failure during profile update.", e);
        }
    }

    @Override
    public Optional<Student> getStudentById(int userId) throws DatabaseException {
        String sql = "SELECT u.id, u.name, u.email, u.password_hash, u.salt, u.created_at, " +
                "sp.department, sp.phone, sp.cgpa, sp.graduation_year, sp.bio " +
                "FROM users u " +
                "LEFT JOIN student_profiles sp ON u.id = sp.user_id " +
                "WHERE u.id = ? AND u.role = 'STUDENT'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving student profile for ID: " + userId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Student> getAllStudents() throws DatabaseException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.email, u.password_hash, u.salt, u.created_at, " +
                "sp.department, sp.phone, sp.cgpa, sp.graduation_year, sp.bio " +
                "FROM users u " +
                "LEFT JOIN student_profiles sp ON u.id = sp.user_id " +
                "WHERE u.role = 'STUDENT' ORDER BY u.name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving list of students.", e);
        }
        return list;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password_hash");
        String salt = rs.getString("salt");
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdAtTs != null ? createdAtTs.toLocalDateTime() : LocalDateTime.now();

        String department = rs.getString("department");
        String phone = rs.getString("phone");
        double cgpa = rs.getDouble("cgpa");
        int graduationYear = rs.getInt("graduation_year");
        String bio = rs.getString("bio");

        return new Student(id, name, email, passwordHash, salt, createdAt,
                department != null ? department : "N/A",
                phone != null ? phone : "",
                cgpa,
                graduationYear != 0 ? graduationYear : 2025,
                bio != null ? bio : "");
    }
}

package com.college.internship.dao;

import com.college.internship.database.DatabaseConnection;
import com.college.internship.exception.DatabaseException;
import com.college.internship.model.Application;
import com.college.internship.model.Internship;
import com.college.internship.model.Student;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of ApplicationDao.
 */
public class ApplicationDaoImpl implements ApplicationDao {

    @Override
    public int apply(Application application) throws DatabaseException {
        String sql = "INSERT INTO applications (student_id, internship_id, cover_letter, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, application.getStudentId());
            stmt.setInt(2, application.getInternshipId());
            stmt.setString(3, application.getCoverLetter());
            stmt.setString(4, application.getStatus().name());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    application.setId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error submitting internship application.", e);
        }
        return -1;
    }

    @Override
    public boolean hasStudentApplied(int studentId, int internshipId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM applications WHERE student_id = ? AND internship_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, internshipId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking application status.", e);
        }
        return false;
    }

    @Override
    public List<Application> getApplicationsByStudent(int studentId) throws DatabaseException {
        String sql = "SELECT a.id as app_id, a.cover_letter, a.status as app_status, a.admin_feedback, a.applied_at, " +
                "i.id as internship_id, i.title, i.company, i.location, i.stipend, i.deadline, i.status as int_status " +
                "FROM applications a " +
                "JOIN internships i ON a.internship_id = i.id " +
                "WHERE a.student_id = ? ORDER BY a.applied_at DESC";

        List<Application> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Application app = new Application();
                    app.setId(rs.getInt("app_id"));
                    app.setStudentId(studentId);
                    app.setInternshipId(rs.getInt("internship_id"));
                    app.setCoverLetter(rs.getString("cover_letter"));
                    app.setStatus(Application.Status.valueOf(rs.getString("app_status")));
                    app.setAdminFeedback(rs.getString("admin_feedback"));
                    Timestamp ts = rs.getTimestamp("applied_at");
                    app.setAppliedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());

                    Internship internship = new Internship();
                    internship.setId(rs.getInt("internship_id"));
                    internship.setTitle(rs.getString("title"));
                    internship.setCompany(rs.getString("company"));
                    internship.setLocation(rs.getString("location"));
                    internship.setStipend(rs.getString("stipend"));
                    Date deadlineDate = rs.getDate("deadline");
                    internship.setDeadline(deadlineDate != null ? deadlineDate.toLocalDate() : LocalDate.now());
                    internship.setStatus(Internship.Status.valueOf(rs.getString("int_status")));

                    app.setInternship(internship);
                    list.add(app);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching student applications.", e);
        }
        return list;
    }

    @Override
    public List<Application> getApplicationsByInternship(int internshipId) throws DatabaseException {
        return fetchFullApplications("WHERE a.internship_id = ?", internshipId);
    }

    @Override
    public List<Application> getAllApplications() throws DatabaseException {
        return fetchFullApplications("", null);
    }

    @Override
    public void updateApplicationStatus(int applicationId, Application.Status status, String adminFeedback) throws DatabaseException {
        String sql = "UPDATE applications SET status = ?, admin_feedback = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setString(2, adminFeedback);
            stmt.setInt(3, applicationId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating application decision.", e);
        }
    }

    private List<Application> fetchFullApplications(String whereClause, Integer filterId) throws DatabaseException {
        String sql = "SELECT a.id as app_id, a.cover_letter, a.status as app_status, a.admin_feedback, a.applied_at, " +
                "u.id as student_id, u.name as student_name, u.email as student_email, " +
                "sp.department, sp.phone, sp.cgpa, sp.graduation_year, " +
                "i.id as internship_id, i.title, i.company, i.location, i.stipend, i.deadline, i.status as int_status " +
                "FROM applications a " +
                "JOIN users u ON a.student_id = u.id " +
                "LEFT JOIN student_profiles sp ON u.id = sp.user_id " +
                "JOIN internships i ON a.internship_id = i.id " +
                whereClause + " ORDER BY a.applied_at DESC";

        List<Application> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (filterId != null) {
                stmt.setInt(1, filterId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Application app = new Application();
                    app.setId(rs.getInt("app_id"));
                    app.setStudentId(rs.getInt("student_id"));
                    app.setInternshipId(rs.getInt("internship_id"));
                    app.setCoverLetter(rs.getString("cover_letter"));
                    app.setStatus(Application.Status.valueOf(rs.getString("app_status")));
                    app.setAdminFeedback(rs.getString("admin_feedback"));
                    Timestamp ts = rs.getTimestamp("applied_at");
                    app.setAppliedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());

                    Student student = new Student();
                    student.setId(rs.getInt("student_id"));
                    student.setName(rs.getString("student_name"));
                    student.setEmail(rs.getString("student_email"));
                    student.setDepartment(rs.getString("department") != null ? rs.getString("department") : "N/A");
                    student.setPhone(rs.getString("phone"));
                    student.setCgpa(rs.getDouble("cgpa"));
                    student.setGraduationYear(rs.getInt("graduation_year"));

                    Internship internship = new Internship();
                    internship.setId(rs.getInt("internship_id"));
                    internship.setTitle(rs.getString("title"));
                    internship.setCompany(rs.getString("company"));
                    internship.setLocation(rs.getString("location"));
                    internship.setStipend(rs.getString("stipend"));
                    Date deadlineDate = rs.getDate("deadline");
                    internship.setDeadline(deadlineDate != null ? deadlineDate.toLocalDate() : LocalDate.now());
                    internship.setStatus(Internship.Status.valueOf(rs.getString("int_status")));

                    app.setStudent(student);
                    app.setInternship(internship);
                    list.add(app);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching applications list.", e);
        }
        return list;
    }
}

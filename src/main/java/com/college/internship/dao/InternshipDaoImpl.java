package com.college.internship.dao;

import com.college.internship.database.DatabaseConnection;
import com.college.internship.exception.DatabaseException;
import com.college.internship.model.Internship;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC Implementation of InternshipDao interface.
 */
public class InternshipDaoImpl implements InternshipDao {

    @Override
    public List<Internship> getAllInternships() throws DatabaseException {
        return fetchInternships("SELECT * FROM internships ORDER BY created_at DESC");
    }

    @Override
    public List<Internship> getOpenInternships() throws DatabaseException {
        return fetchInternships("SELECT * FROM internships WHERE status = 'OPEN' AND deadline >= CURRENT_DATE() ORDER BY created_at DESC");
    }

    @Override
    public Optional<Internship> getInternshipById(int id) throws DatabaseException {
        String sql = "SELECT * FROM internships WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInternship(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving internship ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public int addInternship(Internship internship) throws DatabaseException {
        String sql = "INSERT INTO internships (title, company, location, description, requirements, stipend, deadline, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, internship.getTitle());
            stmt.setString(2, internship.getCompany());
            stmt.setString(3, internship.getLocation());
            stmt.setString(4, internship.getDescription());
            stmt.setString(5, internship.getRequirements());
            stmt.setString(6, internship.getStipend());
            stmt.setDate(7, Date.valueOf(internship.getDeadline()));
            stmt.setString(8, internship.getStatus().name());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    internship.setId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error creating internship listing.", e);
        }
        return -1;
    }

    @Override
    public void updateInternship(Internship internship) throws DatabaseException {
        String sql = "UPDATE internships SET title = ?, company = ?, location = ?, description = ?, requirements = ?, stipend = ?, deadline = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, internship.getTitle());
            stmt.setString(2, internship.getCompany());
            stmt.setString(3, internship.getLocation());
            stmt.setString(4, internship.getDescription());
            stmt.setString(5, internship.getRequirements());
            stmt.setString(6, internship.getStipend());
            stmt.setDate(7, Date.valueOf(internship.getDeadline()));
            stmt.setString(8, internship.getStatus().name());
            stmt.setInt(9, internship.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating internship listing.", e);
        }
    }

    @Override
    public void deleteInternship(int id) throws DatabaseException {
        String sql = "DELETE FROM internships WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting internship ID: " + id, e);
        }
    }

    @Override
    public List<Internship> searchInternships(String keyword) throws DatabaseException {
        String sql = "SELECT * FROM internships WHERE LOWER(title) LIKE ? OR LOWER(company) LIKE ? OR LOWER(location) LIKE ? OR LOWER(requirements) LIKE ? ORDER BY created_at DESC";
        List<Internship> list = new ArrayList<>();
        String searchPattern = "%" + keyword.toLowerCase() + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToInternship(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching internships with keyword: " + keyword, e);
        }
        return list;
    }

    private List<Internship> fetchInternships(String sql) throws DatabaseException {
        List<Internship> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToInternship(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching internships.", e);
        }
        return list;
    }

    private Internship mapResultSetToInternship(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String company = rs.getString("company");
        String location = rs.getString("location");
        String description = rs.getString("description");
        String requirements = rs.getString("requirements");
        String stipend = rs.getString("stipend");
        Date deadlineDate = rs.getDate("deadline");
        LocalDate deadline = deadlineDate != null ? deadlineDate.toLocalDate() : LocalDate.now().plusMonths(1);
        String statusStr = rs.getString("status");
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdAtTs != null ? createdAtTs.toLocalDateTime() : LocalDateTime.now();

        return new Internship(id, title, company, location, description, requirements, stipend, deadline,
                Internship.Status.valueOf(statusStr), createdAt);
    }
}

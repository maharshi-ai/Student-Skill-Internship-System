package com.college.internship.dao;

import com.college.internship.database.DatabaseConnection;
import com.college.internship.exception.DatabaseException;
import com.college.internship.model.Skill;
import com.college.internship.model.StudentSkill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of SkillDao interface.
 */
public class SkillDaoImpl implements SkillDao {

    @Override
    public List<Skill> getAllSkills() throws DatabaseException {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT * FROM skills ORDER BY name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Skill(rs.getInt("id"), rs.getString("name"), rs.getString("category")));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching master skills list.", e);
        }
        return list;
    }

    @Override
    public List<StudentSkill> getStudentSkills(int studentId) throws DatabaseException {
        List<StudentSkill> list = new ArrayList<>();
        String sql = "SELECT ss.id as student_skill_id, ss.student_id, ss.proficiency_level, " +
                "s.id as skill_id, s.name as skill_name, s.category as skill_category " +
                "FROM student_skills ss " +
                "JOIN skills s ON ss.skill_id = s.id " +
                "WHERE ss.student_id = ? ORDER BY s.name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Skill skill = new Skill(rs.getInt("skill_id"), rs.getString("skill_name"), rs.getString("skill_category"));
                    StudentSkill.ProficiencyLevel level = StudentSkill.ProficiencyLevel.valueOf(rs.getString("proficiency_level"));
                    list.add(new StudentSkill(rs.getInt("student_skill_id"), studentId, skill, level));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching student skills for student ID: " + studentId, e);
        }
        return list;
    }

    @Override
    public void addStudentSkill(int studentId, int skillId, StudentSkill.ProficiencyLevel level) throws DatabaseException {
        String sql = "INSERT INTO student_skills (student_id, skill_id, proficiency_level) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE proficiency_level = VALUES(proficiency_level)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, skillId);
            stmt.setString(3, level.name());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error adding skill to student profile.", e);
        }
    }

    @Override
    public void removeStudentSkill(int studentSkillId) throws DatabaseException {
        String sql = "DELETE FROM student_skills WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentSkillId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error removing skill from student profile.", e);
        }
    }

    @Override
    public Skill addMasterSkill(Skill skill) throws DatabaseException {
        String sql = "INSERT INTO skills (name, category) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, skill.getName());
            stmt.setString(2, skill.getCategory());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    skill.setId(rs.getInt(1));
                }
            }
            return skill;
        } catch (SQLException e) {
            throw new DatabaseException("Error adding new master skill.", e);
        }
    }
}

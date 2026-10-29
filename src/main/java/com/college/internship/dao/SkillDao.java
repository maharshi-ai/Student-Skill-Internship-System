package com.college.internship.dao;

import com.college.internship.exception.DatabaseException;
import com.college.internship.model.Skill;
import com.college.internship.model.StudentSkill;

import java.util.List;

/**
 * DAO Interface for managing Master Skills and Student Skills.
 */
public interface SkillDao {
    List<Skill> getAllSkills() throws DatabaseException;
    List<StudentSkill> getStudentSkills(int studentId) throws DatabaseException;
    void addStudentSkill(int studentId, int skillId, StudentSkill.ProficiencyLevel level) throws DatabaseException;
    void removeStudentSkill(int studentSkillId) throws DatabaseException;
    Skill addMasterSkill(Skill skill) throws DatabaseException;
}

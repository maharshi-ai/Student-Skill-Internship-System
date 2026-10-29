package com.college.internship.model;

/**
 * Domain model representing a Student's proficiency in a specific skill.
 */
public class StudentSkill {
    public enum ProficiencyLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }

    private int id;
    private int studentId;
    private Skill skill;
    private ProficiencyLevel proficiencyLevel;

    public StudentSkill() {}

    public StudentSkill(int id, int studentId, Skill skill, ProficiencyLevel proficiencyLevel) {
        this.id = id;
        this.studentId = studentId;
        this.skill = skill;
        this.proficiencyLevel = proficiencyLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public ProficiencyLevel getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(ProficiencyLevel proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }
}

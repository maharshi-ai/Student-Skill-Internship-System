package com.college.internship.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Student user in the system.
 * Demonstrates Inheritance (extends User) and Collections usage.
 */
public class Student extends User {
    private String department;
    private String phone;
    private double cgpa;
    private int graduationYear;
    private String bio;
    private List<StudentSkill> skills;

    public Student() {
        super();
        this.setRole(Role.STUDENT);
        this.skills = new ArrayList<>();
    }

    public Student(int id, String name, String email, String passwordHash, String salt,
                   LocalDateTime createdAt, String department, String phone,
                   double cgpa, int graduationYear, String bio) {
        super(id, name, email, passwordHash, salt, Role.STUDENT, createdAt);
        this.department = department;
        this.phone = phone;
        this.cgpa = cgpa;
        this.graduationYear = graduationYear;
        this.bio = bio;
        this.skills = new ArrayList<>();
    }

    @Override
    public String getDashboardTitle() {
        return "Student Dashboard - " + getName();
    }

    @Override
    public String getRoleName() {
        return "Student";
    }

    // Encapsulated Getters and Setters
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(int graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<StudentSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<StudentSkill> skills) {
        this.skills = skills;
    }
}

package com.college.internship.model;

import java.time.LocalDateTime;

/**
 * Represents an Administrator user in the system.
 * Demonstrates Inheritance (extends User).
 */
public class Admin extends User {
    private String adminDepartment;

    public Admin() {
        super();
        this.setRole(Role.ADMIN);
        this.adminDepartment = "Training & Placement Cell";
    }

    public Admin(int id, String name, String email, String passwordHash, String salt,
                 LocalDateTime createdAt, String adminDepartment) {
        super(id, name, email, passwordHash, salt, Role.ADMIN, createdAt);
        this.adminDepartment = adminDepartment;
    }

    @Override
    public String getDashboardTitle() {
        return "Placement Administrator Console - " + getName();
    }

    @Override
    public String getRoleName() {
        return "Administrator";
    }

    public String getAdminDepartment() {
        return adminDepartment;
    }

    public void setAdminDepartment(String adminDepartment) {
        this.adminDepartment = adminDepartment;
    }
}

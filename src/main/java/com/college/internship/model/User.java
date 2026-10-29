package com.college.internship.model;

import java.time.LocalDateTime;

/**
 * Abstract Base Class representing a System User.
 * Demonstrates Abstraction, Encapsulation, and Polymorphism.
 */
public abstract class User {
    public enum Role {
        STUDENT, ADMIN
    }

    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String salt;
    private Role role;
    private LocalDateTime createdAt;

    // Default Constructor
    public User() {}

    // Parameterized Constructor
    public User(int id, String name, String email, String passwordHash, String salt, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Abstract methods to be implemented by child classes (Abstraction & Polymorphism)
    public abstract String getDashboardTitle();
    public abstract String getRoleName();

    // Polymorphic display method
    public String getWelcomeMessage() {
        return "Welcome, " + name + " (" + getRoleName() + ")";
    }

    // Encapsulation: Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

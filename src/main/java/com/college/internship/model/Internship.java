package com.college.internship.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain model representing an Internship Listing.
 */
public class Internship {
    public enum Status {
        OPEN, CLOSED
    }

    private int id;
    private String title;
    private String company;
    private String location;
    private String description;
    private String requirements;
    private String stipend;
    private LocalDate deadline;
    private Status status;
    private LocalDateTime createdAt;

    public Internship() {
        this.status = Status.OPEN;
    }

    public Internship(int id, String title, String company, String location,
                      String description, String requirements, String stipend,
                      LocalDate deadline, Status status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.description = description;
        this.requirements = requirements;
        this.stipend = stipend;
        this.deadline = deadline;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getStipend() {
        return stipend;
    }

    public void setStipend(String stipend) {
        this.stipend = stipend;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

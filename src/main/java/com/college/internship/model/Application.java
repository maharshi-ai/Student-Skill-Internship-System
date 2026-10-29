package com.college.internship.model;

import java.time.LocalDateTime;

/**
 * Domain model representing an Internship Application.
 */
public class Application {
    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }

    private int id;
    private int studentId;
    private Student student;
    private int internshipId;
    private Internship internship;
    private String coverLetter;
    private Status status;
    private String adminFeedback;
    private LocalDateTime appliedAt;

    public Application() {
        this.status = Status.PENDING;
    }

    public Application(int id, int studentId, Student student, int internshipId,
                       Internship internship, String coverLetter, Status status,
                       String adminFeedback, LocalDateTime appliedAt) {
        this.id = id;
        this.studentId = studentId;
        this.student = student;
        this.internshipId = internshipId;
        this.internship = internship;
        this.coverLetter = coverLetter;
        this.status = status;
        this.adminFeedback = adminFeedback;
        this.appliedAt = appliedAt;
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

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(int internshipId) {
        this.internshipId = internshipId;
    }

    public Internship getInternship() {
        return internship;
    }

    public void setInternship(Internship internship) {
        this.internship = internship;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAdminFeedback() {
        return adminFeedback;
    }

    public void setAdminFeedback(String adminFeedback) {
        this.adminFeedback = adminFeedback;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
}

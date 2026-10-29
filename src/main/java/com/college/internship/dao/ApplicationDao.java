package com.college.internship.dao;

import com.college.internship.exception.DatabaseException;
import com.college.internship.model.Application;

import java.util.List;

/**
 * DAO Interface for Application entity operations.
 */
public interface ApplicationDao {
    int apply(Application application) throws DatabaseException;
    boolean hasStudentApplied(int studentId, int internshipId) throws DatabaseException;
    List<Application> getApplicationsByStudent(int studentId) throws DatabaseException;
    List<Application> getApplicationsByInternship(int internshipId) throws DatabaseException;
    List<Application> getAllApplications() throws DatabaseException;
    void updateApplicationStatus(int applicationId, Application.Status status, String adminFeedback) throws DatabaseException;
}

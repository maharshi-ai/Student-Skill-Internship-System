package com.college.internship.service;

import com.college.internship.dao.ApplicationDao;
import com.college.internship.dao.ApplicationDaoImpl;
import com.college.internship.exception.DatabaseException;
import com.college.internship.exception.ValidationException;
import com.college.internship.model.Application;

import java.util.List;

/**
 * Service class handling Internship Application workflow.
 */
public class ApplicationService {
    private final ApplicationDao applicationDao;

    public ApplicationService() {
        this.applicationDao = new ApplicationDaoImpl();
    }

    public ApplicationService(ApplicationDao applicationDao) {
        this.applicationDao = applicationDao;
    }

    public void applyForInternship(int studentId, int internshipId, String coverLetter)
            throws ValidationException, DatabaseException {

        if (coverLetter == null || coverLetter.trim().isEmpty()) {
            throw new ValidationException("Please provide a cover letter or statement of interest.");
        }

        if (applicationDao.hasStudentApplied(studentId, internshipId)) {
            throw new ValidationException("You have already submitted an application for this internship.");
        }

        Application app = new Application();
        app.setStudentId(studentId);
        app.setInternshipId(internshipId);
        app.setCoverLetter(coverLetter.trim());
        app.setStatus(Application.Status.PENDING);

        applicationDao.apply(app);
    }

    public boolean hasStudentApplied(int studentId, int internshipId) throws DatabaseException {
        return applicationDao.hasStudentApplied(studentId, internshipId);
    }

    public List<Application> getStudentApplications(int studentId) throws DatabaseException {
        return applicationDao.getApplicationsByStudent(studentId);
    }

    public List<Application> getAllApplications() throws DatabaseException {
        return applicationDao.getAllApplications();
    }

    public List<Application> getApplicationsByInternship(int internshipId) throws DatabaseException {
        return applicationDao.getApplicationsByInternship(internshipId);
    }

    public void updateStatus(int applicationId, Application.Status status, String adminFeedback)
            throws ValidationException, DatabaseException {

        if (status == null) {
            throw new ValidationException("Status cannot be null.");
        }

        applicationDao.updateApplicationStatus(applicationId, status, adminFeedback != null ? adminFeedback.trim() : "");
    }
}

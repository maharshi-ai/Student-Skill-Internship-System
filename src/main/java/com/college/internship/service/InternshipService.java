package com.college.internship.service;

import com.college.internship.dao.InternshipDao;
import com.college.internship.dao.InternshipDaoImpl;
import com.college.internship.exception.DatabaseException;
import com.college.internship.exception.ValidationException;
import com.college.internship.model.Internship;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class handling Internship listings business logic.
 */
public class InternshipService {
    private final InternshipDao internshipDao;

    public InternshipService() {
        this.internshipDao = new InternshipDaoImpl();
    }

    public InternshipService(InternshipDao internshipDao) {
        this.internshipDao = internshipDao;
    }

    public List<Internship> getAllInternships() throws DatabaseException {
        return internshipDao.getAllInternships();
    }

    public List<Internship> getOpenInternships() throws DatabaseException {
        return internshipDao.getOpenInternships();
    }

    public List<Internship> searchInternships(String keyword) throws DatabaseException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getOpenInternships();
        }
        return internshipDao.searchInternships(keyword.trim());
    }

    public int createInternship(String title, String company, String location,
                                String description, String requirements, String stipend,
                                LocalDate deadline) throws ValidationException, DatabaseException {

        validateInternshipData(title, company, location, description, requirements, deadline);

        Internship internship = new Internship();
        internship.setTitle(title.trim());
        internship.setCompany(company.trim());
        internship.setLocation(location.trim());
        internship.setDescription(description.trim());
        internship.setRequirements(requirements.trim());
        internship.setStipend(stipend != null ? stipend.trim() : "Unpaid");
        internship.setDeadline(deadline);
        internship.setStatus(Internship.Status.OPEN);

        return internshipDao.addInternship(internship);
    }

    public void updateInternship(Internship internship) throws ValidationException, DatabaseException {
        validateInternshipData(internship.getTitle(), internship.getCompany(), internship.getLocation(),
                internship.getDescription(), internship.getRequirements(), internship.getDeadline());

        internshipDao.updateInternship(internship);
    }

    public void deleteInternship(int id) throws DatabaseException {
        internshipDao.deleteInternship(id);
    }

    private void validateInternshipData(String title, String company, String location,
                                       String description, String requirements, LocalDate deadline)
            throws ValidationException {

        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Internship Title is required.");
        }
        if (company == null || company.trim().isEmpty()) {
            throw new ValidationException("Company Name is required.");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new ValidationException("Location is required.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Job Description is required.");
        }
        if (requirements == null || requirements.trim().isEmpty()) {
            throw new ValidationException("Requirements field is required.");
        }
        if (deadline == null || deadline.isBefore(LocalDate.now())) {
            throw new ValidationException("Application deadline must be a future date.");
        }
    }
}

package com.college.internship.dao;

import com.college.internship.exception.DatabaseException;
import com.college.internship.model.Internship;

import java.util.List;
import java.util.Optional;

/**
 * DAO Interface for Internship entity operations.
 */
public interface InternshipDao {
    List<Internship> getAllInternships() throws DatabaseException;
    List<Internship> getOpenInternships() throws DatabaseException;
    Optional<Internship> getInternshipById(int id) throws DatabaseException;
    int addInternship(Internship internship) throws DatabaseException;
    void updateInternship(Internship internship) throws DatabaseException;
    void deleteInternship(int id) throws DatabaseException;
    List<Internship> searchInternships(String keyword) throws DatabaseException;
}

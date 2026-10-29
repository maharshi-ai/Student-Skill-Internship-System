package com.college.internship.dao;

import com.college.internship.exception.DatabaseException;
import com.college.internship.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * DAO Interface for Student profile operations.
 */
public interface StudentDao {
    void saveProfile(Student student) throws DatabaseException;
    void updateProfile(Student student) throws DatabaseException;
    Optional<Student> getStudentById(int userId) throws DatabaseException;
    List<Student> getAllStudents() throws DatabaseException;
}

package com.college.internship.dao;

import com.college.internship.exception.DatabaseException;
import com.college.internship.model.User;

import java.util.Optional;

/**
 * DAO Interface for User entity operations.
 * Demonstrates Abstraction and Interface concept.
 */
public interface UserDao {
    Optional<User> findByEmail(String email) throws DatabaseException;
    Optional<User> findById(int id) throws DatabaseException;
    int saveUser(User user) throws DatabaseException;
    boolean emailExists(String email) throws DatabaseException;
}

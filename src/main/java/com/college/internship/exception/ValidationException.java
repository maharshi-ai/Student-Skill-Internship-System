package com.college.internship.exception;

/**
 * Custom Exception thrown when user input validation fails.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}

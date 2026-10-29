package com.college.internship.exception;

/**
 * Custom Exception thrown when authentication or authorization fails.
 */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}

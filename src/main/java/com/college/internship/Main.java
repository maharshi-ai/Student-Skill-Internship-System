package com.college.internship;

import com.college.internship.database.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Student Skill & Internship Management System (Spring Boot Web).
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        try {
            DatabaseInitializer.initializeDatabase();
        } catch (Exception e) {
            System.err.println("Database initialization warning: " + e.getMessage());
        }
        SpringApplication.run(Main.class, args);
    }
}

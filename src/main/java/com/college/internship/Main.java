package com.college.internship;

import com.college.internship.database.DatabaseInitializer;
import com.college.internship.ui.LoginFrame;
import com.college.internship.ui.Theme;

import javax.swing.*;

/**
 * Entry point for the Student Skill & Internship Management System.
 */
public class Main {

    public static void main(String[] args) {
        // Step 1: Initialize Modern Swing Theme (FlatLaf)
        Theme.setupTheme();

        // Step 2: Attempt Database Connection & Auto-Initialization
        System.out.println("=================================================================");
        System.out.println("Starting Student Skill & Internship Management System...");
        System.out.println("=================================================================");
        
        try {
            DatabaseInitializer.initializeDatabase();
        } catch (Exception e) {
            System.err.println("Database initialization warning: " + e.getMessage());
        }

        // Step 3: Launch Graphical User Interface on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}

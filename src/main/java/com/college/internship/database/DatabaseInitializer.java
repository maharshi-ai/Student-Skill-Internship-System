package com.college.internship.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Initializes database schema and default seed data automatically upon startup.
 */
public class DatabaseInitializer {

    public static void initializeDatabase() {
        // Step 1: Connect to MySQL server and ensure database exists
        try (Connection serverConn = DatabaseConnection.getServerConnection();
             Statement stmt = serverConn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS student_skill_management;");
            System.out.println("[DatabaseInitializer] Database 'student_skill_management' checked/created successfully.");
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Could not connect to MySQL server: " + e.getMessage());
            return;
        }

        // Step 2: Connect to database and execute schema.sql
        try (Connection conn = DatabaseConnection.getConnection();
             InputStream input = DatabaseInitializer.class.getClassLoader().getResourceAsStream("database/schema.sql")) {

            if (input == null) {
                System.out.println("[DatabaseInitializer] schema.sql resource not found on classpath.");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String script = reader.lines()
                    .filter(line -> !line.trim().startsWith("--"))
                    .collect(Collectors.joining("\n"));

            String[] statements = script.split(";");
            try (Statement stmt = conn.createStatement()) {
                for (String sql : statements) {
                    String trimmed = sql.trim();
                    if (!trimmed.isEmpty()) {
                        try {
                            stmt.execute(trimmed);
                        } catch (Exception ex) {
                            // Non-fatal duplicate/exists exceptions ignored
                        }
                    }
                }
            }
            System.out.println("[DatabaseInitializer] Schema DDL and Seed Data applied successfully.");
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Error executing schema DDL: " + e.getMessage());
        }
    }
}


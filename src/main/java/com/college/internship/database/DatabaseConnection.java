package com.college.internship.database;

import com.college.internship.exception.DatabaseException;
import com.college.internship.util.ConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection manager handling MySQL JDBC connectivity.
 */
public class DatabaseConnection {

    private static String url;
    private static String user;
    private static String password;
    private static boolean driverLoaded = false;

    static {
        try {
            String driver = ConfigUtil.get("db.driver", "com.mysql.cj.jdbc.Driver");
            Class.forName(driver);
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver class not found: " + e.getMessage());
        }

        url = ConfigUtil.get("db.url", "jdbc:mysql://localhost:3306/student_skill_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        user = ConfigUtil.get("db.user", "root");
        password = ConfigUtil.get("db.password", "root");
    }

    public static Connection getConnection() throws DatabaseException {
        if (!driverLoaded) {
            throw new DatabaseException("MySQL JDBC Driver is not loaded.");
        }
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to MySQL database at " + url + ". Please ensure MySQL service is running and credentials in db.properties are correct.", e);
        }
    }

    /**
     * Obtains connection to MySQL server without specific database name.
     */
    public static Connection getServerConnection() throws DatabaseException {
        if (!driverLoaded) {
            throw new DatabaseException("MySQL JDBC Driver is not loaded.");
        }
        try {
            String serverUrl = url;
            if (serverUrl.contains("/student_skill_management")) {
                serverUrl = serverUrl.replace("/student_skill_management", "/");
            }
            return DriverManager.getConnection(serverUrl, user, password);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to MySQL server. Please verify MySQL service status and credentials in db.properties.", e);
        }
    }

    /**
     * Connection test method returning true if database is accessible.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }
}


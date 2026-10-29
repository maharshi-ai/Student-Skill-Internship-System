package com.college.internship.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading application properties from db.properties or environment overrides.
 */
public class ConfigUtil {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load db.properties file. Using default or environment settings.");
        }
    }

    public static String get(String key, String defaultValue) {
        // First check System Environment variables
        String envKey = key.toUpperCase().replace('.', '_');
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal;
        }

        // Then check System properties (-Dkey=value)
        String sysVal = System.getProperty(key);
        if (sysVal != null && !sysVal.trim().isEmpty()) {
            return sysVal;
        }

        // Finally return value from db.properties or default
        return properties.getProperty(key, defaultValue);
    }
}

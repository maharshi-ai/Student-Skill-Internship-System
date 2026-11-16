package com.college.internship.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Password hashing utility.
 *
 * New passwords are stored using BCrypt. Existing SHA-256 + salt records
 * remain verifiable so deployments can upgrade without locking out users.
 */
public final class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder(12);

    private PasswordUtil() {
        // Utility class
    }

    /** Generates a random legacy salt for compatibility with the existing schema. */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    /** Hashes a new password using BCrypt; the legacy salt parameter is unused. */
    public static String hashPassword(String password, String saltHex) {
        if (password == null) {
            throw new IllegalArgumentException("Password must not be null.");
        }
        return BCRYPT.encode(password);
    }

    /**
     * Verifies BCrypt hashes and legacy SHA-256 hashes.
     * Legacy hashes are retained for existing demo/student accounts; new
     * registrations automatically use BCrypt.
     */
    public static boolean verifyPassword(String password, String storedHash, String saltHex) {
        if (password == null || storedHash == null || storedHash.isBlank()) {
            return false;
        }

        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            return BCRYPT.matches(password, storedHash);
        }

        // Backward compatibility for existing SHA-256 + salt database records.
        if (saltHex == null || saltHex.isBlank()) {
            return false;
        }
        try {
            byte[] saltBytes = HexFormat.of().parseHex(saltHex);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(saltBytes);
            byte[] computed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            byte[] expected = HexFormat.of().parseHex(storedHash);
            return MessageDigest.isEqual(computed, expected);
        } catch (IllegalArgumentException | NoSuchAlgorithmException ex) {
            return false;
        }
    }
}

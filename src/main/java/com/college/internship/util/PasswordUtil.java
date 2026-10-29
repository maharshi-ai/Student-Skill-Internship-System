package com.college.internship.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Utility class for hashing and verifying passwords securely using SHA-256 with Salt.
 */
public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a 16-byte random salt as a hex string.
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    /**
     * Hashes the password concatenated with the salt using SHA-256.
     */
    public static String hashPassword(String password, String saltHex) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] saltBytes = HexFormat.of().parseHex(saltHex);
            md.update(saltBytes);
            byte[] hashedPassword = md.digest(password.getBytes());
            return HexFormat.of().formatHex(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies if an unhashed password matches the stored hash and salt.
     */
    public static boolean verifyPassword(String password, String storedHash, String saltHex) {
        String computedHash = hashPassword(password, saltHex);
        return computedHash.equalsIgnoreCase(storedHash);
    }
}

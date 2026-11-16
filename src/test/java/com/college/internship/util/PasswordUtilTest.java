package com.college.internship.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void newPasswordHashesAreBcryptAndCanBeVerified() {
        String password = "Strong-demo-password-123";
        String hash = PasswordUtil.hashPassword(password, PasswordUtil.generateSalt());

        assertTrue(hash.startsWith("$2"));
        assertTrue(PasswordUtil.verifyPassword(password, hash, null));
        assertFalse(PasswordUtil.verifyPassword("wrong-password", hash, null));
    }

    @Test
    void existingSeededSha256AdminPasswordRemainsVerifiable() {
        String legacyHash = "4b43b8feb9f9ea668e96682130c2d73c9139dc9673b473955a7f305c870d44c9";
        String legacySalt = "a1b2c3d4e5f67890";

        assertTrue(PasswordUtil.verifyPassword("admin123", legacyHash, legacySalt));
        assertFalse(PasswordUtil.verifyPassword("wrong-password", legacyHash, legacySalt));
    }

    @Test
    void malformedLegacyHashFailsClosed() {
        assertFalse(PasswordUtil.verifyPassword("password", "not-a-hash", "not-hex"));
    }
}

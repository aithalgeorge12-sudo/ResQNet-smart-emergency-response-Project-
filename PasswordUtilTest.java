package com.resqnet.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    public void testHashAndVerifyPassword() {
        String raw = "EmergencySecret123!";
        String hash = PasswordUtil.hashPassword(raw);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"));
        assertTrue(PasswordUtil.checkPassword(raw, hash));
        assertFalse(PasswordUtil.checkPassword("WrongPassword", hash));
    }

    @Test
    public void testPlainFallback() {
        assertTrue(PasswordUtil.checkPassword("plain123", "plain123"));
        assertFalse(PasswordUtil.checkPassword("wrong", "plain123"));
    }
}

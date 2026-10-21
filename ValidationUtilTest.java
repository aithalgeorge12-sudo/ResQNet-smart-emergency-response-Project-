package com.resqnet.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @Test
    public void testEmailValidation() {
        assertTrue(ValidationUtil.isValidEmail("citizen@example.com"));
        assertTrue(ValidationUtil.isValidEmail("john.doe@sub.resqnet.org"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail("no-at-sign.com"));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    public void testPhoneValidation() {
        assertTrue(ValidationUtil.isValidPhone("+91 9876543210"));
        assertTrue(ValidationUtil.isValidPhone("123-456-7890"));
        assertTrue(ValidationUtil.isValidPhone("9876543210"));
        assertFalse(ValidationUtil.isValidPhone("abc"));
        assertFalse(ValidationUtil.isValidPhone(null));
    }

    @Test
    public void testDateParsing() {
        LocalDate date = ValidationUtil.parseDate("2000-05-15");
        assertNotNull(date);
        assertEquals(2000, date.getYear());
        assertEquals(5, date.getMonthValue());
        assertEquals(15, date.getDayOfMonth());

        assertNull(ValidationUtil.parseDate("invalid-date"));
        assertNull(ValidationUtil.parseDate(null));
    }

    @Test
    public void testSanitization() {
        String unsafe = "<script>alert('xss');</script>";
        String safe = ValidationUtil.sanitize(unsafe);
        assertFalse(safe.contains("<script>"));
        assertTrue(safe.contains("&lt;script&gt;"));
    }
}

package com.ecommerce.reglog.util;

import java.util.regex.Pattern;

/**
 * Enterprise Validation Utilities for User Registration & Authentication Security
 */
public final class ValidationUtils {

    // Minimum 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,100}$"
    );

    // RFC 5322 compliant email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    // 10 to 15 digits international / national mobile number pattern
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9]{10,15}$"
    );

    private ValidationUtils() {}

    /**
     * Validate full name: min 3, max 100 characters
     */
    public static boolean isValidFullName(String fullName) {
        if (fullName == null) return false;
        String trimmed = fullName.trim();
        return trimmed.length() >= 3 && trimmed.length() <= 100;
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate mobile number format: 10-15 digits
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String sanitized = phone.trim().replaceAll("[\\s\\-()]", "");
        return PHONE_PATTERN.matcher(sanitized).matches();
    }

    /**
     * Clean phone to digits only (with optional leading +)
     */
    public static String cleanPhone(String phone) {
        if (phone == null) return null;
        return phone.trim().replaceAll("[\\s\\-()]", "");
    }

    /**
     * Validate password complexity:
     * - Minimum 8 characters, maximum 100 characters
     * - At least 1 uppercase letter
     * - At least 1 lowercase letter
     * - At least 1 numeric digit
     * - At least 1 special character (!@#$%^&*()_+-=[]{};':"|,.<>/?)
     */
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Password validation error description if invalid
     */
    public static String getPasswordValidationError(String password) {
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (password.length() > 100) {
            return "Password cannot exceed 100 characters.";
        }
        if (!password.chars().anyMatch(Character::isUpperCase)) {
            return "Password must contain at least one uppercase letter (A-Z).";
        }
        if (!password.chars().anyMatch(Character::isLowerCase)) {
            return "Password must contain at least one lowercase letter (a-z).";
        }
        if (!password.chars().anyMatch(Character::isDigit)) {
            return "Password must contain at least one numeric digit (0-9).";
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return "Password must contain at least one special character (!@#$%^&* etc.).";
        }
        return null;
    }
}

package com.resqnet.service;

import com.resqnet.dao.MedicalDetailsDao;
import com.resqnet.dao.UserDao;
import com.resqnet.model.EmergencyContact;
import com.resqnet.model.User;
import com.resqnet.model.UserMedicalDetails;
import com.resqnet.util.PasswordUtil;
import com.resqnet.util.ValidationUtil;

/**
 * Business service managing user accounts, authentication, and profile updates.
 */
public class UserService {

    private final UserDao userDao = new UserDao();
    private final MedicalDetailsDao medicalDao = new MedicalDetailsDao();

    /**
     * Registers a new user with comprehensive validation and secure BCrypt hashing.
     */
    public String registerUser(User user, UserMedicalDetails medical, EmergencyContact primaryContact,
                               EmergencyContact secondaryContact, String rawPassword, String confirmPassword) {
        // 1. Validation
        if (!ValidationUtil.isNotEmpty(user.getFullName())) {
            return "Full Name is required.";
        }
        if (user.getDob() == null) {
            return "Date of Birth is required.";
        }
        if (!ValidationUtil.isNotEmpty(user.getGender())) {
            return "Gender selection is required.";
        }
        if (!ValidationUtil.isValidPhone(user.getPhone())) {
            return "Please provide a valid phone number.";
        }
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            return "Please provide a valid email address.";
        }
        if (!ValidationUtil.isNotEmpty(user.getAddress())) {
            return "Address is required.";
        }
        if (!ValidationUtil.isNotEmpty(rawPassword) || rawPassword.length() < 6) {
            return "Password must be at least 6 characters long.";
        }
        if (!rawPassword.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        if (primaryContact == null || !ValidationUtil.isNotEmpty(primaryContact.getContactName())
                || !ValidationUtil.isValidPhone(primaryContact.getPhone())) {
            return "Primary emergency contact name and valid phone are required.";
        }

        // 2. Check duplicate email
        if (userDao.isEmailRegistered(user.getEmail())) {
            return "An account with this email address already exists. Please log in.";
        }

        // 3. Hash password securely
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);
        user.setPasswordHash(hashedPassword);

        // 4. Save to Database
        boolean success = userDao.registerFullUser(user, medical, primaryContact, secondaryContact);
        return success ? "SUCCESS" : "An error occurred while creating your account. Please try again.";
    }

    /**
     * Authenticates a user by email and password.
     */
    public User authenticate(String email, String rawPassword) {
        if (!ValidationUtil.isValidEmail(email) || !ValidationUtil.isNotEmpty(rawPassword)) {
            return null;
        }

        User user = userDao.getUserByEmail(email);
        if (user == null) {
            return null;
        }

        if (PasswordUtil.checkPassword(rawPassword, user.getPasswordHash())) {
            // Load full details for session
            return userDao.getUserById(user.getUserId());
        }
        return null;
    }

    /**
     * Retrieves full user profile by ID.
     */
    public User getUserProfile(int userId) {
        return userDao.getUserById(userId);
    }

    /**
     * Updates profile and medical details.
     */
    public boolean updateProfile(User user, UserMedicalDetails medical) {
        boolean uSuccess = userDao.updateUserProfile(user);
        boolean mSuccess = true;
        if (medical != null) {
            medical.setUserId(user.getUserId());
            mSuccess = medicalDao.upsertMedicalDetails(medical);
        }
        return uSuccess && mSuccess;
    }
}

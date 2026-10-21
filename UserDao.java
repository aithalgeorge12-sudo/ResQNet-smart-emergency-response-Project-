package com.resqnet.dao;

import com.resqnet.model.EmergencyContact;
import com.resqnet.model.User;
import com.resqnet.model.UserMedicalDetails;
import com.resqnet.util.DatabaseConnection;

import java.sql.*;

/**
 * Data Access Object for User accounts and combined registration transactions.
 */
public class UserDao {

    /**
     * Atomically registers a new user with their medical details and emergency contacts.
     */
    public boolean registerFullUser(User user, UserMedicalDetails medical, EmergencyContact primaryContact, EmergencyContact secondaryContact) {
        String insertUserSql = "INSERT INTO users (full_name, dob, gender, phone, email, address, password_hash) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertMedicalSql = "INSERT INTO user_medical_details (user_id, blood_group, diseases, medical_conditions, allergies, current_medications, consulting_doctor_name, consulting_doctor_phone, other_medical_info) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertContactSql = "INSERT INTO emergency_contacts (user_id, contact_name, relationship, phone, is_primary) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Transaction begins

            int generatedUserId;
            // 1. Insert User
            try (PreparedStatement uStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                uStmt.setString(1, user.getFullName());
                uStmt.setDate(2, Date.valueOf(user.getDob()));
                uStmt.setString(3, user.getGender());
                uStmt.setString(4, user.getPhone());
                uStmt.setString(5, user.getEmail().trim().toLowerCase());
                uStmt.setString(6, user.getAddress());
                uStmt.setString(7, user.getPasswordHash());
                uStmt.executeUpdate();

                try (ResultSet rs = uStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedUserId = rs.getInt(1);
                        user.setUserId(generatedUserId);
                    } else {
                        throw new SQLException("Failed to retrieve generated user ID.");
                    }
                }
            }

            // 2. Insert Medical Details
            if (medical != null) {
                try (PreparedStatement mStmt = conn.prepareStatement(insertMedicalSql)) {
                    mStmt.setInt(1, generatedUserId);
                    mStmt.setString(2, medical.getBloodGroup() != null ? medical.getBloodGroup() : "Unknown");
                    mStmt.setString(3, medical.getDiseases() != null && !medical.getDiseases().isEmpty() ? medical.getDiseases() : "None");
                    mStmt.setString(4, medical.getMedicalConditions() != null && !medical.getMedicalConditions().isEmpty() ? medical.getMedicalConditions() : "None");
                    mStmt.setString(5, medical.getAllergies() != null && !medical.getAllergies().isEmpty() ? medical.getAllergies() : "None");
                    mStmt.setString(6, medical.getCurrentMedications() != null ? medical.getCurrentMedications() : "None");
                    mStmt.setString(7, medical.getConsultingDoctorName() != null ? medical.getConsultingDoctorName() : "N/A");
                    mStmt.setString(8, medical.getConsultingDoctorPhone() != null ? medical.getConsultingDoctorPhone() : "N/A");
                    mStmt.setString(9, medical.getOtherMedicalInfo() != null ? medical.getOtherMedicalInfo() : "");
                    mStmt.executeUpdate();
                }
            }

            // 3. Insert Primary Contact
            if (primaryContact != null && primaryContact.getContactName() != null && !primaryContact.getContactName().trim().isEmpty()) {
                try (PreparedStatement cStmt = conn.prepareStatement(insertContactSql)) {
                    cStmt.setInt(1, generatedUserId);
                    cStmt.setString(2, primaryContact.getContactName());
                    cStmt.setString(3, primaryContact.getRelationship());
                    cStmt.setString(4, primaryContact.getPhone());
                    cStmt.setBoolean(5, true); // Primary
                    cStmt.executeUpdate();
                }
            }

            // 4. Insert Optional Secondary Contact
            if (secondaryContact != null && secondaryContact.getContactName() != null && !secondaryContact.getContactName().trim().isEmpty()) {
                try (PreparedStatement cStmt2 = conn.prepareStatement(insertContactSql)) {
                    cStmt2.setInt(1, generatedUserId);
                    cStmt2.setString(2, secondaryContact.getContactName());
                    cStmt2.setString(3, secondaryContact.getRelationship());
                    cStmt2.setString(4, secondaryContact.getPhone());
                    cStmt2.setBoolean(5, false); // Secondary
                    cStmt2.executeUpdate();
                }
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Checks if an email is already registered in the system.
     */
    public boolean isEmailRegistered(String email) {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds a user by email address.
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds a user by user_id and loads their medical details and primary contact.
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapUser(rs);
                    // Fetch medical details
                    MedicalDetailsDao medDao = new MedicalDetailsDao();
                    user.setMedicalDetails(medDao.getByUserId(userId));

                    // Fetch primary contact
                    EmergencyContactDao contactDao = new EmergencyContactDao();
                    user.setPrimaryContact(contactDao.getPrimaryContact(userId));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates core user profile information.
     */
    public boolean updateUserProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, dob = ?, gender = ?, phone = ?, address = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setDate(2, Date.valueOf(user.getDob()));
            stmt.setString(3, user.getGender());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getAddress());
            stmt.setInt(6, user.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates user's hashed password.
     */
    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setFullName(rs.getString("full_name"));
        Date dob = rs.getDate("dob");
        if (dob != null) {
            u.setDob(dob.toLocalDate());
        }
        u.setGender(rs.getString("gender"));
        u.setPhone(rs.getString("phone"));
        u.setEmail(rs.getString("email"));
        u.setAddress(rs.getString("address"));
        u.setPasswordHash(rs.getString("password_hash"));
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) {
            u.setCreatedAt(ct.toLocalDateTime());
        }
        Timestamp ut = rs.getTimestamp("updated_at");
        if (ut != null) {
            u.setUpdatedAt(ut.toLocalDateTime());
        }
        return u;
    }
}

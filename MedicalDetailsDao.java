package com.resqnet.dao;

import com.resqnet.model.UserMedicalDetails;
import com.resqnet.util.DatabaseConnection;

import java.sql.*;

/**
 * Data Access Object for User Medical Details.
 */
public class MedicalDetailsDao {

    /**
     * Retrieves medical details for a specific user.
     */
    public UserMedicalDetails getByUserId(int userId) {
        String sql = "SELECT * FROM user_medical_details WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapDetails(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Upserts (inserts or updates) medical details for a user.
     */
    public boolean upsertMedicalDetails(UserMedicalDetails d) {
        String sql = "INSERT INTO user_medical_details (user_id, blood_group, diseases, medical_conditions, allergies, current_medications, consulting_doctor_name, consulting_doctor_phone, other_medical_info) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "blood_group = VALUES(blood_group), " +
                     "diseases = VALUES(diseases), " +
                     "medical_conditions = VALUES(medical_conditions), " +
                     "allergies = VALUES(allergies), " +
                     "current_medications = VALUES(current_medications), " +
                     "consulting_doctor_name = VALUES(consulting_doctor_name), " +
                     "consulting_doctor_phone = VALUES(consulting_doctor_phone), " +
                     "other_medical_info = VALUES(other_medical_info)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, d.getUserId());
            stmt.setString(2, d.getBloodGroup());
            stmt.setString(3, d.getDiseases());
            stmt.setString(4, d.getMedicalConditions());
            stmt.setString(5, d.getAllergies());
            stmt.setString(6, d.getCurrentMedications());
            stmt.setString(7, d.getConsultingDoctorName());
            stmt.setString(8, d.getConsultingDoctorPhone());
            stmt.setString(9, d.getOtherMedicalInfo());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private UserMedicalDetails mapDetails(ResultSet rs) throws SQLException {
        UserMedicalDetails d = new UserMedicalDetails();
        d.setMedicalId(rs.getInt("medical_id"));
        d.setUserId(rs.getInt("user_id"));
        d.setBloodGroup(rs.getString("blood_group"));
        d.setDiseases(rs.getString("diseases"));
        d.setMedicalConditions(rs.getString("medical_conditions"));
        d.getAllergies();
        d.setAllergies(rs.getString("allergies"));
        d.setCurrentMedications(rs.getString("current_medications"));
        d.setConsultingDoctorName(rs.getString("consulting_doctor_name"));
        d.setConsultingDoctorPhone(rs.getString("consulting_doctor_phone"));
        d.setOtherMedicalInfo(rs.getString("other_medical_info"));
        Timestamp ut = rs.getTimestamp("updated_at");
        if (ut != null) {
            d.setUpdatedAt(ut.toLocalDateTime());
        }
        return d;
    }
}

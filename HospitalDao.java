package com.resqnet.dao;

import com.resqnet.model.Hospital;
import com.resqnet.model.HospitalStaff;
import com.resqnet.util.DatabaseConnection;
import com.resqnet.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Hospitals and Hospital Staff.
 */
public class HospitalDao {

    /**
     * Lists all registered emergency hospitals.
     */
    public List<Hospital> getAllHospitals() {
        List<Hospital> list = new ArrayList<>();
        String sql = "SELECT * FROM hospitals ORDER BY hospital_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapHospital(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Finds a hospital by its ID.
     */
    public Hospital getHospitalById(int hospitalId) {
        String sql = "SELECT * FROM hospitals WHERE hospital_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hospitalId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapHospital(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Authenticates hospital staff via staff code or email and password.
     */
    public HospitalStaff authenticateStaff(String identifier, String plainPassword) {
        String sql = "SELECT s.*, h.hospital_name, h.emergency_helpline, h.ambulance_phone, h.address AS hosp_address, h.city " +
                     "FROM hospital_staff s " +
                     "JOIN hospitals h ON s.hospital_id = h.hospital_id " +
                     "WHERE LOWER(s.staff_code) = LOWER(?) OR LOWER(s.email) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, identifier.trim());
            stmt.setString(2, identifier.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (PasswordUtil.checkPassword(plainPassword, storedHash)) {
                        HospitalStaff staff = mapStaff(rs);
                        Hospital h = new Hospital();
                        h.setHospitalId(rs.getInt("hospital_id"));
                        h.setHospitalName(rs.getString("hospital_name"));
                        h.setEmergencyHelpline(rs.getString("emergency_helpline"));
                        h.setAmbulancePhone(rs.getString("ambulance_phone"));
                        h.setAddress(rs.getString("hosp_address"));
                        h.setCity(rs.getString("city"));
                        staff.setHospital(h);
                        return staff;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Hospital mapHospital(ResultSet rs) throws SQLException {
        Hospital h = new Hospital();
        h.setHospitalId(rs.getInt("hospital_id"));
        h.setHospitalCode(rs.getString("hospital_code"));
        h.setHospitalName(rs.getString("hospital_name"));
        h.setAddress(rs.getString("address"));
        h.setCity(rs.getString("city"));
        h.setEmergencyHelpline(rs.getString("emergency_helpline"));
        h.setAmbulancePhone(rs.getString("ambulance_phone"));
        h.setDoctorInCharge(rs.getString("doctor_in_charge"));
        h.setSampleData(rs.getBoolean("is_sample_data"));
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) {
            h.setCreatedAt(ct.toLocalDateTime());
        }
        return h;
    }

    private HospitalStaff mapStaff(ResultSet rs) throws SQLException {
        HospitalStaff s = new HospitalStaff();
        s.setStaffId(rs.getInt("staff_id"));
        s.setHospitalId(rs.getInt("hospital_id"));
        s.setStaffCode(rs.getString("staff_code"));
        s.setStaffName(rs.getString("staff_name"));
        s.setEmail(rs.getString("email"));
        s.setPasswordHash(rs.getString("password_hash"));
        s.setRole(rs.getString("role"));
        s.setSampleData(rs.getBoolean("is_sample_data"));
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) {
            s.setCreatedAt(ct.toLocalDateTime());
        }
        return s;
    }
}

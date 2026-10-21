package com.resqnet.dao;

import com.resqnet.model.EmergencyContact;
import com.resqnet.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Emergency Contacts.
 */
public class EmergencyContactDao {

    /**
     * Gets all contacts registered for a user.
     */
    public List<EmergencyContact> getContactsByUserId(int userId) {
        List<EmergencyContact> list = new ArrayList<>();
        String sql = "SELECT * FROM emergency_contacts WHERE user_id = ? ORDER BY is_primary DESC, contact_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapContact(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Gets the primary emergency contact for a user.
     */
    public EmergencyContact getPrimaryContact(int userId) {
        String sql = "SELECT * FROM emergency_contacts WHERE user_id = ? AND is_primary = 1 LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapContact(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If no contact is explicitly marked primary, fetch the first available contact
        List<EmergencyContact> all = getContactsByUserId(userId);
        return all.isEmpty() ? null : all.get(0);
    }

    /**
     * Adds an emergency contact for a user.
     */
    public boolean addContact(EmergencyContact c) {
        String sql = "INSERT INTO emergency_contacts (user_id, contact_name, relationship, phone, is_primary) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, c.getUserId());
            stmt.setString(2, c.getContactName());
            stmt.setString(3, c.getRelationship());
            stmt.setString(4, c.getPhone());
            stmt.setBoolean(5, c.isPrimary());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing contact, verifying user ownership.
     */
    public boolean updateContact(EmergencyContact c) {
        String sql = "UPDATE emergency_contacts SET contact_name = ?, relationship = ?, phone = ?, is_primary = ? WHERE contact_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getContactName());
            stmt.setString(2, c.getRelationship());
            stmt.setString(3, c.getPhone());
            stmt.setBoolean(4, c.isPrimary());
            stmt.setInt(5, c.getContactId());
            stmt.setInt(6, c.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a contact by ID, verifying user ownership.
     */
    public boolean deleteContact(int contactId, int userId) {
        String sql = "DELETE FROM emergency_contacts WHERE contact_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, contactId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private EmergencyContact mapContact(ResultSet rs) throws SQLException {
        EmergencyContact c = new EmergencyContact();
        c.setContactId(rs.getInt("contact_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setContactName(rs.getString("contact_name"));
        c.setRelationship(rs.getString("relationship"));
        c.setPhone(rs.getString("phone"));
        c.setPrimary(rs.getBoolean("is_primary"));
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) {
            c.setCreatedAt(ct.toLocalDateTime());
        }
        return c;
    }
}

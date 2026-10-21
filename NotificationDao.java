package com.resqnet.dao;

import com.resqnet.model.NotificationRecord;
import com.resqnet.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for in-website emergency notifications and audit logs.
 */
public class NotificationDao {

    /**
     * Logs an emergency notification dispatch record.
     */
    public boolean logNotification(NotificationRecord n) {
        String sql = "INSERT INTO notification_records (report_id, recipient_type, recipient_name, recipient_phone, message_content, delivery_status, gateway_note) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, n.getReportId());
            stmt.setString(2, n.getRecipientType());
            stmt.setString(3, n.getRecipientName());
            stmt.setString(4, n.getRecipientPhone());
            stmt.setString(5, n.getMessageContent());
            stmt.setString(6, n.getDeliveryStatus() != null ? n.getDeliveryStatus() : "In-Website Logged");
            stmt.setString(7, n.getGatewayNote());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    n.setNotificationId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets all notification records triggered for a specific emergency report.
     */
    public List<NotificationRecord> getNotificationsByReportId(int reportId) {
        List<NotificationRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM notification_records WHERE report_id = ? ORDER BY sent_at ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    NotificationRecord n = new NotificationRecord();
                    n.setNotificationId(rs.getInt("notification_id"));
                    n.setReportId(rs.getInt("report_id"));
                    n.setRecipientType(rs.getString("recipient_type"));
                    n.setRecipientName(rs.getString("recipient_name"));
                    n.setRecipientPhone(rs.getString("recipient_phone"));
                    n.setMessageContent(rs.getString("message_content"));
                    n.setDeliveryStatus(rs.getString("delivery_status"));
                    n.setGatewayNote(rs.getString("gateway_note"));
                    Timestamp st = rs.getTimestamp("sent_at");
                    if (st != null) {
                        n.setSentAt(st.toLocalDateTime());
                    }
                    list.add(n);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

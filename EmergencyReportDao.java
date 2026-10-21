package com.resqnet.dao;

import com.resqnet.model.EmergencyReport;
import com.resqnet.model.Hospital;
import com.resqnet.model.ReportAttachment;
import com.resqnet.model.User;
import com.resqnet.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object for Emergency Incident Reports.
 */
public class EmergencyReportDao {

    /**
     * Creates a new emergency report and returns the generated report ID.
     */
    public int createReport(EmergencyReport report) {
        String sql = "INSERT INTO emergency_reports (user_id, report_tracking_id, category, sub_category, severity, description, incident_location, latitude, longitude, injured_condition, vehicles_involved, is_anonymous, status, assigned_hospital_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Generate unique human-readable tracking ID: RQ-YYYYMMDD-XXXX
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String trackingId = "RQ-" + datePart + "-" + randomPart;
        report.setReportTrackingId(trackingId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (report.getUserId() != null && report.getUserId() > 0) {
                stmt.setInt(1, report.getUserId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, trackingId);
            stmt.setString(3, report.getCategory());
            stmt.setString(4, report.getSubCategory());
            stmt.setString(5, report.getSeverity() != null ? report.getSeverity() : "High");
            stmt.setString(6, report.getDescription());
            stmt.setString(7, report.getIncidentLocation());
            if (report.getLatitude() != null) {
                stmt.setBigDecimal(8, report.getLatitude());
            } else {
                stmt.setNull(8, Types.DECIMAL);
            }
            if (report.getLongitude() != null) {
                stmt.setBigDecimal(9, report.getLongitude());
            } else {
                stmt.setNull(9, Types.DECIMAL);
            }
            stmt.setString(10, report.getInjuredCondition() != null ? report.getInjuredCondition() : "Unknown");
            stmt.setString(11, report.getVehiclesInvolved() != null ? report.getVehiclesInvolved() : "N/A");
            stmt.setBoolean(12, report.isAnonymous());
            stmt.setString(13, report.getStatus() != null ? report.getStatus() : "Reported");
            if (report.getAssignedHospitalId() != null && report.getAssignedHospitalId() > 0) {
                stmt.setInt(14, report.getAssignedHospitalId());
            } else {
                stmt.setNull(14, Types.INTEGER);
            }

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int reportId = rs.getInt(1);
                    report.setReportId(reportId);
                    return reportId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Adds photo attachment metadata.
     */
    public boolean addAttachment(ReportAttachment a) {
        String sql = "INSERT INTO report_attachments (report_id, file_name, file_path, file_type, file_size_bytes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, a.getReportId());
            stmt.setString(2, a.getFileName());
            stmt.setString(3, a.getFilePath());
            stmt.setString(4, a.getFileType());
            stmt.setLong(5, a.getFileSizeBytes());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    a.setAttachmentId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all reports for a specific logged-in user.
     */
    public List<EmergencyReport> getReportsByUserId(int userId) {
        List<EmergencyReport> list = new ArrayList<>();
        String sql = "SELECT r.*, h.hospital_name " +
                     "FROM emergency_reports r " +
                     "LEFT JOIN hospitals h ON r.assigned_hospital_id = h.hospital_id " +
                     "WHERE r.user_id = ? " +
                     "ORDER BY r.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReport(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves all reports across the system (for hospital portal & responders).
     */
    public List<EmergencyReport> getAllReports() {
        List<EmergencyReport> list = new ArrayList<>();
        String sql = "SELECT r.*, h.hospital_name, u.full_name AS reporter_name, u.phone AS reporter_phone " +
                     "FROM emergency_reports r " +
                     "LEFT JOIN hospitals h ON r.assigned_hospital_id = h.hospital_id " +
                     "LEFT JOIN users u ON r.user_id = u.user_id " +
                     "ORDER BY r.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                EmergencyReport report = mapReport(rs);
                if (rs.getString("reporter_name") != null) {
                    User u = new User();
                    u.setFullName(rs.getString("reporter_name"));
                    u.setPhone(rs.getString("reporter_phone"));
                    report.setReporter(u);
                }
                list.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves a single report by ID along with its attachments and notifications.
     */
    public EmergencyReport getReportById(int reportId) {
        String sql = "SELECT r.*, h.hospital_name, h.emergency_helpline, h.ambulance_phone, " +
                     "u.full_name AS reporter_name, u.phone AS reporter_phone, u.email AS reporter_email " +
                     "FROM emergency_reports r " +
                     "LEFT JOIN hospitals h ON r.assigned_hospital_id = h.hospital_id " +
                     "LEFT JOIN users u ON r.user_id = u.user_id " +
                     "WHERE r.report_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    EmergencyReport report = mapReport(rs);
                    if (rs.getString("reporter_name") != null) {
                        User u = new User();
                        u.setFullName(rs.getString("reporter_name"));
                        u.setPhone(rs.getString("reporter_phone"));
                        u.setEmail(rs.getString("reporter_email"));
                        report.setReporter(u);
                    }
                    // Load attachments
                    report.setAttachments(getAttachmentsByReportId(reportId));
                    // Load notifications
                    NotificationDao notifDao = new NotificationDao();
                    report.setNotifications(notifDao.getNotificationsByReportId(reportId));
                    return report;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates status of an emergency report (e.g. Ambulance Dispatched, Resolved).
     */
    public boolean updateReportStatus(int reportId, String newStatus, Integer assignedHospitalId) {
        String sql = "UPDATE emergency_reports SET status = ?, assigned_hospital_id = COALESCE(?, assigned_hospital_id) WHERE report_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            if (assignedHospitalId != null && assignedHospitalId > 0) {
                stmt.setInt(2, assignedHospitalId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, reportId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets photo attachments for a report.
     */
    public List<ReportAttachment> getAttachmentsByReportId(int reportId) {
        List<ReportAttachment> list = new ArrayList<>();
        String sql = "SELECT * FROM report_attachments WHERE report_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReportAttachment a = new ReportAttachment();
                    a.setAttachmentId(rs.getInt("attachment_id"));
                    a.setReportId(rs.getInt("report_id"));
                    a.setFileName(rs.getString("file_name"));
                    a.setFilePath(rs.getString("file_path"));
                    a.setFileType(rs.getString("file_type"));
                    a.setFileSizeBytes(rs.getLong("file_size_bytes"));
                    Timestamp ut = rs.getTimestamp("uploaded_at");
                    if (ut != null) {
                        a.setUploadedAt(ut.toLocalDateTime());
                    }
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private EmergencyReport mapReport(ResultSet rs) throws SQLException {
        EmergencyReport r = new EmergencyReport();
        r.setReportId(rs.getInt("report_id"));
        int uid = rs.getInt("user_id");
        if (!rs.wasNull()) {
            r.setUserId(uid);
        }
        r.setReportTrackingId(rs.getString("report_tracking_id"));
        r.setCategory(rs.getString("category"));
        r.setSubCategory(rs.getString("sub_category"));
        r.setSeverity(rs.getString("severity"));
        r.setDescription(rs.getString("description"));
        r.setIncidentLocation(rs.getString("incident_location"));
        r.setLatitude(rs.getBigDecimal("latitude"));
        r.setLongitude(rs.getBigDecimal("longitude"));
        r.setInjuredCondition(rs.getString("injured_condition"));
        r.setVehiclesInvolved(rs.getString("vehicles_involved"));
        r.setAnonymous(rs.getBoolean("is_anonymous"));
        r.setStatus(rs.getString("status"));
        int hid = rs.getInt("assigned_hospital_id");
        if (!rs.wasNull()) {
            r.setAssignedHospitalId(hid);
            Hospital h = new Hospital();
            h.setHospitalId(hid);
            try {
                h.setHospitalName(rs.getString("hospital_name"));
            } catch (SQLException ignored) {}
            r.setAssignedHospital(h);
        }
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) {
            r.setCreatedAt(ct.toLocalDateTime());
        }
        Timestamp ut = rs.getTimestamp("updated_at");
        if (ut != null) {
            r.setUpdatedAt(ut.toLocalDateTime());
        }
        return r;
    }
}

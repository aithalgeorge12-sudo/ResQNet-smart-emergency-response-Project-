package com.resqnet.service;

import com.resqnet.dao.EmergencyReportDao;
import com.resqnet.dao.HospitalDao;
import com.resqnet.model.EmergencyReport;
import com.resqnet.model.Hospital;
import com.resqnet.model.ReportAttachment;
import com.resqnet.util.ValidationUtil;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Service orchestrating emergency reporting across all six incident types,
 * photo validation, file storage, and notification dispatch.
 */
public class EmergencyService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private final EmergencyReportDao reportDao = new EmergencyReportDao();
    private final HospitalDao hospitalDao = new HospitalDao();
    private final NotificationService notificationService = new NotificationService();

    /**
     * Submits an emergency report, handles optional photo upload, and triggers alerts.
     */
    public EmergencyReport submitReport(EmergencyReport report, Part filePart, String uploadDirPath) throws Exception {
        if (!ValidationUtil.isNotEmpty(report.getDescription())) {
            throw new IllegalArgumentException("Description of the emergency is required.");
        }
        if (!ValidationUtil.isNotEmpty(report.getIncidentLocation())) {
            throw new IllegalArgumentException("Incident location is required.");
        }

        // Auto-assign first sample hospital if none chosen
        if (report.getAssignedHospitalId() == null || report.getAssignedHospitalId() <= 0) {
            List<Hospital> hospitals = hospitalDao.getAllHospitals();
            if (!hospitals.isEmpty()) {
                report.setAssignedHospitalId(hospitals.get(0).getHospitalId());
            }
        }

        // Save report to database
        int reportId = reportDao.createReport(report);
        if (reportId <= 0) {
            throw new RuntimeException("Failed to persist emergency report to database.");
        }
        report.setReportId(reportId);

        // Handle optional photo attachment
        if (filePart != null && filePart.getSize() > 0) {
            handlePhotoUpload(reportId, filePart, uploadDirPath);
        }

        // Dispatch notifications
        notificationService.dispatchEmergencyAlerts(report);

        return reportDao.getReportById(reportId);
    }

    private void handlePhotoUpload(int reportId, Part filePart, String uploadDirPath) throws IOException {
        long size = filePart.getSize();
        if (size > MAX_FILE_SIZE) {
            System.err.println("Upload skipped: file exceeds 5MB limit.");
            return;
        }

        String submittedName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        if (submittedName == null || submittedName.trim().isEmpty()) {
            return;
        }

        // Validate extension
        String lower = submittedName.toLowerCase();
        if (!lower.endsWith(".jpg") && !lower.endsWith(".jpeg") && !lower.endsWith(".png") && !lower.endsWith(".webp")) {
            System.err.println("Upload skipped: unsupported file format. Only JPG, PNG, WEBP allowed.");
            return;
        }

        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Generate unique safe filename
        String extension = lower.substring(lower.lastIndexOf('.'));
        String safeFileName = "incident_" + reportId + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
        Path targetPath = Paths.get(uploadDirPath, safeFileName);

        try (InputStream in = filePart.getInputStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Save attachment metadata
        ReportAttachment attachment = new ReportAttachment(
                reportId,
                submittedName,
                "uploads/" + safeFileName,
                filePart.getContentType() != null ? filePart.getContentType() : "image/jpeg",
                size
        );
        reportDao.addAttachment(attachment);
    }

    public List<EmergencyReport> getReportsForUser(int userId) {
        return reportDao.getReportsByUserId(userId);
    }

    public EmergencyReport getReportById(int reportId) {
        return reportDao.getReportById(reportId);
    }

    public List<EmergencyReport> getAllReports() {
        return reportDao.getAllReports();
    }

    public boolean updateStatus(int reportId, String newStatus, Integer hospitalId) {
        return reportDao.updateReportStatus(reportId, newStatus, hospitalId);
    }
}

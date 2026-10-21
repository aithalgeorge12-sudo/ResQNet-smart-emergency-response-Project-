package com.resqnet.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Incident report entity representing Medical, Fire, Accident, Crime, Disaster, or Other emergency.
 */
public class EmergencyReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private int reportId;
    private Integer userId; // Nullable for anonymous/guest reports
    private String reportTrackingId;
    private String category; // Medical, Fire, Accident, Crime, Disaster, Other
    private String subCategory; // e.g. Heart Attack, Snake Bite, Kitchen Fire
    private String severity; // Low, Medium, High, Critical
    private String description;
    private String incidentLocation;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String injuredCondition;
    private String vehiclesInvolved;
    private boolean isAnonymous;
    private String status; // Reported, Hospital Notified, Ambulance Dispatched, etc.
    private Integer assignedHospitalId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Associated objects for UI rendering
    private User reporter;
    private Hospital assignedHospital;
    private List<ReportAttachment> attachments = new ArrayList<>();
    private List<NotificationRecord> notifications = new ArrayList<>();

    public EmergencyReport() {}

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getReportTrackingId() {
        return reportTrackingId;
    }

    public void setReportTrackingId(String reportTrackingId) {
        this.reportTrackingId = reportTrackingId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIncidentLocation() {
        return incidentLocation;
    }

    public void setIncidentLocation(String incidentLocation) {
        this.incidentLocation = incidentLocation;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getInjuredCondition() {
        return injuredCondition;
    }

    public void setInjuredCondition(String injuredCondition) {
        this.injuredCondition = injuredCondition;
    }

    public String getVehiclesInvolved() {
        return vehiclesInvolved;
    }

    public void setVehiclesInvolved(String vehiclesInvolved) {
        this.vehiclesInvolved = vehiclesInvolved;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAssignedHospitalId() {
        return assignedHospitalId;
    }

    public void setAssignedHospitalId(Integer assignedHospitalId) {
        this.assignedHospitalId = assignedHospitalId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public Hospital getAssignedHospital() {
        return assignedHospital;
    }

    public void setAssignedHospital(Hospital assignedHospital) {
        this.assignedHospital = assignedHospital;
    }

    public List<ReportAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ReportAttachment> attachments) {
        this.attachments = attachments;
    }

    public List<NotificationRecord> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationRecord> notifications) {
        this.notifications = notifications;
    }
}

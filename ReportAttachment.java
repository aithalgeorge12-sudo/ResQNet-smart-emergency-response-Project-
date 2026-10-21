package com.resqnet.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Stores photo attachment metadata associated with an emergency report.
 */
public class ReportAttachment implements Serializable {
    private static final long serialVersionUID = 1L;

    private int attachmentId;
    private int reportId;
    private String fileName;
    private String filePath;
    private String fileType;
    private long fileSizeBytes;
    private LocalDateTime uploadedAt;

    public ReportAttachment() {}

    public ReportAttachment(int reportId, String fileName, String filePath, String fileType, long fileSizeBytes) {
        this.reportId = reportId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSizeBytes = fileSizeBytes;
    }

    public int getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}

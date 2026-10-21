package com.resqnet.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Audit log of an emergency notification record.
 */
public class NotificationRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int notificationId;
    private int reportId;
    private String recipientType; // Emergency Contact, Hospital Authority, Police Control, Fire Dispatch
    private String recipientName;
    private String recipientPhone;
    private String messageContent;
    private String deliveryStatus; // In-Website Logged, Simulated Gateway Sent, Pending External Gateway
    private String gatewayNote;
    private LocalDateTime sentAt;

    public NotificationRecord() {}

    public NotificationRecord(int reportId, String recipientType, String recipientName, String recipientPhone,
                              String messageContent, String deliveryStatus, String gatewayNote) {
        this.reportId = reportId;
        this.recipientType = recipientType;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.messageContent = messageContent;
        this.deliveryStatus = deliveryStatus;
        this.gatewayNote = gatewayNote;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getGatewayNote() {
        return gatewayNote;
    }

    public void setGatewayNote(String gatewayNote) {
        this.gatewayNote = gatewayNote;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}

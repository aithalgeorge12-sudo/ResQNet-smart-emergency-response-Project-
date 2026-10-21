package com.resqnet.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Stores primary and secondary emergency contacts for a user.
 */
public class EmergencyContact implements Serializable {
    private static final long serialVersionUID = 1L;

    private int contactId;
    private int userId;
    private String contactName;
    private String relationship;
    private String phone;
    private boolean isPrimary;
    private LocalDateTime createdAt;

    public EmergencyContact() {}

    public EmergencyContact(int userId, String contactName, String relationship, String phone, boolean isPrimary) {
        this.userId = userId;
        this.contactName = contactName;
        this.relationship = relationship;
        this.phone = phone;
        this.isPrimary = isPrimary;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

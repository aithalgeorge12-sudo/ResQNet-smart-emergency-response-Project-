package com.resqnet.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Stores sensitive medical data, conditions, allergies, and doctor contact info.
 */
public class UserMedicalDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    private int medicalId;
    private int userId;
    private String bloodGroup;
    private String diseases;
    private String medicalConditions;
    private String allergies;
    private String currentMedications;
    private String consultingDoctorName;
    private String consultingDoctorPhone;
    private String otherMedicalInfo;
    private LocalDateTime updatedAt;

    public UserMedicalDetails() {}

    public UserMedicalDetails(int userId, String bloodGroup, String diseases, String medicalConditions,
                              String allergies, String currentMedications, String consultingDoctorName,
                              String consultingDoctorPhone, String otherMedicalInfo) {
        this.userId = userId;
        this.bloodGroup = bloodGroup;
        this.diseases = diseases;
        this.medicalConditions = medicalConditions;
        this.allergies = allergies;
        this.currentMedications = currentMedications;
        this.consultingDoctorName = consultingDoctorName;
        this.consultingDoctorPhone = consultingDoctorPhone;
        this.otherMedicalInfo = otherMedicalInfo;
    }

    public int getMedicalId() {
        return medicalId;
    }

    public void setMedicalId(int medicalId) {
        this.medicalId = medicalId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getDiseases() {
        return diseases;
    }

    public void setDiseases(String diseases) {
        this.diseases = diseases;
    }

    public String getMedicalConditions() {
        return medicalConditions;
    }

    public void setMedicalConditions(String medicalConditions) {
        this.medicalConditions = medicalConditions;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getCurrentMedications() {
        return currentMedications;
    }

    public void setCurrentMedications(String currentMedications) {
        this.currentMedications = currentMedications;
    }

    public String getConsultingDoctorName() {
        return consultingDoctorName;
    }

    public void setConsultingDoctorName(String consultingDoctorName) {
        this.consultingDoctorName = consultingDoctorName;
    }

    public String getConsultingDoctorPhone() {
        return consultingDoctorPhone;
    }

    public void setConsultingDoctorPhone(String consultingDoctorPhone) {
        this.consultingDoctorPhone = consultingDoctorPhone;
    }

    public String getOtherMedicalInfo() {
        return otherMedicalInfo;
    }

    public void setOtherMedicalInfo(String otherMedicalInfo) {
        this.otherMedicalInfo = otherMedicalInfo;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

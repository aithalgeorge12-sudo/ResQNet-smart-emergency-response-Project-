package com.resqnet.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Hospital / Trauma care center facility record.
 */
public class Hospital implements Serializable {
    private static final long serialVersionUID = 1L;

    private int hospitalId;
    private String hospitalCode;
    private String hospitalName;
    private String address;
    private String city;
    private String emergencyHelpline;
    private String ambulancePhone;
    private String doctorInCharge;
    private boolean isSampleData;
    private LocalDateTime createdAt;

    public Hospital() {}

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalCode() {
        return hospitalCode;
    }

    public void setHospitalCode(String hospitalCode) {
        this.hospitalCode = hospitalCode;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmergencyHelpline() {
        return emergencyHelpline;
    }

    public void setEmergencyHelpline(String emergencyHelpline) {
        this.emergencyHelpline = emergencyHelpline;
    }

    public String getAmbulancePhone() {
        return ambulancePhone;
    }

    public void setAmbulancePhone(String ambulancePhone) {
        this.ambulancePhone = ambulancePhone;
    }

    public String getDoctorInCharge() {
        return doctorInCharge;
    }

    public void setDoctorInCharge(String doctorInCharge) {
        this.doctorInCharge = doctorInCharge;
    }

    public boolean isSampleData() {
        return isSampleData;
    }

    public void setSampleData(boolean sampleData) {
        isSampleData = sampleData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

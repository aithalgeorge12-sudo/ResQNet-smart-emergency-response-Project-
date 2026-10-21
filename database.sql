-- =============================================================================
-- ResQNet: Smart Emergency Response & Assistance System
-- Database Schema & Sample Data
-- Database Name: resqnet_db
-- =============================================================================

CREATE DATABASE IF NOT EXISTS resqnet_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE resqnet_db;

-- Drop dependent tables in reverse order of foreign keys
DROP TABLE IF EXISTS notification_records;
DROP TABLE IF EXISTS report_attachments;
DROP TABLE IF EXISTS emergency_reports;
DROP TABLE IF EXISTS hospital_staff;
DROP TABLE IF EXISTS hospitals;
DROP TABLE IF EXISTS emergency_contacts;
DROP TABLE IF EXISTS user_medical_details;
DROP TABLE IF EXISTS users;

-- -----------------------------------------------------------------------------
-- 1. USERS TABLE
-- Stores primary account credentials and core profile data
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    dob DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    phone VARCHAR(25) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 2. USER MEDICAL DETAILS TABLE
-- Normalized table for sensitive health records and consulting doctor info
-- -----------------------------------------------------------------------------
CREATE TABLE user_medical_details (
    medical_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    blood_group VARCHAR(10) NOT NULL,
    diseases VARCHAR(255) DEFAULT 'None',
    medical_conditions VARCHAR(255) DEFAULT 'None',
    allergies VARCHAR(255) DEFAULT 'None',
    current_medications TEXT,
    consulting_doctor_name VARCHAR(150),
    consulting_doctor_phone VARCHAR(25),
    other_medical_info TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_medical_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 3. EMERGENCY CONTACTS TABLE
-- Relational table storing primary and additional emergency contacts per user
-- -----------------------------------------------------------------------------
CREATE TABLE emergency_contacts (
    contact_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    contact_name VARCHAR(150) NOT NULL,
    relationship VARCHAR(50) NOT NULL,
    phone VARCHAR(25) NOT NULL,
    is_primary TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contacts_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_contact_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 4. HOSPITALS TABLE
-- Directory of authorized regional emergency hospitals & trauma centers
-- Clearly marked sample data for demonstration
-- -----------------------------------------------------------------------------
CREATE TABLE hospitals (
    hospital_id INT AUTO_INCREMENT PRIMARY KEY,
    hospital_code VARCHAR(50) NOT NULL UNIQUE,
    hospital_name VARCHAR(150) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    emergency_helpline VARCHAR(25) NOT NULL,
    ambulance_phone VARCHAR(25) NOT NULL,
    doctor_in_charge VARCHAR(150),
    is_sample_data TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 5. HOSPITAL STAFF / RESPONDERS TABLE
-- Authorized medical personnel credentials for the Hospital Portal
-- -----------------------------------------------------------------------------
CREATE TABLE hospital_staff (
    staff_id INT AUTO_INCREMENT PRIMARY KEY,
    hospital_id INT NOT NULL,
    staff_code VARCHAR(50) NOT NULL UNIQUE,
    staff_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'Emergency Coordinator',
    is_sample_data TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_hospital FOREIGN KEY (hospital_id) REFERENCES hospitals(hospital_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 6. EMERGENCY REPORTS TABLE
-- Stores incident reports for Medical, Fire, Accident, Crime, Disaster, Other
-- -----------------------------------------------------------------------------
CREATE TABLE emergency_reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    report_tracking_id VARCHAR(50) NOT NULL UNIQUE,
    category ENUM('Medical', 'Fire', 'Accident', 'Crime', 'Disaster', 'Other') NOT NULL,
    sub_category VARCHAR(100) NOT NULL,
    severity ENUM('Low', 'Medium', 'High', 'Critical') DEFAULT 'High',
    description TEXT NOT NULL,
    incident_location VARCHAR(255) NOT NULL,
    latitude DECIMAL(10, 8) NULL,
    longitude DECIMAL(11, 8) NULL,
    injured_condition VARCHAR(100) DEFAULT 'Unknown',
    vehicles_involved VARCHAR(150) DEFAULT 'N/A',
    is_anonymous TINYINT(1) DEFAULT 0,
    status ENUM('Reported', 'Hospital Notified', 'Ambulance Dispatched', 'Police Dispatched', 'Fire Crew Dispatched', 'Resolved', 'Under Investigation') DEFAULT 'Reported',
    assigned_hospital_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    CONSTRAINT fk_report_hospital FOREIGN KEY (assigned_hospital_id) REFERENCES hospitals(hospital_id) ON DELETE SET NULL,
    INDEX idx_report_user (user_id),
    INDEX idx_report_category (category),
    INDEX idx_report_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 7. REPORT ATTACHMENTS TABLE
-- Secure metadata storage for incident photo uploads
-- -----------------------------------------------------------------------------
CREATE TABLE report_attachments (
    attachment_id INT AUTO_INCREMENT PRIMARY KEY,
    report_id INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attachment_report FOREIGN KEY (report_id) REFERENCES emergency_reports(report_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 8. NOTIFICATION RECORDS TABLE
-- Audit log of in-website notifications generated for contacts & hospital desks
-- -----------------------------------------------------------------------------
CREATE TABLE notification_records (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    report_id INT NOT NULL,
    recipient_type ENUM('Emergency Contact', 'Hospital Authority', 'Police Control', 'Fire Dispatch') NOT NULL,
    recipient_name VARCHAR(150) NOT NULL,
    recipient_phone VARCHAR(25) NOT NULL,
    message_content TEXT NOT NULL,
    delivery_status ENUM('In-Website Logged', 'Simulated Gateway Sent', 'Pending External Gateway') DEFAULT 'In-Website Logged',
    gateway_note VARCHAR(255) DEFAULT 'In-website notification logged. External SMS/VoIP gateways require third-party API configuration.',
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_report FOREIGN KEY (report_id) REFERENCES emergency_reports(report_id) ON DELETE CASCADE,
    INDEX idx_notification_report (report_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- SAMPLE SEED DATA
-- Clearly marked sample data for demonstration and testing purposes
-- =============================================================================

-- Sample Hospitals (Demo facilities)
INSERT INTO hospitals (hospital_code, hospital_name, address, city, emergency_helpline, ambulance_phone, doctor_in_charge, is_sample_data) VALUES
('HOSP-001', 'City Care Trauma & Emergency Hospital (Sample)', '108 Health Blvd, Central Zone', 'Metro City', '+1-800-555-0191', '+1-800-555-0199', 'Dr. Sarah Mitchell, MD', 1),
('HOSP-002', 'Apex Lifeline Multispecialty Hospital (Sample)', '452 Ring Road, North District', 'Metro City', '+1-800-555-0144', '+1-800-555-0149', 'Dr. Arvind Rao, MS', 1),
('HOSP-003', 'Sunrise Memorial Emergency Care (Sample)', '12 Ocean Drive, South Harbor', 'Metro City', '+1-800-555-0177', '+1-800-555-0179', 'Dr. Elena Rostova, MD', 1);

-- Sample Hospital Staff (Password for both demo accounts is: hospital123)
INSERT INTO hospital_staff (hospital_id, staff_code, staff_name, email, password_hash, role, is_sample_data) VALUES
(1, 'STAFF-101', 'Nurse Supervisor Jane Doe (Demo Staff)', 'jane.doe@citycarehospital.sample', 'hospital123', 'Trauma Coordinator', 1),
(2, 'STAFF-102', 'Dr. Mark Bennett (Demo Staff)', 'mark.bennett@apexlifeline.sample', 'hospital123', 'Emergency Physician', 1);

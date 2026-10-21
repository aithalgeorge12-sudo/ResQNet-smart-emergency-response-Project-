package com.resqnet.service;

import com.resqnet.dao.EmergencyContactDao;
import com.resqnet.dao.HospitalDao;
import com.resqnet.dao.NotificationDao;
import com.resqnet.dao.UserDao;
import com.resqnet.model.EmergencyContact;
import com.resqnet.model.EmergencyReport;
import com.resqnet.model.Hospital;
import com.resqnet.model.NotificationRecord;
import com.resqnet.model.User;

/**
 * Service managing emergency notifications, preparing alert payloads for contacts and hospitals,
 * and maintaining audit trails.
 *
 * NOTE FOR PRODUCTION DEPLOYMENT:
 * External SMS, WhatsApp, or Automated Voice dispatch requires integrating a third-party gateway
 * (such as Twilio, AWS SNS, or Sinch) with valid API credentials. This in-website system logs all
 * outgoing alerts to MySQL and prepares them for immediate external dispatch.
 */
public class NotificationService {

    private final NotificationDao notificationDao = new NotificationDao();
    private final EmergencyContactDao contactDao = new EmergencyContactDao();
    private final HospitalDao hospitalDao = new HospitalDao();
    private final UserDao userDao = new UserDao();

    /**
     * Prepares and logs notifications for the user's primary emergency contact and the nearest hospital.
     */
    public void dispatchEmergencyAlerts(EmergencyReport report) {
        if (report == null || report.getReportId() <= 0) return;

        User reporter = null;
        if (report.getUserId() != null && report.getUserId() > 0) {
            reporter = userDao.getUserById(report.getUserId());
        }

        String reporterName = (reporter != null && !report.isAnonymous()) ? reporter.getFullName() : "Anonymous Citizen";
        String bloodGroup = (reporter != null && reporter.getMedicalDetails() != null) ? reporter.getMedicalDetails().getBloodGroup() : "Unknown";
        String medicalNotes = (reporter != null && reporter.getMedicalDetails() != null) ?
                "Allergies: " + reporter.getMedicalDetails().getAllergies() + ", Diseases: " + reporter.getMedicalDetails().getDiseases() : "None reported";

        // 1. Notify Primary Emergency Contact
        if (reporter != null) {
            EmergencyContact contact = contactDao.getPrimaryContact(reporter.getUserId());
            if (contact != null) {
                String contactMsg = String.format(
                        "URGENT ALERT: %s has submitted an emergency report [%s - %s] at %s. Condition: %s. ResQNet Tracking: %s. Please reach out immediately.",
                        reporterName, report.getCategory(), report.getSubCategory(), report.getIncidentLocation(),
                        report.getInjuredCondition(), report.getReportTrackingId()
                );

                NotificationRecord notifContact = new NotificationRecord(
                        report.getReportId(),
                        "Emergency Contact",
                        contact.getContactName() + " (" + contact.getRelationship() + ")",
                        contact.getPhone(),
                        contactMsg,
                        "In-Website Logged",
                        "Notification queued for emergency contact. (External SMS dispatch requires Twilio/SNS API setup)."
                );
                notificationDao.logNotification(notifContact);
            }
        }

        // 2. Notify Assigned Hospital or Regional Response Desk
        Hospital hospital = null;
        if (report.getAssignedHospitalId() != null && report.getAssignedHospitalId() > 0) {
            hospital = hospitalDao.getHospitalById(report.getAssignedHospitalId());
        } else {
            // Pick default trauma center
            var hospitals = hospitalDao.getAllHospitals();
            if (!hospitals.isEmpty()) {
                hospital = hospitals.get(0);
                report.setAssignedHospitalId(hospital.getHospitalId());
            }
        }

        if (hospital != null) {
            String hospitalMsg = String.format(
                    "EMERGENCY INTAKE: %s Incident [%s] at %s. Reporter: %s. Blood Group: %s. Medical Details: %s. Tracking Ref: %s.",
                    report.getCategory(), report.getSubCategory(), report.getIncidentLocation(),
                    reporterName, bloodGroup, medicalNotes, report.getReportTrackingId()
            );

            NotificationRecord notifHospital = new NotificationRecord(
                    report.getReportId(),
                    "Hospital Authority",
                    hospital.getHospitalName(),
                    hospital.getEmergencyHelpline(),
                    hospitalMsg,
                    "In-Website Logged",
                    "Dispatched to hospital trauma intake queue in Hospital Portal."
            );
            notificationDao.logNotification(notifHospital);
        }
    }
}

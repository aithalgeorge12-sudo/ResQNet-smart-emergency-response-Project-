package com.resqnet.controller;

import com.resqnet.dao.EmergencyContactDao;
import com.resqnet.dao.HospitalDao;
import com.resqnet.dao.UserDao;
import com.resqnet.model.EmergencyContact;
import com.resqnet.model.EmergencyReport;
import com.resqnet.model.Hospital;
import com.resqnet.model.User;
import com.resqnet.service.EmergencyService;
import com.resqnet.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller managing the Medical Emergency module with first-aid guidance,
 * click-to-call, location capture, and incident reporting.
 */
@WebServlet("/emergency/medical")
public class MedicalEmergencyServlet extends HttpServlet {

    private final EmergencyService emergencyService = new EmergencyService();
    private final UserDao userDao = new UserDao();
    private final EmergencyContactDao contactDao = new EmergencyContactDao();
    private final HospitalDao hospitalDao = new HospitalDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (session != null && session.getAttribute("userId") != null) ?
                (Integer) session.getAttribute("userId") : null;

        if (userId != null) {
            User user = userDao.getUserById(userId);
            EmergencyContact primaryContact = contactDao.getPrimaryContact(userId);
            request.setAttribute("user", user);
            request.setAttribute("primaryContact", primaryContact);
        }

        List<Hospital> hospitals = hospitalDao.getAllHospitals();
        request.setAttribute("hospitals", hospitals);

        // Optional pre-selected subcategory via query parameter (e.g. ?type=snake-bite)
        String subType = request.getParameter("type");
        request.setAttribute("selectedType", subType != null ? subType : "heart-attack");

        request.getRequestDispatcher("/emergency-medical.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Integer userId = (session != null && session.getAttribute("userId") != null) ?
                (Integer) session.getAttribute("userId") : null;

        String subCategory = request.getParameter("subCategory");
        String severity = request.getParameter("severity");
        String description = request.getParameter("description");
        String location = request.getParameter("location");
        String latStr = request.getParameter("latitude");
        String lngStr = request.getParameter("longitude");
        String patientCondition = request.getParameter("patientCondition");
        int hospitalId = ValidationUtil.parseInt(request.getParameter("hospitalId"), 0);

        try {
            EmergencyReport report = new EmergencyReport();
            report.setUserId(userId);
            report.setCategory("Medical");
            report.setSubCategory(ValidationUtil.isNotEmpty(subCategory) ? subCategory : "Medical Emergency");
            report.setSeverity(ValidationUtil.isNotEmpty(severity) ? severity : "Critical");
            report.setDescription(description);
            report.setIncidentLocation(location);
            report.setInjuredCondition(ValidationUtil.isNotEmpty(patientCondition) ? patientCondition : "Unstable");

            if (ValidationUtil.isNotEmpty(latStr) && ValidationUtil.isNotEmpty(lngStr)) {
                report.setLatitude(new BigDecimal(latStr));
                report.setLongitude(new BigDecimal(lngStr));
            }
            if (hospitalId > 0) {
                report.setAssignedHospitalId(hospitalId);
            }

            String uploadDir = getServletContext().getRealPath("/uploads");
            EmergencyReport savedReport = emergencyService.submitReport(report, null, uploadDir);

            request.setAttribute("report", savedReport);
            request.setAttribute("successMessage", "Medical emergency report logged successfully! Tracking ID: " + savedReport.getReportTrackingId());
            request.getRequestDispatcher("/my-reports.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to submit report: " + e.getMessage());
            doGet(request, response);
        }
    }
}

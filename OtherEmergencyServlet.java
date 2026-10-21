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
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller handling General and Uncategorized Emergencies with photo upload support.
 */
@WebServlet("/emergency/other")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class OtherEmergencyServlet extends HttpServlet {

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

        request.getRequestDispatcher("/emergency-other.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Integer userId = (session != null && session.getAttribute("userId") != null) ?
                (Integer) session.getAttribute("userId") : null;

        String emergencyType = request.getParameter("emergencyType");
        String severity = request.getParameter("severity");
        String description = request.getParameter("description");
        String location = request.getParameter("location");
        String latStr = request.getParameter("latitude");
        String lngStr = request.getParameter("longitude");
        String callerContact = request.getParameter("callerContact");

        Part filePart = null;
        try {
            filePart = request.getPart("photo");
        } catch (Exception ignored) {}

        try {
            EmergencyReport report = new EmergencyReport();
            report.setUserId(userId);
            report.setCategory("Other");
            report.setSubCategory(ValidationUtil.isNotEmpty(emergencyType) ? emergencyType : "General Emergency");
            report.setSeverity(ValidationUtil.isNotEmpty(severity) ? severity : "High");
            report.setDescription("Caller Phone: " + (callerContact != null ? callerContact : "N/A") + ". " + description);
            report.setIncidentLocation(location);

            if (ValidationUtil.isNotEmpty(latStr) && ValidationUtil.isNotEmpty(lngStr)) {
                report.setLatitude(new BigDecimal(latStr));
                report.setLongitude(new BigDecimal(lngStr));
            }

            String uploadDir = getServletContext().getRealPath("/uploads");
            EmergencyReport savedReport = emergencyService.submitReport(report, filePart, uploadDir);

            request.setAttribute("report", savedReport);
            request.setAttribute("successMessage", "Emergency report registered successfully! Tracking ID: " + savedReport.getReportTrackingId());
            request.getRequestDispatcher("/my-reports.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to submit report: " + e.getMessage());
            doGet(request, response);
        }
    }
}

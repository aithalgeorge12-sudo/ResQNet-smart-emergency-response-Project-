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
 * Controller handling Accident Emergencies with photo upload, GPS coordinates,
 * emergency contact integration, and hospital trauma dispatch.
 */
@WebServlet("/emergency/accident")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class AccidentEmergencyServlet extends HttpServlet {

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
            List<EmergencyContact> allContacts = contactDao.getContactsByUserId(userId);
            request.setAttribute("user", user);
            request.setAttribute("primaryContact", primaryContact);
            request.setAttribute("allContacts", allContacts);
        }

        List<Hospital> hospitals = hospitalDao.getAllHospitals();
        request.setAttribute("hospitals", hospitals);

        request.getRequestDispatcher("/emergency-accident.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Integer userId = (session != null && session.getAttribute("userId") != null) ?
                (Integer) session.getAttribute("userId") : null;

        String accidentType = request.getParameter("accidentType");
        String severity = request.getParameter("severity");
        String description = request.getParameter("description");
        String location = request.getParameter("location");
        String latStr = request.getParameter("latitude");
        String lngStr = request.getParameter("longitude");
        String injuredCondition = request.getParameter("injuredCondition");
        String vehiclesInvolved = request.getParameter("vehiclesInvolved");
        int hospitalId = ValidationUtil.parseInt(request.getParameter("hospitalId"), 0);

        Part filePart = null;
        try {
            filePart = request.getPart("accidentImage");
        } catch (Exception ignored) {}

        try {
            EmergencyReport report = new EmergencyReport();
            report.setUserId(userId);
            report.setCategory("Accident");
            report.setSubCategory(ValidationUtil.isNotEmpty(accidentType) ? accidentType : "Road Accident");
            report.setSeverity(ValidationUtil.isNotEmpty(severity) ? severity : "Critical");
            report.setDescription(description);
            report.setIncidentLocation(location);
            report.setInjuredCondition(ValidationUtil.isNotEmpty(injuredCondition) ? injuredCondition : "Critical Condition");
            report.setVehiclesInvolved(ValidationUtil.isNotEmpty(vehiclesInvolved) ? vehiclesInvolved : "Single Vehicle");

            if (ValidationUtil.isNotEmpty(latStr) && ValidationUtil.isNotEmpty(lngStr)) {
                report.setLatitude(new BigDecimal(latStr));
                report.setLongitude(new BigDecimal(lngStr));
            }
            if (hospitalId > 0) {
                report.setAssignedHospitalId(hospitalId);
            }

            String uploadDir = getServletContext().getRealPath("/uploads");
            EmergencyReport savedReport = emergencyService.submitReport(report, filePart, uploadDir);

            request.setAttribute("report", savedReport);
            request.setAttribute("successMessage", "Accident report submitted successfully! In-website notification alert queued for emergency contacts & hospital. Tracking ID: " + savedReport.getReportTrackingId());
            request.getRequestDispatcher("/my-reports.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to submit accident report: " + e.getMessage());
            doGet(request, response);
        }
    }
}

package com.resqnet.controller;

import com.resqnet.dao.EmergencyContactDao;
import com.resqnet.dao.EmergencyReportDao;
import com.resqnet.dao.HospitalDao;
import com.resqnet.dao.UserDao;
import com.resqnet.model.EmergencyContact;
import com.resqnet.model.EmergencyReport;
import com.resqnet.model.Hospital;
import com.resqnet.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller serving the central emergency command dashboard for logged-in users.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final EmergencyContactDao contactDao = new EmergencyContactDao();
    private final EmergencyReportDao reportDao = new EmergencyReportDao();
    private final HospitalDao hospitalDao = new HospitalDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=auth-required");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        // Always load latest data fresh from MySQL
        User freshUser = userDao.getUserById(userId);
        if (freshUser == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Update session copy
        session.setAttribute("loggedInUser", freshUser);
        session.setAttribute("userName", freshUser.getFullName());

        EmergencyContact primaryContact = contactDao.getPrimaryContact(userId);
        List<EmergencyContact> allContacts = contactDao.getContactsByUserId(userId);
        List<EmergencyReport> userReports = reportDao.getReportsByUserId(userId);
        List<Hospital> hospitals = hospitalDao.getAllHospitals();

        request.setAttribute("user", freshUser);
        request.setAttribute("primaryContact", primaryContact);
        request.setAttribute("allContacts", allContacts);
        request.setAttribute("userReports", userReports);
        request.setAttribute("hospitals", hospitals);

        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}

package com.resqnet.controller;

import com.resqnet.dao.EmergencyReportDao;
import com.resqnet.model.EmergencyReport;
import com.resqnet.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller displaying incident report history, tracking status, and notification audit logs.
 */
@WebServlet("/my-reports")
public class MyReportsServlet extends HttpServlet {

    private final EmergencyReportDao reportDao = new EmergencyReportDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (session != null && session.getAttribute("userId") != null) ?
                (Integer) session.getAttribute("userId") : null;

        String idParam = request.getParameter("id");
        if (ValidationUtil.isNotEmpty(idParam)) {
            int reportId = ValidationUtil.parseInt(idParam, 0);
            if (reportId > 0) {
                EmergencyReport report = reportDao.getReportById(reportId);
                // Security check: if logged in user, only view own report (unless hospital staff)
                if (report != null && (userId == null || report.getUserId() == null || report.getUserId().equals(userId))) {
                    request.setAttribute("report", report);
                }
            }
        }

        if (userId != null) {
            List<EmergencyReport> userReports = reportDao.getReportsByUserId(userId);
            request.setAttribute("userReports", userReports);
        }

        request.getRequestDispatcher("/my-reports.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

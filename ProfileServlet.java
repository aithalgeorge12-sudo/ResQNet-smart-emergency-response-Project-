package com.resqnet.controller;

import com.resqnet.dao.EmergencyContactDao;
import com.resqnet.dao.UserDao;
import com.resqnet.model.EmergencyContact;
import com.resqnet.model.User;
import com.resqnet.model.UserMedicalDetails;
import com.resqnet.service.UserService;
import com.resqnet.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller managing User Profile viewing and updating ("My Details" section).
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final UserDao userDao = new UserDao();
    private final EmergencyContactDao contactDao = new EmergencyContactDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=auth-required");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        User user = userDao.getUserById(userId);
        List<EmergencyContact> contacts = contactDao.getContactsByUserId(userId);

        request.setAttribute("user", user);
        request.setAttribute("contacts", contacts);
        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=auth-required");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        User existingUser = userDao.getUserById(userId);
        if (existingUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Extract updated profile fields
        String fullName = request.getParameter("fullName");
        String dobStr = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        // Extract updated medical fields
        String bloodGroup = request.getParameter("bloodGroup");
        String diseases = request.getParameter("diseases");
        String medicalConditions = request.getParameter("medicalConditions");
        String allergies = request.getParameter("allergies");
        String currentMedications = request.getParameter("currentMedications");
        String doctorName = request.getParameter("doctorName");
        String doctorPhone = request.getParameter("doctorPhone");
        String otherMedicalInfo = request.getParameter("otherMedicalInfo");

        LocalDate dob = ValidationUtil.parseDate(dobStr);
        if (dob != null) {
            existingUser.setDob(dob);
        }
        if (ValidationUtil.isNotEmpty(fullName)) {
            existingUser.setFullName(fullName);
        }
        if (ValidationUtil.isNotEmpty(gender)) {
            existingUser.setGender(gender);
        }
        if (ValidationUtil.isValidPhone(phone)) {
            existingUser.setPhone(phone);
        }
        if (ValidationUtil.isNotEmpty(address)) {
            existingUser.setAddress(address);
        }

        UserMedicalDetails medical = existingUser.getMedicalDetails();
        if (medical == null) {
            medical = new UserMedicalDetails();
            medical.setUserId(userId);
        }
        medical.setBloodGroup(bloodGroup);
        medical.setDiseases(diseases);
        medical.setMedicalConditions(medicalConditions);
        medical.setAllergies(allergies);
        medical.setCurrentMedications(currentMedications);
        medical.setConsultingDoctorName(doctorName);
        medical.setConsultingDoctorPhone(doctorPhone);
        medical.setOtherMedicalInfo(otherMedicalInfo);

        boolean updated = userService.updateProfile(existingUser, medical);

        if (updated) {
            session.setAttribute("userName", existingUser.getFullName());
            request.setAttribute("successMessage", "Your profile and medical details have been updated successfully!");
        } else {
            request.setAttribute("errorMessage", "Failed to update profile. Please verify your entries.");
        }

        User refreshedUser = userDao.getUserById(userId);
        List<EmergencyContact> contacts = contactDao.getContactsByUserId(userId);
        request.setAttribute("user", refreshedUser);
        request.setAttribute("contacts", contacts);
        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }
}

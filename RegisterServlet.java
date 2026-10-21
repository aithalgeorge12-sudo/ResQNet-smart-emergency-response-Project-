package com.resqnet.controller;

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

import java.io.IOException;
import java.time.LocalDate;

/**
 * Controller handling user registration workflow.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // 1. Account Details
        String fullName = request.getParameter("fullName");
        String dobStr = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // 2. Medical Details
        String bloodGroup = request.getParameter("bloodGroup");
        String diseases = request.getParameter("diseases");
        String medicalConditions = request.getParameter("medicalConditions");
        String allergies = request.getParameter("allergies");
        String currentMedications = request.getParameter("currentMedications");
        String doctorName = request.getParameter("doctorName");
        String doctorPhone = request.getParameter("doctorPhone");
        String otherMedicalInfo = request.getParameter("otherMedicalInfo");

        // 3. Primary Emergency Contact
        String contactName = request.getParameter("contactName");
        String relationship = request.getParameter("relationship");
        String contactPhone = request.getParameter("contactPhone");

        // 4. Optional Additional Emergency Contact
        String contact2Name = request.getParameter("contact2Name");
        String contact2Relationship = request.getParameter("contact2Relationship");
        String contact2Phone = request.getParameter("contact2Phone");

        LocalDate dob = ValidationUtil.parseDate(dobStr);

        User user = new User();
        user.setFullName(fullName);
        user.setDob(dob);
        user.setGender(gender);
        user.setPhone(phone);
        user.setEmail(email);
        user.setAddress(address);

        UserMedicalDetails medical = new UserMedicalDetails();
        medical.setBloodGroup(bloodGroup);
        medical.setDiseases(diseases);
        medical.setMedicalConditions(medicalConditions);
        medical.setAllergies(allergies);
        medical.setCurrentMedications(currentMedications);
        medical.setConsultingDoctorName(doctorName);
        medical.setConsultingDoctorPhone(doctorPhone);
        medical.setOtherMedicalInfo(otherMedicalInfo);

        EmergencyContact primaryContact = new EmergencyContact();
        primaryContact.setContactName(contactName);
        primaryContact.setRelationship(relationship);
        primaryContact.setPhone(contactPhone);
        primaryContact.setPrimary(true);

        EmergencyContact secondaryContact = null;
        if (ValidationUtil.isNotEmpty(contact2Name) && ValidationUtil.isNotEmpty(contact2Phone)) {
            secondaryContact = new EmergencyContact();
            secondaryContact.setContactName(contact2Name);
            secondaryContact.setRelationship(contact2Relationship);
            secondaryContact.setPhone(contact2Phone);
            secondaryContact.setPrimary(false);
        }

        String result = userService.registerUser(user, medical, primaryContact, secondaryContact, password, confirmPassword);

        if ("SUCCESS".equals(result)) {
            response.sendRedirect(request.getContextPath() + "/login?success=registered");
        } else {
            // Re-populate submitted values for user convenience
            request.setAttribute("errorMessage", result);
            request.setAttribute("user", user);
            request.setAttribute("dobStr", dobStr);
            request.setAttribute("medical", medical);
            request.setAttribute("primaryContact", primaryContact);
            request.setAttribute("secondaryContact", secondaryContact);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}

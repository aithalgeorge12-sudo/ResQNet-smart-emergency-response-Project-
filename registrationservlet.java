import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("name");
        String age = request.getParameter("age");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String bloodGroup = request.getParameter("bloodGroup");
        String emergencyContact = request.getParameter("emergencyContact");
        String diseases = request.getParameter("diseases");
        String allergies = request.getParameter("allergies");
        String medications = request.getParameter("medications");
        String doctor = request.getParameter("consultingDoctor");

        try {

            Connection con = VConnection.getConnection();

            String sql = "INSERT INTO registration "
                    + "(full_name, age, phone_number, email_address, full_address, "
                    + "blood_group, emergency_contact, diseases, allergies, "
                    + "medications, doctor) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setInt(2, Integer.parseInt(age));
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setString(5, address);
            ps.setString(6, bloodGroup);
            ps.setString(7, emergencyContact);
            ps.setString(8, diseases);
            ps.setString(9, allergies);
            ps.setString(10, medications);
            ps.setString(11, doctor);

            int result = ps.executeUpdate();

            if (result > 0) {
                out.println("<h2>Registration Successful!</h2>");
            }

            ps.close();
            con.close();

        } catch (Exception e) {
            out.println("<h2>Error: " + e.getMessage() + "</h2>");
        }
    }
}
package com.codewithz;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.post.db.DataBaseConnection;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/register")
public class signupservlet extends HttpServlet {
    public  void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hashedPassword);
            ps.executeUpdate();
            out.print("{\"success\": true}");
        } catch (Exception e) {
            out.print("{\"success\": false}");
        }
    }


}

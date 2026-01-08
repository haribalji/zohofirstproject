package com.codewithz;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.post.db.dbconnection;
import java.sql.*;
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println("it is  login  called");
        String sql = "SELECT * FROM users WHERE username=? AND password=?";

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
//               the query will pre-complied
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");

                HttpSession session = request.getSession(true);
                session.setAttribute("userId", userId);

                System.out.println("LOGIN SESSION ID: " + session.getId());

                System.out.println(userId);
//                response.getWriter().println("Login successful");
                out.print("{\"success\": true}");
            } else {
                out.print("{\"success\": false}");
//                response.getWriter().println("Invalid credentials");
            }
        } catch (SQLException e) {
            throw new ServletException("Login error", e);
        }
    }
}

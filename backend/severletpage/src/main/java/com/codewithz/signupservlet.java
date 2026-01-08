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

@WebServlet("/register")
public class signupservlet extends HttpServlet {


    public  void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("welcome to signup");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String sql = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
//                                                           ?-->        positional arguments
        try (Connection con = dbconnection.getConnection();

             PreparedStatement ps = con.prepareStatement(sql)) {
//    it prepares the sql query for the send into database
            System.out.println("hi everyone");

//      it will sent once and values sent back,
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();

            out.print("{\"success\": true}");


        } catch (Exception e) {
            out.print("{\"success\": false}");
//           throw new ServletException("Signup failed", e);
        }
    }


}

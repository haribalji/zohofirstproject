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
import java.sql.*;
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//ServletException it is used to handle any servelt related issues
        response.setContentType("application/json");
        //setting http header so receriver(client) understand the format of data
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String sql = "SELECT * FROM users WHERE username=?" ;
         try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (
            rs.next()) {
//                first you need to move next
                String storedHash = rs.getString("password");
                boolean match = BCrypt.checkpw(password, storedHash);
              if(match){
//                  ok the user is exists
                  int userId = rs.getInt("id");
                  HttpSession session = request.getSession(true);
                  session.setAttribute("userId", userId);
                  out.print("{\"success\": true}");
//                  JSON data into the HTTP response body
              }
              else {
                  out.print("{\"success\": false}");//here we sending the response
              }
            }
            else{
                //is is the situation where the username not found
                out.print("{\"success\": false}");
            }
        } catch (Exception e) {

            throw new ServletException("Login error", e);


        }
    }
}

package com.codewithz;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.post.db.dbconnection;
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
        System.out.println("it is  login  called");

        String sql = "SELECT * FROM users WHERE username=?" ;
        System.out.println("is it login");

         try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
//            System.out.println("data fetched correctly");

//               the query will pre-complied
             ps.setString(1, username);
            System.out.println("data fetched correctly");
            ResultSet rs = ps.executeQuery();
//          bcrypt function working
//this function first collect the alogo,then salt then cf  using this applied in user input password the same rounds of hash and
//            password and if both string match then it return true other wise false
//            rs.next() check next row is exists or not if yes it will move to next row
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
                  System.out.println("LOGIN SESSION ID: " + session.getId());
                  System.out.println(userId);
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
//here it will create serveltexecption to tell the tomcat
// That error is occured then it stops the request process
// and sets the status code with custom messages  and send to browser

//            diff between exeception vs ServletException
//            as normal exeception cannot inform the tomcat.
//            but serveltexecption   does it
//            it prints the log in servelt

        }
//        Weakrefernece<String> wr=new Weakrefernce<>(new String("java"));
    }
}

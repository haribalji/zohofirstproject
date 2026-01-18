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
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
//bcrypt is password hash algo added salted along with it
//BCrypt.gensalt(10) it is used to provide the salt random and by default the cf=10 only
//        so 2^10 time s the hash will be performed again and again
//given string converting a password into an unreadable fixed string that cannot be reversed

//        first get the passsword +salt it hashing for  2^cost factor
        String sql = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
//                                                           ?-->        positional arguments
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
//    it prepares the sql query for the send into database
//      it will sent once and values sent back,
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

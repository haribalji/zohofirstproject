package com.codewithz;
import com.post.db.dbconnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/add-comment")
public class Addcomment extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        System.out.println("lets begin the adding comment");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
//            they did not have valid permission
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        System.out.println("lets begin the stat comment");

        int userId = (int) session.getAttribute("userId");
        int postId = Integer.parseInt(req.getParameter("postId"));
        String comment = req.getParameter("comment");
        System.out.println(userId);
        System.out.println(comment);

        String sql = "INSERT INTO comments (post_id, user_id, comment) VALUES (?, ?, ?)";

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ps.setInt(2, userId);
            ps.setString(3, comment);
            ps.executeUpdate();

            res.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {

            e.printStackTrace();
//            raise the server error
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}


package com.codewithz;
import com.post.db.DataBaseConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
@WebServlet("/add-comment")
public class Addcomment extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = (int) session.getAttribute("userId");
        int postId = Integer.parseInt(req.getParameter("postId"));
        String comment = req.getParameter("comment");


        String sql = "INSERT INTO comments (post_id, user_id, comment) VALUES (?, ?, ?)";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ps.setInt(2, userId);
            ps.setString(3, comment);
            ps.executeUpdate();

            res.setStatus(HttpServletResponse.SC_OK);//all ok 200

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }
    }
}


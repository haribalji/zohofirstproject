package com.codewithz;


import com.post.db.dbconnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/delete-comment")
public class Deletecomment extends HttpServlet {


    public void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        int userId = (int) session.getAttribute("userId");
        int commentId = Integer.parseInt(req.getParameter("commentId"));
        String sql = "DELETE FROM comments WHERE id=? AND user_id=?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.setInt(2, userId);
            int deleted = ps.executeUpdate();

            if (deleted == 0) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {
                res.setStatus(HttpServletResponse.SC_OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

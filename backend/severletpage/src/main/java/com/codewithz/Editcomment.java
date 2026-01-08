package com.codewithz;
import com.post.db.dbconnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/edit-comment")
public class Editcomment extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = (int) session.getAttribute("userId");
        int commentId = Integer.parseInt(req.getParameter("commentId"));
        String comment = req.getParameter("comment");

        String sql = "UPDATE comments SET comment=? WHERE id=? AND user_id=?";
//                id is comment id
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, comment);
            ps.setInt(2, commentId);
            ps.setInt(3, userId);

            int updated = ps.executeUpdate();

            if (updated == 0) {
//                accept the request but not have access to edit the resouces
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {
//                ok status
                res.setStatus(HttpServletResponse.SC_OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

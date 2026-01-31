package com.codewithz;
import com.post.db.DataBaseConnection;
import jakarta.servlet.ServletException;
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
            //Not  logined in,the staus code 400 series
            return;
        }
        int userId = (int) session.getAttribute("userId");
        int commentId = Integer.parseInt(req.getParameter("commentId"));
        String sql = "DELETE FROM comments WHERE id=? AND user_id=?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.setInt(2, userId);
            int deleted = ps.executeUpdate();//return no of rows deleted

            if (deleted == 0) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {
                res.setStatus(HttpServletResponse.SC_OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

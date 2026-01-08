package com.codewithz;
import com.post.db.dbconnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/delete-post")
public class Deletepost extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        // 🔐 Check session
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            return;
        }

        int userId = (int) session.getAttribute("userId");
        int postId = Integer.parseInt(req.getParameter("id"));

        String sql = "DELETE FROM posts WHERE id=? AND user_id=?";

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ps.setInt(2, userId);

            int deleted = ps.executeUpdate();//return no of row is deleted

            if (deleted == 0) {
                //  Post exists but not belogs to user
//
                res.setStatus(403); // 403 accepted the request but not able to give authorize
            } else {
                res.setStatus(200); // 200 all ok
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
        }
    }
}

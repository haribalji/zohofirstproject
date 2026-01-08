package com.codewithz;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.post.db.dbconnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/edit-post")
public class Edit_post extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        System.out.println("editting is stated");
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.setStatus(401);
            return;
        }

        int userId = (int) session.getAttribute("userId");
        System.out.println(userId);
        int postId = Integer.parseInt(req.getParameter("postId"));
        String caption = req.getParameter("caption");
        System.out.println(postId);
        System.out.println(caption);
        String sql = "UPDATE posts SET caption=? WHERE id=? AND user_id=?";

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, caption);
            ps.setInt(2, postId);
            ps.setInt(3, userId);

            int updated = ps.executeUpdate();
            System.out.println("editting is updated");

            if (updated == 0) {
                res.setStatus(403); //  not owner
            }

        } catch (Exception e) {
            System.out.println("error");
//            e.printStackTrace();
        }
    }
}

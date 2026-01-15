package com.codewithz;
import jakarta.servlet.ServletException;
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
            throws IOException ,ServletException{
        System.out.println("editting is stated");
        HttpSession session = req.getSession(false);//to get seession data without creating
        // ,old one getting
        if (session == null) {
            res.setStatus(401);//not logined
            return;
        }
        int userId = (int) session.getAttribute("userId");
        int postId = Integer.parseInt(req.getParameter("postId"));
        String caption = req.getParameter("caption");
        String sql = "UPDATE posts SET caption=? WHERE id=? AND user_id=?";

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, caption);
            ps.setInt(2, postId);
            ps.setInt(3, userId);

            int updated = ps.executeUpdate();
            System.out.println("editting is updated");

            if (updated == 0) {
                res.setStatus(403); //  not owner or permission
            }

        } catch (Exception e) {
            e.printStackTrace();//error is printed by us
            throw new ServletException(" error", e);//here the error is printed by

        }
    }
}

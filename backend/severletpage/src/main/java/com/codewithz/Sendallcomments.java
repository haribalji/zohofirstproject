package com.codewithz;
import com.post.db.dbconnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;

@WebServlet("/comments")
public class Sendallcomments extends HttpServlet {


    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json");

        int postId = Integer.parseInt(req.getParameter("postId"));

        String sql =
                "SELECT c.id, c.comment, c.user_id, u.username " +
                        "FROM comments c JOIN users u ON c.user_id = u.id " +
                        "WHERE c.post_id=? ORDER BY c.id";

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            while (rs.next()) {
                json.append("{")
                        .append("\"id\":").append(rs.getInt("id")).append(",")
                        .append("\"comment\":\"").append(rs.getString("comment")).append("\",")
                        .append("\"user_id\":").append(rs.getInt("user_id")).append(",")
                        .append("\"username\":\"").append(rs.getString("username")).append("\"")
                        .append("},");
            }

            if (json.charAt(json.length() - 1) == ',')
                json.deleteCharAt(json.length() - 1);

            json.append("]");
            res.getWriter().print(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

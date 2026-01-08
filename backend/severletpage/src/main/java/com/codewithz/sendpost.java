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
import java.sql.ResultSet;

@WebServlet("/posts")
public class sendpost extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json");

        HttpSession session = req.getSession(false);
        if (session == null) {
            res.setStatus(401);
            return;
        }

        String sql =
                "SELECT p.id,p.user_id, p.image_path, p.caption, u.username " +
                        "FROM posts p JOIN users u ON p.user_id = u.id " +
                        "ORDER BY p.id DESC";

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder json = new StringBuilder("[");
            while (rs.next()) {
                json.append("{")
                        .append("\"id\":").append(rs.getInt("id")).append(",")
                        .append("\"user_id\":").append(rs.getInt("user_id")).append(",")
                        .append("\"image_path\":\"").append(rs.getString("image_path")).append("\",")
                        .append("\"caption\":\"").append(rs.getString("caption")).append("\",")
                        .append("\"username\":\"").append(rs.getString("username")).append("\"")
                        .append("},");
            }
            //to remove the waste comma
            if (json.charAt(json.length() - 1) == ',')
                json.deleteCharAt(json.length() - 1);

            json.append("]");//generally the json can't end with comma
            res.getWriter().print(json);

        } catch (Exception e) {
            System.out.println("error");

        }
    }
}


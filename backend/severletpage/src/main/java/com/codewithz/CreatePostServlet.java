//package com.codewithz;
//import com.post.db.dbconnection;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.MultipartConfig;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.Part;
//
//import java.io.File;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//@MultipartConfig
//@WebServlet("/createpost")
//public class CreatePostServlet extends HttpServlet {
//
//    public void doPost(HttpServletRequest req, HttpServletResponse res)
//            throws IOException, ServletException {
//         int userId=1;
//        Part image = req.getPart("image");
//        String caption = req.getParameter("caption");
//
//        String fileName = System.currentTimeMillis() + "_" + image.getSubmittedFileName();
//        String uploadPath = getServletContext().getRealPath("/uploads");
//
//        File dir = new File(uploadPath);
//        if (!dir.exists()) dir.mkdir();
//
//        image.write(uploadPath + "/" + fileName);
//
//        String imagePath = "uploads/" + fileName;
//
//
//        // save path in DB
//        String sql = "INSERT INTO posts(user_id, image_path, caption) VALUES (?,?,?)";
//
//        // JDBC insert (skip boilerplate)
//
//        try (Connection con = dbconnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//
//            ps.setInt(1, userId++);
//            ps.setString(2, imagePath);
//            ps.setString(3, caption);
//
//            ps.executeUpdate();   // 🔥 INSERT happens here
//
//            res.getWriter().print("{\"success\": true}");
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            res.getWriter().print("{\"success\": false}");
//        }
//
//
//
//    }
//}
//
//



package com.codewithz;

import com.post.db.dbconnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@MultipartConfig
@WebServlet("/createpost")
public class CreatePostServlet extends HttpServlet {


    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        res.setContentType("application/json");

        // 🔐 Get session
        HttpSession session = req.getSession(false);

        System.out.println("CREATEPOST SESSION: " + (session != null ? session.getId() : "null"));
        if (session == null || session.getAttribute("userId") == null) {
            System.out.println("creating post begin");

            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().print("{\"success\": false, \"msg\":\"Not logged in\"}");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        System.out.println(userId+"this  name");
        Part image = req.getPart("image");
        String caption = req.getParameter("caption");

        String fileName = System.currentTimeMillis() + "_" + image.getSubmittedFileName();
        String uploadPath = getServletContext().getRealPath("/uploads");

        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();

        image.write(uploadPath + File.separator + fileName);

        String imagePath = "uploads/" + fileName;

        String sql = "INSERT INTO posts (user_id, image_path, caption) VALUES (?, ?, ?)";

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, imagePath);
            ps.setString(3, caption);

            ps.executeUpdate();

            res.getWriter().print("{\"success\": true}");

        } catch (SQLException e) {
//            e.printStackTrace();
            res.getWriter().print("{\"success\": false}");
        }
    }
}

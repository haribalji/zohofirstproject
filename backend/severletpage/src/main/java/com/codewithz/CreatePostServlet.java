package com.codewithz;
import com.post.db.DataBaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@MultipartConfig
@WebServlet("/createpost")
public class CreatePostServlet extends HttpServlet {
public void doPost(HttpServletRequest req, HttpServletResponse res)throws IOException, ServletException {

        res.setContentType("application/json");
        //  here only we will get the  session
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
//            sending the response to client or browser
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().print("{\"success\": false, \"msg\":\"Not logged in\"}");
            return;
        }
        int userId = (int) session.getAttribute("userId");
        Part image = req.getPart("image");
        String caption = req.getParameter("caption");
        String fileName = System.currentTimeMillis() + "_" + image.getSubmittedFileName();
 String uploadPath = getServletContext().getRealPath("/uploads");

        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();
        image.write(uploadPath + File.separator + fileName);
   //it used for file in folder
        String imagePath = "uploads/" + fileName;

        String sql = "INSERT INTO posts (user_id, image_path, caption) VALUES (?, ?, ?)";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, imagePath);
            ps.setString(3, caption);

            ps.executeUpdate();

            res.getWriter().print("{\"success\": true}");

        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().print("{\"success\": false}");

        }
    }
}
